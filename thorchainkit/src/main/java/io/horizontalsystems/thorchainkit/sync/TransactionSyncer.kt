package io.horizontalsystems.thorchainkit.sync

import io.horizontalsystems.thorchainkit.ThorchainKit.SyncError
import io.horizontalsystems.thorchainkit.ThorchainKit.SyncState
import io.horizontalsystems.thorchainkit.database.Storage
import io.horizontalsystems.thorchainkit.models.CoinTransfer
import io.horizontalsystems.thorchainkit.models.Transaction
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

        fun fromMidgardAction(action: MidgardAction): Transaction? {
            val hash = action.incoming.firstOrNull { it.txId.isNotEmpty() }?.txId
                ?: action.outgoing.firstOrNull { it.txId.isNotEmpty() }?.txId
                ?: return null

            return Transaction(
                hash = hash,
                blockHeight = action.height,
                timestamp = action.date / 1_000_000,
                type = action.type,
                status = action.status,
                memo = extractMemo(action),
                incoming = action.incoming.map { tx ->
                    tx.coins.map { CoinTransfer(tx.address, it.asset, BigInteger(it.amount)) }
                }.flatten(),
                outgoing = action.outgoing.map { tx ->
                    tx.coins.map { CoinTransfer(tx.address, it.asset, BigInteger(it.amount)) }
                }.flatten()
            )
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
