package io.horizontalsystems.thorchainkit.sample

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.horizontalsystems.thorchainkit.ThorchainKit
import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.models.Transaction
import io.horizontalsystems.thorchainkit.network.Network
import io.horizontalsystems.thorchainkit.transaction.Signer
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger

private const val DEFAULT_MNEMONIC =
    "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // NOTE: a sample app storing a plaintext mnemonic in SharedPreferences is fine for a demo,
    // but a real wallet must keep the mnemonic in encrypted/keystore-backed storage.
    private val prefs = application.getSharedPreferences("thorchainkit_sample", Context.MODE_PRIVATE)

    private var mnemonicState by mutableStateOf(prefs.getString(KEY_MNEMONIC, null) ?: DEFAULT_MNEMONIC)
    var mnemonic: String
        get() = mnemonicState
        set(value) {
            mnemonicState = value
            prefs.edit().putString(KEY_MNEMONIC, value).apply()
        }

    private var networkState by mutableStateOf(readNetwork())
    var network: Network
        get() = networkState
        set(value) {
            networkState = value
            prefs.edit().putString(KEY_NETWORK, value.name).apply()
        }

    private var watchAddressState by mutableStateOf(prefs.getString(KEY_WATCH, null) ?: "")
    var watchAddress: String
        get() = watchAddressState
        set(value) {
            watchAddressState = value
            prefs.edit().putString(KEY_WATCH, value).apply()
        }

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

    init {
        // resume the last active session across process death
        when (prefs.getString(KEY_MODE, null)) {
            MODE_MNEMONIC -> startFromMnemonic(persist = false)
            MODE_WATCH -> startWatch(persist = false)
        }
    }

    fun startFromMnemonic(persist: Boolean = true) {
        runCatching {
            val seed = Mnemonic().toSeed(mnemonic.trim().split(Regex("\\s+")))
            signer = Signer.getInstance(seed, network)
            start(ThorchainKit.getAddress(seed, network))
            if (persist) persistSession(MODE_MNEMONIC)
        }.onFailure { error = it.message }
    }

    fun startWatch(persist: Boolean = true) {
        runCatching {
            signer = null
            start(Address.fromString(watchAddress.trim(), network))
            if (persist) persistSession(MODE_WATCH)
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
                    amount = BigDecimal(amount).movePointRight(kit.decimals).toBigInteger(),
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
        prefs.edit().remove(KEY_MODE).apply()
    }

    private fun persistSession(mode: String) {
        prefs.edit().putString(KEY_MODE, mode).apply()
    }

    private fun readNetwork(): Network =
        prefs.getString(KEY_NETWORK, null)
            ?.let { name -> runCatching { Network.valueOf(name) }.getOrNull() }
            ?: Network.Mainnet

    companion object {
        private const val KEY_NETWORK = "network"
        private const val KEY_MNEMONIC = "mnemonic"
        private const val KEY_WATCH = "watch_address"
        private const val KEY_MODE = "session_mode"
        private const val MODE_MNEMONIC = "mnemonic"
        private const val MODE_WATCH = "watch"
    }
}