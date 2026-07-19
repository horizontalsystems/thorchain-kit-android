package io.horizontalsystems.thorchainkit.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.sqlite.SQLiteStatement
import io.horizontalsystems.thorchainkit.models.Balance
import java.math.BigInteger
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BalanceDao_Impl(
  __db: RoomDatabase,
) : BalanceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBalance: EntityInsertAdapter<Balance>

  private val __roomTypeConverters: RoomTypeConverters = RoomTypeConverters()
  init {
    this.__db = __db
    this.__insertAdapterOfBalance = object : EntityInsertAdapter<Balance>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `Balance` (`denom`,`amount`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Balance) {
        statement.bindText(1, entity.denom)
        val _tmp: String = __roomTypeConverters.bigIntegerToString(entity.amount)
        statement.bindText(2, _tmp)
      }
    }
  }

  public override fun insert(balances: List<Balance>): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfBalance.insert(_connection, balances)
  }

  public override fun getAll(): List<Balance> {
    val _sql: String = "SELECT * FROM Balance"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDenom: Int = getColumnIndexOrThrow(_stmt, "denom")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _result: MutableList<Balance> = mutableListOf()
        while (_stmt.step()) {
          val _item: Balance
          val _tmpDenom: String
          _tmpDenom = _stmt.getText(_columnIndexOfDenom)
          val _tmpAmount: BigInteger
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfAmount)
          _tmpAmount = __roomTypeConverters.bigIntegerFromString(_tmp)
          _item = Balance(_tmpDenom,_tmpAmount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBalance(denom: String): Balance? {
    val _sql: String = "SELECT * FROM Balance WHERE denom = ?"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, denom)
        val _columnIndexOfDenom: Int = getColumnIndexOrThrow(_stmt, "denom")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _result: Balance?
        if (_stmt.step()) {
          val _tmpDenom: String
          _tmpDenom = _stmt.getText(_columnIndexOfDenom)
          val _tmpAmount: BigInteger
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfAmount)
          _tmpAmount = __roomTypeConverters.bigIntegerFromString(_tmp)
          _result = Balance(_tmpDenom,_tmpAmount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun deleteExcept(denoms: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM Balance WHERE denom NOT IN (")
    val _inputSize: Int = denoms.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performBlocking(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in denoms) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
