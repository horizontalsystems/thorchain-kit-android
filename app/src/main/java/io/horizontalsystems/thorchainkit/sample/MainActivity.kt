package io.horizontalsystems.thorchainkit.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.thorchainkit.network.Network
import java.math.BigDecimal
import java.math.BigInteger

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val kit = viewModel.kit

        if (kit == null) {
            Text("ThorchainKit Sample", style = MaterialTheme.typography.h6)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                selectableNetworks.forEach { network ->
                    OutlinedButton(onClick = { viewModel.network = network }) {
                        Text(if (network == viewModel.network) "✓ ${network.displayName}" else network.displayName)
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.mnemonic,
                onValueChange = { viewModel.mnemonic = it },
                label = { Text("Mnemonic") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.startFromMnemonic() }, modifier = Modifier.fillMaxWidth()) {
                Text("Restore")
            }

            OutlinedTextField(
                value = viewModel.watchAddress,
                onValueChange = { viewModel.watchAddress = it },
                label = { Text("Watch address (thor1...)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(onClick = { viewModel.startWatch() }, modifier = Modifier.fillMaxWidth()) {
                Text("Watch")
            }

            viewModel.error?.let { Text("Error: $it", color = MaterialTheme.colors.error) }
        } else {
            Text("${kit.network.displayName} · block ${viewModel.lastBlockHeight}", style = MaterialTheme.typography.caption)
            Text(kit.receiveAddress, style = MaterialTheme.typography.body2)
            Text("Sync: ${viewModel.syncState}", style = MaterialTheme.typography.caption)
            Text("Tx sync: ${viewModel.transactionsSyncState}", style = MaterialTheme.typography.caption)

            Divider()
            Text("Balances", style = MaterialTheme.typography.h6)
            if (viewModel.balances.isEmpty()) {
                Text("no balances", style = MaterialTheme.typography.caption)
            }
            viewModel.balances.toSortedMap().forEach { (denom, amount) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(denom)
                    Text(amount.toRune(kit.decimals))
                }
            }

            Divider()
            SendSection(viewModel)

            Divider()
            Text("Transactions", style = MaterialTheme.typography.h6)
            viewModel.transactions.forEach { tx ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("${tx.type} · ${tx.status}", style = MaterialTheme.typography.body2)
                    Text(tx.hash.take(16) + "…", style = MaterialTheme.typography.caption)
                    tx.memo?.takeIf { it.isNotBlank() }?.let {
                        Text("memo: $it", style = MaterialTheme.typography.caption)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Divider()
            OutlinedButton(onClick = { viewModel.stop() }, modifier = Modifier.fillMaxWidth()) {
                Text("Stop")
            }
        }
    }
}

@Composable
private fun SendSection(viewModel: MainViewModel) {
    var to by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var memo by rememberSaveable { mutableStateOf("") }

    // native settlement asset of the selected chain (RUNE on THORChain, CACAO on Maya)
    val symbol = viewModel.kit?.nativeDenom?.uppercase() ?: "RUNE"

    Text("Send $symbol", style = MaterialTheme.typography.h6)
    OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("To") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ($symbol)") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = memo, onValueChange = { memo = it }, label = { Text("Memo (optional)") }, modifier = Modifier.fillMaxWidth())
    Button(
        onClick = { viewModel.send(to, amount, memo) },
        enabled = to.isNotBlank() && amount.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Send")
    }
    viewModel.sendResult?.let { Text(it, style = MaterialTheme.typography.caption) }
}

// mainnet chains shown in the sample selector (stagenet is a kit config option, not surfaced here)
private val selectableNetworks = listOf(Network.Mainnet, Network.MayaMainnet)

private val Network.displayName: String
    get() = when (this) {
        Network.Mainnet -> "THORChain"
        Network.MayaMainnet -> "Mayachain"
        else -> name
    }

private fun BigInteger.toRune(decimals: Int): String {
    return BigDecimal(this).movePointLeft(decimals).stripTrailingZeros().toPlainString()
}
