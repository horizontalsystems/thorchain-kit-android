package io.horizontalsystems.thorchainkit.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MainDatabase_Impl : MainDatabase() {
  private val _lastBlockHeightDao: Lazy<LastBlockHeightDao> = lazy {
    LastBlockHeightDao_Impl(this)
  }

  private val _balanceDao: Lazy<BalanceDao> = lazy {
    BalanceDao_Impl(this)
  }

  private val _transactionDao: Lazy<TransactionDao> = lazy {
    TransactionDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "27de276654f74df36deedc7829f98a38", "a0748d58fec1ff9d65a0b5998df1e2d8") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `LastBlockHeight` (`height` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Balance` (`denom` TEXT NOT NULL, `amount` TEXT NOT NULL, PRIMARY KEY(`denom`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Transaction` (`hash` TEXT NOT NULL, `blockHeight` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `memo` TEXT, `incoming` TEXT NOT NULL, `outgoing` TEXT NOT NULL, PRIMARY KEY(`hash`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `TransactionSyncState` (`lastTimestamp` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '27de276654f74df36deedc7829f98a38')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `LastBlockHeight`")
        connection.execSQL("DROP TABLE IF EXISTS `Balance`")
        connection.execSQL("DROP TABLE IF EXISTS `Transaction`")
        connection.execSQL("DROP TABLE IF EXISTS `TransactionSyncState`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsLastBlockHeight: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLastBlockHeight.put("height", TableInfo.Column("height", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLastBlockHeight.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLastBlockHeight: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLastBlockHeight: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLastBlockHeight: TableInfo = TableInfo("LastBlockHeight", _columnsLastBlockHeight, _foreignKeysLastBlockHeight, _indicesLastBlockHeight)
        val _existingLastBlockHeight: TableInfo = read(connection, "LastBlockHeight")
        if (!_infoLastBlockHeight.equals(_existingLastBlockHeight)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |LastBlockHeight(io.horizontalsystems.thorchainkit.models.LastBlockHeight).
              | Expected:
              |""".trimMargin() + _infoLastBlockHeight + """
              |
              | Found:
              |""".trimMargin() + _existingLastBlockHeight)
        }
        val _columnsBalance: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBalance.put("denom", TableInfo.Column("denom", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBalance.put("amount", TableInfo.Column("amount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBalance: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBalance: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBalance: TableInfo = TableInfo("Balance", _columnsBalance, _foreignKeysBalance, _indicesBalance)
        val _existingBalance: TableInfo = read(connection, "Balance")
        if (!_infoBalance.equals(_existingBalance)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |Balance(io.horizontalsystems.thorchainkit.models.Balance).
              | Expected:
              |""".trimMargin() + _infoBalance + """
              |
              | Found:
              |""".trimMargin() + _existingBalance)
        }
        val _columnsTransaction: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransaction.put("hash", TableInfo.Column("hash", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("blockHeight", TableInfo.Column("blockHeight", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("memo", TableInfo.Column("memo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("incoming", TableInfo.Column("incoming", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransaction.put("outgoing", TableInfo.Column("outgoing", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransaction: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransaction: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTransaction: TableInfo = TableInfo("Transaction", _columnsTransaction, _foreignKeysTransaction, _indicesTransaction)
        val _existingTransaction: TableInfo = read(connection, "Transaction")
        if (!_infoTransaction.equals(_existingTransaction)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |Transaction(io.horizontalsystems.thorchainkit.models.Transaction).
              | Expected:
              |""".trimMargin() + _infoTransaction + """
              |
              | Found:
              |""".trimMargin() + _existingTransaction)
        }
        val _columnsTransactionSyncState: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactionSyncState.put("lastTimestamp", TableInfo.Column("lastTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionSyncState.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactionSyncState: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransactionSyncState: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTransactionSyncState: TableInfo = TableInfo("TransactionSyncState", _columnsTransactionSyncState, _foreignKeysTransactionSyncState, _indicesTransactionSyncState)
        val _existingTransactionSyncState: TableInfo = read(connection, "TransactionSyncState")
        if (!_infoTransactionSyncState.equals(_existingTransactionSyncState)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |TransactionSyncState(io.horizontalsystems.thorchainkit.models.TransactionSyncState).
              | Expected:
              |""".trimMargin() + _infoTransactionSyncState + """
              |
              | Found:
              |""".trimMargin() + _existingTransactionSyncState)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "LastBlockHeight", "Balance", "Transaction", "TransactionSyncState")
  }

  public override fun clearAllTables() {
    super.performClear(false, "LastBlockHeight", "Balance", "Transaction", "TransactionSyncState")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(LastBlockHeightDao::class, LastBlockHeightDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BalanceDao::class, BalanceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransactionDao::class, TransactionDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun lastBlockHeightDao(): LastBlockHeightDao = _lastBlockHeightDao.value

  public override fun balanceDao(): BalanceDao = _balanceDao.value

  public override fun transactionDao(): TransactionDao = _transactionDao.value
}
