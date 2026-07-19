package io.horizontalsystems.thorchainkit.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.sqlite.SQLiteStatement
import io.horizontalsystems.thorchainkit.models.LastBlockHeight
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LastBlockHeightDao_Impl(
  __db: RoomDatabase,
) : LastBlockHeightDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLastBlockHeight: EntityInsertAdapter<LastBlockHeight>
  init {
    this.__db = __db
    this.__insertAdapterOfLastBlockHeight = object : EntityInsertAdapter<LastBlockHeight>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `LastBlockHeight` (`height`,`id`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LastBlockHeight) {
        statement.bindLong(1, entity.height)
        statement.bindText(2, entity.id)
      }
    }
  }

  public override fun insert(lastBlockHeight: LastBlockHeight): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfLastBlockHeight.insert(_connection, lastBlockHeight)
  }

  public override fun getLastBlockHeight(): LastBlockHeight? {
    val _sql: String = "SELECT * FROM LastBlockHeight"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _result: LastBlockHeight?
        if (_stmt.step()) {
          val _tmpHeight: Long
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight)
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          _result = LastBlockHeight(_tmpHeight,_tmpId)
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
