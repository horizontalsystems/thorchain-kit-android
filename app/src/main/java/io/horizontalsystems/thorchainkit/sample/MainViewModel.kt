package io.horizontalsystems.thorchainkit.sample

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.horizontalsystems.thorchainkit.ThorchainKit
import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.models.Denom
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.network.Network
import io.horizontalsystems.thorchainkit.transaction.Signer
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var mnemonic by mutableStateOf("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about")
    var network by mutableStateOf(Network.Mainnet)
    var watchAddress by mutableStateOf("")

    var kit: ThorchainKit? by mutableStateOf(null)
        private set
    var error: String? by mutableStateOf(null)
        private set

    var syncState by mutableStateOf("")
    var transactionsSyncState by mutableStateOf("")
    var lastBlockHeight by mutableStateOf(0L)
    var balances by mutableStateOf<Map<String, BigInteger>>(emptyMap())
    var transactions by mutableStateOf<List<Transaction>>(emptyList())

    var sendResult: String? by mutableStateOf(null)

    private var signer: Signer? = null

    fun startFromMnemonic() {
        runCatching {
            val seed = Mnemonic().toSeed(mnemonic.trim().split(Regex("\\s+")))
            signer = Signer.getInstance(seed, network)
            start(ThorchainKit.getAddress(seed, network))
        }.onFailure { error = it.message }
    }

    fun startWatch() {
        runCatching {
            signer = null
            start(Address.fromString(watchAddress.trim(), network))
        }.onFailure { error = it.message }
    }

    private fun start(address: Address) {
        error = null

        val kit = ThorchainKit.getInstance(getApplication(), address, network, walletId = "sample")
        this.kit = kit
        kit.start()

        viewModelScope.launch { kit.syncStateFlow.collect { syncState = it.toString() } }
        viewModelScope.launch { kit.transactionsSyncStateFlow.collect { transactionsSyncState = it.toString() } }
        viewModelScope.launch { kit.lastBlockHeightFlow.collect { lastBlockHeight = it } }
        viewModelScope.launch { kit.balancesFlow.collect { balances = it } }
        viewModelScope.launch {
            kit.transactionsFlow.collect { transactions = kit.getTransactions(limit = 20) }
        }
    }

    fun send(to: String, amount: String, memo: String) {
        val kit = kit ?: return
        val signer = signer ?: run {
            sendResult = "watch-only: no signer"
            return
        }

        viewModelScope.launch {
            sendResult = runCatching {
                val txHash = kit.send(
                    to = Address.fromString(to.trim(), network),
                    amount = BigDecimal(amount).movePointRight(Denom.DECIMALS).toBigInteger(),
                    memo = memo.ifBlank { null },
                    signer = signer
                )
                "sent: $txHash"
            }.getOrElse { "error: ${it.message}" }
        }
    }

    fun stop() {
        kit?.stop()
        kit = null
        signer = null
        transactions = emptyList()
        balances = emptyMap()
        sendResult = null
    }
}
