package io.horizontalsystems.thorchainkit.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ThornodeApi {

    @GET("cosmos/bank/v1beta1/balances/{address}")
    suspend fun balances(
        @Path("address") address: String,
        @Query("pagination.limit") limit: Int = 1000
    ): BalancesResponse

    @GET("cosmos/auth/v1beta1/accounts/{address}")
    suspend fun account(@Path("address") address: String): AccountResponse

    @GET("thorchain/network")
    suspend fun network(): NetworkResponse

    @GET("thorchain/lastblock/thorchain")
    suspend fun lastBlock(): List<LastBlockResponse>

    @GET("cosmos/base/tendermint/v1beta1/node_info")
    suspend fun nodeInfo(): NodeInfoResponse

    @POST("cosmos/tx/v1beta1/txs")
    suspend fun broadcast(@Body request: BroadcastRequest): BroadcastResponse

    @GET("cosmos/tx/v1beta1/txs/{hash}")
    suspend fun transaction(@Path("hash") hash: String): TxByHashResponse
}

data class BalancesResponse(
    val balances: List<CoinResponse>
)

data class CoinResponse(
    val denom: String,
    val amount: String
)

// account payload varies: BaseAccount fields are top-level, ModuleAccount nests
// them under base_account — parsed manually in ThornodeApiProvider
data class AccountResponse(
    val account: JsonObject
)

data class NetworkResponse(
    @SerializedName("native_tx_fee_rune") val nativeTxFeeRune: String
)

data class LastBlockResponse(
    val chain: String,
    val thorchain: Long
)

data class NodeInfoResponse(
    @SerializedName("default_node_info") val defaultNodeInfo: DefaultNodeInfo
) {
    data class DefaultNodeInfo(
        val network: String
    )
}

data class BroadcastRequest(
    @SerializedName("tx_bytes") val txBytes: String,
    val mode: String = "BROADCAST_MODE_SYNC"
)

data class BroadcastResponse(
    @SerializedName("tx_response") val txResponse: TxResponse
)

data class TxByHashResponse(
    @SerializedName("tx_response") val txResponse: TxResponse
)

data class TxResponse(
    val height: String,
    val txhash: String,
    val code: Int,
    @SerializedName("raw_log") val rawLog: String?
)
