package io.horizontalsystems.thorchainkit

import io.horizontalsystems.thorchainkit.models.Asset
import io.horizontalsystems.thorchainkit.models.Denom
import org.junit.Assert.assertEquals
import org.junit.Test

class DenomTest {

    @Test
    fun assetFor_thorNatives() {
        assertEquals(Asset.Rune, Denom.assetFor("rune"))
        assertEquals(Asset("THOR", "TCY", "TCY"), Denom.assetFor("tcy"))
        assertEquals(Asset("THOR", "RUJI", "RUJI"), Denom.assetFor("x/ruji"))
    }

    @Test
    fun assetFor_secured() {
        assertEquals(Asset("BTC", "BTC", "BTC", secured = true), Denom.assetFor("btc-btc"))
        assertEquals(
            Asset("ETH", "USDC-0XA0B86991C6218B36C1D19D4A2E9EB0CE3606EB48", "USDC", secured = true),
            Denom.assetFor("eth-usdc-0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48")
        )
    }

    @Test
    fun assetFor_synth() {
        assertEquals(Asset("BTC", "BTC", "BTC", synth = true), Denom.assetFor("btc/btc"))
    }

    @Test
    fun denomFor_inverse() {
        // every assetFor result maps back to the original denom
        listOf(
            "rune",
            "tcy",
            "x/ruji",
            "btc-btc",
            "btc/btc",
            "eth-usdc-0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
        ).forEach { denom ->
            assertEquals(denom, Denom.denomFor(Denom.assetFor(denom)))
        }
    }

    @Test
    fun denomFor_futureThorNative() {
        assertEquals("newtoken", Denom.denomFor(Asset("THOR", "NEWTOKEN", "NEWTOKEN")))
        assertEquals(Asset("THOR", "NEWTOKEN", "NEWTOKEN"), Denom.assetFor("newtoken"))
    }
}
