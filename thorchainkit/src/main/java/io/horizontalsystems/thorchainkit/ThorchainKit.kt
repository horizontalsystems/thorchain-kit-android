package io.horizontalsystems.thorchainkit

import android.content.Context
import io.horizontalsystems.thorchainkit.account.BalanceManager
import io.horizontalsystems.thorchainkit.database.Storage
import io.horizontalsystems.thorchainkit.database.ThorchainDatabaseManager
import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.models.Asset
import io.horizontalsystems.thorchainkit.models.Denom
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.network.ConnectionManager
import io.horizontalsystems.thorchainkit.network.MidgardProvider
import io.horizontalsystems.thorchainkit.network.Network
import io.horizontalsystems.thorchainkit.network.ThornodeApiProvider
import io.horizontalsystems.thorchainkit.sync.SyncTimer
import io.horizontalsystems.thorchainkit.sync.Syncer
import io.horizontalsystems.thorchainkit.sync.TransactionSyncer
import io.horizontalsystems.thorchainkit.transaction.Signer
import io.horizontalsystems.thorchainkit.transaction.TransactionSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.math.BigInteger
import java.net.URL
import java.util.Objects

class ThorchainKit private constructor(
    val address: Address,
    val network: Network,
    private val syncer: Syncer,
    private val transactionSyncer: TransactionSyncer,
    private val balanceManager: BalanceManager,
    private val transactionSender: TransactionSender,
    private val thornodeApiProvider: ThornodeApiProvider,
    private val storage: Storage
) {

    private var started = false
    private var scope: CoroutineScope? = null

    val receiveAddress: String
        get() = address.toString()

    val lastBlockHeight: Long
        get() = syncer.lastBlockHeight

    val lastBlockHeightFlow: StateFlow<Long>
        get() = syncer.lastBlockHeightFlow

    val runeBalance: BigInteger
        get() = balanceManager.runeBalance

    val runeBalanceFlow: StateFlow<BigInteger>
        get() = balanceManager.runeBalanceFlow

    val balances: Map<String, BigInteger>
        get() = balanceManager.balancesFlow.value

    val balancesFlow: StateFlow<Map<String, BigInteger>>
        get() = balanceManager.balancesFlow

    val syncState: SyncState
        get() = syncer.syncState

    val syncStateFlow: StateFlow<SyncState>
        get() = syncer.syncStateFlow

    val transactionsSyncState: SyncState
        get() = transactionSyncer.syncState

    val transactionsSyncStateFlow: StateFlow<SyncState>
        get() = transactionSyncer.syncStateFlow

    val transactionsFlow: SharedFlow<List<Transaction>>
        get() = transactionSyncer.transactionsFlow

    fun getDenomBalance(denom: String): BigInteger {
        return balanceManager.balance(denom)
    }

    fun getDenomBalanceFlow(denom: String): Flow<BigInteger> {
        return balanceManager.balancesFlow
            .map { it[denom] ?: BigInteger.ZERO }
            .distinctUntilChanged()
    }

    fun getTransactions(fromTimestamp: Long? = null, limit: Int? = null): List<Transaction> {
        return storage.getTransactions(fromTimestamp, limit)
    }

    fun getPendingTransactions(): List<Transaction> {
        return storage.getPendingTransactions()
    }

    fun start() {
        if (started) return
        started = true

        scope = CoroutineScope(Dispatchers.IO).also {
            syncer.start(it)
        }
    }

    fun stop() {
        started = false
        syncer.stop()

        scope?.cancel()
    }

    fun pause() {
        syncer.pause()
    }

    fun resume() {
        syncer.resume()
    }

    fun refresh() {
        syncer.refresh()
    }

    suspend fun estimateFee(): BigInteger {
        return thornodeApiProvider.fetchNativeTxFee()
    }

    suspend fun send(
        to: Address,
        amount: BigInteger,
        denom: String = Denom.RUNE,
        memo: String? = null,
        signer: Signer
    ): String {
        require(to.prefix == network.addressPrefix) { "Address prefix mismatch: ${to.prefix}" }

        val txHash = transactionSender.send(to, amount, denom, memo, signer)
        refresh()
        return txHash
    }

    suspend fun deposit(
        asset: Asset,
        amount: BigInteger,
        memo: String,
        signer: Signer
    ): String {
        val txHash = transactionSender.deposit(asset, amount, memo, signer)
        refresh()
        return txHash
    }

    fun statusInfo(): Map<String, Any> {
        val statusInfo = LinkedHashMap<String, Any>()

        statusInfo["Started"] = started
        statusInfo["Address"] = receiveAddress
        statusInfo["Last Block Height"] = lastBlockHeight
        statusInfo["Sync State"] = syncState
        statusInfo["Transactions Sync State"] = transactionsSyncState
        statusInfo["RUNE Balance"] = runeBalance

        return statusInfo
    }

    sealed class SyncState {
        class Synced : SyncState()
        class NotSynced(val error: Throwable) : SyncState()
        class Syncing(val progress: Double? = null) : SyncState()

        override fun toString(): String = when (this) {
            is Syncing -> "Syncing ${progress?.let { "${it * 100}" } ?: ""}"
            is NotSynced -> "NotSynced ${error.javaClass.simpleName} - message: ${error.message}"
            else -> this.javaClass.simpleName
        }

        override fun equals(other: Any?): Boolean {
            if (other !is SyncState) return false
            if (other.javaClass != this.javaClass) return false
            if (other is Syncing && this is Syncing) return other.progress == this.progress
            return true
        }

        override fun hashCode(): Int {
            if (this is Syncing) return Objects.hashCode(this.progress)
            return Objects.hashCode(this.javaClass.name)
        }
    }

    sealed class SyncError : Throwable() {
        class NotStarted : SyncError()
        class NoNetworkConnection : SyncError()
    }

    companion object {

        fun getAddress(seed: ByteArray, network: Network): Address {
            val privateKey = Signer.privateKey(seed, network)
            return Signer.address(privateKey, network)
        }

        fun getInstance(
            context: Context,
            seed: ByteArray,
            network: Network,
            walletId: String,
            syncInterval: Long = 15,
            thornodeUrls: List<URL> = network.thornodeUrls,
            midgardUrls: List<URL> = network.midgardUrls
        ): ThorchainKit {
            return getInstance(context, getAddress(seed, network), network, walletId, syncInterval, thornodeUrls, midgardUrls)
        }

        // watch-account mode: address only; send/deposit will fail with SignerMismatch
        fun getInstance(
            context: Context,
            address: Address,
            network: Network,
            walletId: String,
            syncInterval: Long = 15,
            thornodeUrls: List<URL> = network.thornodeUrls,
            midgardUrls: List<URL> = network.midgardUrls
        ): ThorchainKit {
            require(address.prefix == network.addressPrefix) { "Address prefix mismatch: ${address.prefix}" }

            val thornodeApiProvider = ThornodeApiProvider(thornodeUrls)
            val midgardProvider = MidgardProvider(midgardUrls)

            val mainDatabase = ThorchainDatabaseManager.getMainDatabase(context, network, walletId)
            val storage = Storage(mainDatabase)

            val balanceManager = BalanceManager(storage)
            val transactionSyncer = TransactionSyncer(address.toString(), midgardProvider, storage)
            val syncTimer = SyncTimer(syncInterval, ConnectionManager(context))
            val syncer = Syncer(address, syncTimer, thornodeApiProvider, balanceManager, transactionSyncer, storage)
            val transactionSender = TransactionSender(address, thornodeApiProvider)

            return ThorchainKit(
                address = address,
                network = network,
                syncer = syncer,
                transactionSyncer = transactionSyncer,
                balanceManager = balanceManager,
                transactionSender = transactionSender,
                thornodeApiProvider = thornodeApiProvider,
                storage = storage
            )
        }

        fun clear(context: Context, network: Network, walletId: String) {
            ThorchainDatabaseManager.clear(context, network, walletId)
        }
    }
}
