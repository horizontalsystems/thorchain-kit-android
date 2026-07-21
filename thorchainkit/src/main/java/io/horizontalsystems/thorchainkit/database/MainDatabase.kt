package io.horizontalsystems.thorchainkit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.horizontalsystems.thorchainkit.models.Balance
import io.horizontalsystems.thorchainkit.models.LastBlockHeight
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.models.TransactionSyncState

@Database(
    entities = [
        LastBlockHeight::class,
        Balance::class,
        Transaction::class,
        TransactionSyncState::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class MainDatabase : RoomDatabase() {

    abstract fun lastBlockHeightDao(): LastBlockHeightDao
    abstract fun balanceDao(): BalanceDao
    abstract fun transactionDao(): TransactionDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE TransactionSyncState ADD COLUMN backfillPageToken TEXT")
            }
        }

        fun getInstance(context: Context, databaseName: String): MainDatabase {
            return Room.databaseBuilder(context, MainDatabase::class.java, databaseName)
                .addMigrations(MIGRATION_1_2)
                // last resort only: everything stored is a re-syncable cache (no keys)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
