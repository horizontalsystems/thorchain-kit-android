package io.horizontalsystems.thorchainkit

import android.content.Context
import io.horizontalsystems.thorchainkit.network.Network

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
