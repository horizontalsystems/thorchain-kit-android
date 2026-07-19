package io.horizontalsystems.thorchainkit.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TransactionSyncState(
    val lastTimestamp: Long,
    @PrimaryKey val id: String = ""
)
