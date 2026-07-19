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
        return "Thorchain-${network.name}-$walletId"
    }
}
