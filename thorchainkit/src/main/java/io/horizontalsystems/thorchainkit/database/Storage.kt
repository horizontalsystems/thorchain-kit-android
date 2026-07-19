package io.horizontalsystems.thorchainkit.database

import io.horizontalsystems.thorchainkit.models.Balance
import io.horizontalsystems.thorchainkit.models.LastBlockHeight
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.models.TransactionSyncState
import java.math.BigInteger

class Storage(
    private val database: MainDatabase
) {

    fun getLastBlockHeight(): Long? {
        return database.lastBlockHeightDao().getLastBlockHeight()?.height
    }

    fun saveLastBlockHeight(lastBlockHeight: Long) {
        database.lastBlockHeightDao().insert(LastBlockHeight(lastBlockHeight))
    }

    fun getBalances(): List<Balance> {
        return database.balanceDao().getAll()
    }

    fun getBalance(denom: String): BigInteger? {
        return database.balanceDao().getBalance(denom)?.amount
    }

    fun saveBalances(balances: List<Balance>) {
        database.balanceDao().insert(balances)
        database.balanceDao().deleteExcept(balances.map { it.denom })
    }

    fun getTransactions(fromTimestamp: Long?, limit: Int?): List<Transaction> {
        return database.transactionDao().getTransactions(
            fromTimestamp ?: Long.MAX_VALUE,
            limit ?: Int.MAX_VALUE
        )
    }

    fun getPendingTransactions(): List<Transaction> {
        return database.transactionDao().getPending()
    }

    fun saveTransactions(transactions: List<Transaction>) {
        database.transactionDao().insert(transactions)
    }

    fun getTransactionSyncTimestamp(): Long? {
        return database.transactionDao().getSyncState()?.lastTimestamp
    }

    fun saveTransactionSyncTimestamp(timestamp: Long) {
        database.transactionDao().insert(TransactionSyncState(timestamp))
    }
}
