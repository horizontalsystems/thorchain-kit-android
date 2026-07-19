package io.horizontalsystems.thorchainkit.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.sqlite.SQLiteStatement
import io.horizontalsystems.thorchainkit.models.CoinTransfer
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.models.TransactionSyncState
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransaction: EntityInsertAdapter<Transaction>

  private val __roomTypeConverters: RoomTypeConverters = RoomTypeConverters()

  private val __insertAdapterOfTransactionSyncState: EntityInsertAdapter<TransactionSyncState>
  init {
    this.__db = __db
    this.__insertAdapterOfTransaction = object : EntityInsertAdapter<Transaction>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `Transaction` (`hash`,`blockHeight`,`timestamp`,`type`,`status`,`memo`,`incoming`,`outgoing`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindText(1, entity.hash)
        statement.bindLong(2, entity.blockHeight)
        statement.bindLong(3, entity.timestamp)
        statement.bindText(4, entity.type)
        statement.bindText(5, entity.status)
        val _tmpMemo: String? = entity.memo
        if (_tmpMemo == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpMemo)
        }
        val _tmp: String = __roomTypeConverters.coinTransfersToString(entity.incoming)
        statement.bindText(7, _tmp)
        val _tmp_1: String = __roomTypeConverters.coinTransfersToString(entity.outgoing)
        statement.bindText(8, _tmp_1)
      }
    }
    this.__insertAdapterOfTransactionSyncState = object : EntityInsertAdapter<TransactionSyncState>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `TransactionSyncState` (`lastTimestamp`,`id`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionSyncState) {
        statement.bindLong(1, entity.lastTimestamp)
        statement.bindText(2, entity.id)
      }
    }
  }

  public override fun insert(transactions: List<Transaction>): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfTransaction.insert(_connection, transactions)
  }

  public override fun insert(syncState: TransactionSyncState): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfTransactionSyncState.insert(_connection, syncState)
  }

  public override fun getAll(): List<Transaction> {
    val _sql: String = "SELECT * FROM `Transaction` ORDER BY timestamp DESC, hash DESC"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfHash: Int = getColumnIndexOrThrow(_stmt, "hash")
        val _columnIndexOfBlockHeight: Int = getColumnIndexOrThrow(_stmt, "blockHeight")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMemo: Int = getColumnIndexOrThrow(_stmt, "memo")
        val _columnIndexOfIncoming: Int = getColumnIndexOrThrow(_stmt, "incoming")
        val _columnIndexOfOutgoing: Int = getColumnIndexOrThrow(_stmt, "outgoing")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpHash: String
          _tmpHash = _stmt.getText(_columnIndexOfHash)
          val _tmpBlockHeight: Long
          _tmpBlockHeight = _stmt.getLong(_columnIndexOfBlockHeight)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMemo: String?
          if (_stmt.isNull(_columnIndexOfMemo)) {
            _tmpMemo = null
          } else {
            _tmpMemo = _stmt.getText(_columnIndexOfMemo)
          }
          val _tmpIncoming: List<CoinTransfer>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfIncoming)
          _tmpIncoming = __roomTypeConverters.coinTransfersFromString(_tmp)
          val _tmpOutgoing: List<CoinTransfer>
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfOutgoing)
          _tmpOutgoing = __roomTypeConverters.coinTransfersFromString(_tmp_1)
          _item = Transaction(_tmpHash,_tmpBlockHeight,_tmpTimestamp,_tmpType,_tmpStatus,_tmpMemo,_tmpIncoming,_tmpOutgoing)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactions(fromTimestamp: Long, limit: Int): List<Transaction> {
    val _sql: String = "SELECT * FROM `Transaction` WHERE timestamp < ? ORDER BY timestamp DESC, hash DESC LIMIT ?"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, fromTimestamp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfHash: Int = getColumnIndexOrThrow(_stmt, "hash")
        val _columnIndexOfBlockHeight: Int = getColumnIndexOrThrow(_stmt, "blockHeight")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMemo: Int = getColumnIndexOrThrow(_stmt, "memo")
        val _columnIndexOfIncoming: Int = getColumnIndexOrThrow(_stmt, "incoming")
        val _columnIndexOfOutgoing: Int = getColumnIndexOrThrow(_stmt, "outgoing")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpHash: String
          _tmpHash = _stmt.getText(_columnIndexOfHash)
          val _tmpBlockHeight: Long
          _tmpBlockHeight = _stmt.getLong(_columnIndexOfBlockHeight)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMemo: String?
          if (_stmt.isNull(_columnIndexOfMemo)) {
            _tmpMemo = null
          } else {
            _tmpMemo = _stmt.getText(_columnIndexOfMemo)
          }
          val _tmpIncoming: List<CoinTransfer>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfIncoming)
          _tmpIncoming = __roomTypeConverters.coinTransfersFromString(_tmp)
          val _tmpOutgoing: List<CoinTransfer>
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfOutgoing)
          _tmpOutgoing = __roomTypeConverters.coinTransfersFromString(_tmp_1)
          _item = Transaction(_tmpHash,_tmpBlockHeight,_tmpTimestamp,_tmpType,_tmpStatus,_tmpMemo,_tmpIncoming,_tmpOutgoing)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPending(): List<Transaction> {
    val _sql: String = "SELECT * FROM `Transaction` WHERE status = 'pending'"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfHash: Int = getColumnIndexOrThrow(_stmt, "hash")
        val _columnIndexOfBlockHeight: Int = getColumnIndexOrThrow(_stmt, "blockHeight")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMemo: Int = getColumnIndexOrThrow(_stmt, "memo")
        val _columnIndexOfIncoming: Int = getColumnIndexOrThrow(_stmt, "incoming")
        val _columnIndexOfOutgoing: Int = getColumnIndexOrThrow(_stmt, "outgoing")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpHash: String
          _tmpHash = _stmt.getText(_columnIndexOfHash)
          val _tmpBlockHeight: Long
          _tmpBlockHeight = _stmt.getLong(_columnIndexOfBlockHeight)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpMemo: String?
          if (_stmt.isNull(_columnIndexOfMemo)) {
            _tmpMemo = null
          } else {
            _tmpMemo = _stmt.getText(_columnIndexOfMemo)
          }
          val _tmpIncoming: List<CoinTransfer>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfIncoming)
          _tmpIncoming = __roomTypeConverters.coinTransfersFromString(_tmp)
          val _tmpOutgoing: List<CoinTransfer>
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfOutgoing)
          _tmpOutgoing = __roomTypeConverters.coinTransfersFromString(_tmp_1)
          _item = Transaction(_tmpHash,_tmpBlockHeight,_tmpTimestamp,_tmpType,_tmpStatus,_tmpMemo,_tmpIncoming,_tmpOutgoing)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSyncState(): TransactionSyncState? {
    val _sql: String = "SELECT * FROM TransactionSyncState"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfLastTimestamp: Int = getColumnIndexOrThrow(_stmt, "lastTimestamp")
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _result: TransactionSyncState?
        if (_stmt.step()) {
          val _tmpLastTimestamp: Long
          _tmpLastTimestamp = _stmt.getLong(_columnIndexOfLastTimestamp)
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          _result = TransactionSyncState(_tmpLastTimestamp,_tmpId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
