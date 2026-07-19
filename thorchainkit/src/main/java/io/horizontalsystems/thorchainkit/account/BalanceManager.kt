package io.horizontalsystems.thorchainkit.account

import io.horizontalsystems.thorchainkit.database.Storage
import io.horizontalsystems.thorchainkit.models.Balance
import io.horizontalsystems.thorchainkit.models.DenomBalance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.math.BigInteger

class BalanceManager(
    private val storage: Storage
) {

    companion object {
        const val RUNE_DENOM = "rune"
    }

    private val _balancesFlow = MutableStateFlow(
        storage.getBalances().associate { it.denom to it.amount }
    )
    val balancesFlow: StateFlow<Map<String, BigInteger>> = _balancesFlow

    private val _runeBalanceFlow = MutableStateFlow(balancesFlow.value[RUNE_DENOM] ?: BigInteger.ZERO)
    val runeBalanceFlow: StateFlow<BigInteger> = _runeBalanceFlow

    val runeBalance: BigInteger
        get() = _runeBalanceFlow.value

    fun balance(denom: String): BigInteger {
        return _balancesFlow.value[denom] ?: BigInteger.ZERO
    }

    fun handleBalances(balances: List<DenomBalance>) {
        storage.saveBalances(balances.map { Balance(it.denom, it.amount) })

        _balancesFlow.update { balances.associate { it.denom to it.amount } }
        _runeBalanceFlow.update { _balancesFlow.value[RUNE_DENOM] ?: BigInteger.ZERO }
    }
}
