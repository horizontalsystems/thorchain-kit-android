package io.horizontalsystems.thorchainkit.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigInteger

@Entity
data class Transaction(
    @PrimaryKey
    val hash: String,
    val blockHeight: Long,
    val timestamp: Long,
    val type: String,
    val status: String,
    val memo: String?,
    val incoming: List<CoinTransfer>,
    val outgoing: List<CoinTransfer>
) {

    val isPending: Boolean
        get() = status == "pending"
}

data class CoinTransfer(
    val address: String,
    val asset: String,
    val amount: BigInteger
)
