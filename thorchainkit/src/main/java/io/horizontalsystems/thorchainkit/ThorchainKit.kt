package io.horizontalsystems.thorchainkit

import android.content.Context
import io.horizontalsystems.thorchainkit.network.Network
import java.util.Objects

class ThorchainKit private constructor(
    val network: Network,
    val walletId: String
) {

    fun start() {
        TODO("Not yet implemented")
    }

    fun stop() {
        TODO("Not yet implemented")
    }

    fun refresh() {
        TODO("Not yet implemented")
    }

    fun statusInfo(): Map<String, Any> = mapOf(
        "Network" to network.name
    )

    sealed class SyncState {
        class Synced : SyncState()
        class NotSynced(val error: Throwable) : SyncState()
        class Syncing(val progress: Double? = null) : SyncState()

        override fun toString(): String = when (this) {
            is Syncing -> "Syncing ${progress?.let { "${it * 100}" } ?: ""}"
            is NotSynced -> "NotSynced ${error.javaClass.simpleName} - message: ${error.message}"
            else -> this.javaClass.simpleName
        }

        override fun equals(other: Any?): Boolean {
            if (other !is SyncState) return false
            if (other.javaClass != this.javaClass) return false
            if (other is Syncing && this is Syncing) return other.progress == this.progress
            return true
        }

        override fun hashCode(): Int {
            if (this is Syncing) return Objects.hashCode(this.progress)
            return Objects.hashCode(this.javaClass.name)
        }
    }

    sealed class SyncError : Throwable() {
        class NotStarted : SyncError()
        class NoNetworkConnection : SyncError()
    }

    companion object {

        fun getInstance(
            context: Context,
            seed: ByteArray,
            network: Network,
            walletId: String
        ): ThorchainKit {
            TODO("Not yet implemented")
        }

        fun clear(context: Context, network: Network, walletId: String) {
            TODO("Not yet implemented")
        }
    }
}
