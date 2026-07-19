package io.horizontalsystems.thorchainkit.network

import com.google.gson.JsonObject
import io.horizontalsystems.thorchainkit.models.AccountInfo
import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.models.DenomBalance
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.net.URL
import java.util.Base64

class ThornodeApiProvider(
    baseUrls: List<URL>
) {

    private val apis: List<ThornodeApi> = baseUrls.map {
        Retrofit.Builder()
            .baseUrl(it.toString())
            .client(ApiClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ThornodeApi::class.java)
    }

    suspend fun fetchBalances(address: Address): List<DenomBalance> =
        withFailover { api ->
            api.balances(address.toString()).balances.map {
                DenomBalance(it.denom, BigInteger(it.amount))
            }
        }

    // null for accounts the chain has not seen yet (created on first receive)
    suspend fun fetchAccount(address: Address): AccountInfo? =
        withFailover { api ->
            try {
                parseAccountInfo(api.account(address.toString()).account)
            } catch (error: HttpException) {
                if (error.code() == 404) null else throw error
            }
        }

    suspend fun fetchNativeTxFee(): BigInteger =
        withFailover { api ->
            BigInteger(api.network().nativeTxFeeRune)
        }

    suspend fun fetchLastBlockHeight(): Long =
        withFailover { api ->
            api.lastBlock().first().thorchain
        }

    suspend fun fetchChainId(): String =
        withFailover { api ->
            api.nodeInfo().defaultNodeInfo.network
        }

    suspend fun broadcast(txRaw: ByteArray): String =
        withFailover { api ->
            val response = api.broadcast(
                BroadcastRequest(txBytes = Base64.getEncoder().encodeToString(txRaw))
            ).txResponse

            if (response.code != 0) {
                throw BroadcastError(response.code, response.rawLog ?: "")
            }

            response.txhash
        }

    // null while the transaction is not yet included in a block
    suspend fun fetchTransaction(hash: String): TxResponse? =
        withFailover { api ->
            try {
                api.transaction(hash).txResponse
            } catch (error: HttpException) {
                if (error.code() == 404) null else throw error
            }
        }

    private suspend fun <T> withFailover(block: suspend (ThornodeApi) -> T): T {
        var lastError: Throwable? = null

        apis.forEach { api ->
            try {
                return block(api)
            } catch (error: HttpException) {
                // client errors are definitive answers, not provider outages
                if (error.code() < 500) throw error
                lastError = error
            } catch (error: BroadcastError) {
                throw error
            } catch (error: Throwable) {
                lastError = error
            }
        }

        throw lastError ?: IllegalStateException("No thornode providers configured")
    }

    companion object {

        fun parseAccountInfo(account: JsonObject): AccountInfo {
            val type = account.get("@type")?.asString ?: ""
            val fields = if (account.has("base_account")) {
                account.getAsJsonObject("base_account")
            } else {
                require(type.endsWith("BaseAccount")) { "Unsupported account type: $type" }
                account
            }

            return AccountInfo(
                accountNumber = fields.get("account_number").asString.toLong(),
                sequence = fields.get("sequence").asString.toLong()
            )
        }
    }
}

class BroadcastError(val code: Int, val log: String) : Throwable() {
    override val message: String
        get() = "Broadcast failed with code: $code, log: $log"
}
