package io.horizontalsystems.thorchainkit.database

import android.content.Context
import io.horizontalsystems.thorchainkit.network.Network

internal object ThorchainDatabaseManager {

    fun getMainDatabase(context: Context, network: Network, walletId: String): MainDatabase {
        return MainDatabase.getInstance(context, getDatabaseName(network, walletId))
    }

    fun clear(context: Context, network: Network, walletId: String) {
        synchronized(this) {
            context.deleteDatabase(getDatabaseName(network, walletId))
        }
    }

    private fun getDatabaseName(network: Network, walletId: String): String {
        // protocolPath makes chain identity an explicit component, so a THORChain and a
        // Maya wallet derived from the same seed can never share a Room DB file. (network.name
        // is already unique within the single Network enum, but keying on the chain
        // explicitly is correct-by-construction rather than relying on that convention.)
        return "${network.protocolPath}-${network.name}-$walletId"
    }
}
