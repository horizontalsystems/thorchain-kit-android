package io.horizontalsystems.thorchainkit.sync

import io.horizontalsystems.thorchainkit.ThorchainKit.SyncError
import io.horizontalsystems.thorchainkit.ThorchainKit.SyncState
import io.horizontalsystems.thorchainkit.database.Storage
import io.horizontalsystems.thorchainkit.models.CoinTransfer
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.network.ActionTx
import io.horizontalsystems.thorchainkit.network.InvalidProviderResponse
import io.horizontalsystems.thorchainkit.network.MidgardAction
import io.horizontalsystems.thorchainkit.network.MidgardProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.math.BigInteger

class TransactionSyncer(
    private val address: String,
    private val midgardProvider: MidgardProvider,
    private val storage: Storage
) {

    companion object {
        private const val PAGE_LIMIT = 50
        private const val MAX_PAGES = 20

        // Midgard data is untrusted input: every required field is checked and a
        // malformed action surfaces as a typed InvalidProviderResponse instead of
        // an NPE or a silently wrong record
        fun fromMidgardAction(action: MidgardAction): Transaction? {
            val incoming = action.incoming
                ?: throw InvalidProviderResponse("midgard action: missing 'in'")
            val outgoing = action.outgoing
                ?: throw InvalidProviderResponse("midgard action: missing 'out'")

            val hash = incoming.firstOrNull { !it.txId.isNullOrEmpty() }?.txId
                ?: outgoing.firstOrNull { !it.txId.isNullOrEmpty() }?.txId
                ?: return null

            return Transaction(
                hash = hash,
                blockHeight = action.height
                    ?: throw InvalidProviderResponse("midgard action: missing height"),
                timestamp = (action.date
                    ?: throw InvalidProviderResponse("midgard action: missing date")) / 1_000_000,
                type = action.type
                    ?: throw InvalidProviderResponse("midgard action: missing type"),
                status = action.status
                    ?: throw InvalidProviderResponse("midgard action: missing status"),
                memo = extractMemo(action),
                incoming = coinTransfers(incoming),
                outgoing = coinTransfers(outgoing)
            )
        }

        private fun coinTransfers(txs: List<ActionTx>): List<CoinTransfer> {
            return txs.flatMap { tx ->
                val address = tx.address
                    ?: throw InvalidProviderResponse("midgard action: missing address")

                tx.coins.orEmpty().map { coin ->
                    val asset = coin.asset
                        ?: throw InvalidProviderResponse("midgard action: missing coin asset")
                    val amountString = coin.amount
                        ?: throw InvalidProviderResponse("midgard action: missing coin amount")
                    val amount = try {
                        BigInteger(amountString)
                    } catch (error: NumberFormatException) {
                        throw InvalidProviderResponse("midgard action: invalid amount: $amountString")
                    }

                    CoinTransfer(address, asset, amount)
                }
            }
        }

        // memo sits inside the type-specific metadata object: metadata.send.memo, metadata.swap.memo, ...
        private fun extractMemo(action: MidgardAction): String? {
            val metadata = action.metadata ?: return null

            metadata.entrySet().forEach { (_, value) ->
                if (value.isJsonObject) {
                    val memo = value.asJsonObject.get("memo")
                    if (memo != null && memo.isJsonPrimitive) {
                        return memo.asString
                    }
                }
            }
            return null
        }
    }

    private val _syncStateFlow: MutableStateFlow<SyncState> = MutableStateFlow(SyncState.NotSynced(SyncError.NotStarted()))
    val syncStateFlow: StateFlow<SyncState> = _syncStateFlow

    val syncState: SyncState
        get() = _syncStateFlow.value

    private val _transactionsFlow = MutableSharedFlow<List<Transaction>>(replay = 0, extraBufferCapacity = 10)
    val transactionsFlow: SharedFlow<List<Transaction>> = _transactionsFlow

    suspend fun sync() {
        _syncStateFlow.update { SyncState.Syncing() }

        try {
            val lastSyncedTimestamp = storage.getTransactionSyncTimestamp() ?: 0
            val updated = mutableListOf<Transaction>()
            var nextPageToken: String? = null

            for (page in 0 until MAX_PAGES) {
                val (actions, token) = midgardProvider.fetchActions(address, PAGE_LIMIT, nextPageToken)
                val transactions = actions.mapNotNull { fromMidgardAction(it) }

                updated.addAll(transactions)

                val reachedSynced = transactions.any { it.timestamp <= lastSyncedTimestamp && !it.isPending }
                nextPageToken = token

                if (reachedSynced || token == null || actions.isEmpty()) break
            }

            if (updated.isNotEmpty()) {
                storage.saveTransactions(updated)

                val maxConfirmedTimestamp = updated.filter { !it.isPending }.maxOfOrNull { it.timestamp }
                if (maxConfirmedTimestamp != null && maxConfirmedTimestamp > lastSyncedTimestamp) {
                    storage.saveTransactionSyncTimestamp(maxConfirmedTimestamp)
                }

                _transactionsFlow.emit(updated)
            }

            _syncStateFlow.update { SyncState.Synced() }
        } catch (error: Throwable) {
            _syncStateFlow.update { SyncState.NotSynced(error) }
        }
    }
}
