package io.horizontalsystems.thorchainkit

import com.google.gson.JsonParser
import io.horizontalsystems.thorchainkit.network.ThornodeApiProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ThornodeApiProviderTest {

    @Test
    fun parseAccountInfo_baseAccount() {
        val json = JsonParser.parseString(
            """
            {
              "@type": "/cosmos.auth.v1beta1.BaseAccount",
              "address": "thor1gm00vwsfcp48enm4uv9e5dhm37jtd0ye27wrx0",
              "pub_key": null,
              "account_number": "123456",
              "sequence": "42"
            }
            """
        ).asJsonObject

        val accountInfo = ThornodeApiProvider.parseAccountInfo(json)

        assertEquals(123456L, accountInfo.accountNumber)
        assertEquals(42L, accountInfo.sequence)
    }

    @Test
    fun parseAccountInfo_moduleAccount() {
        // real payload shape returned for thor1dheycdevq39qlkxs2a6wuuzyn4aqxhve4qxtxt (reserve)
        val json = JsonParser.parseString(
            """
            {
              "@type": "/cosmos.auth.v1beta1.ModuleAccount",
              "base_account": {
                "address": "thor1dheycdevq39qlkxs2a6wuuzyn4aqxhve4qxtxt",
                "pub_key": null,
                "account_number": "8",
                "sequence": "0"
              },
              "name": "reserve",
              "permissions": []
            }
            """
        ).asJsonObject

        val accountInfo = ThornodeApiProvider.parseAccountInfo(json)

        assertEquals(8L, accountInfo.accountNumber)
        assertEquals(0L, accountInfo.sequence)
    }

    @Test
    fun parseAccountInfo_unsupportedType() {
        val json = JsonParser.parseString("""{"@type": "/cosmos.auth.v1beta1.SomethingElse"}""").asJsonObject

        assertThrows(IllegalArgumentException::class.java) {
            ThornodeApiProvider.parseAccountInfo(json)
        }
    }
}
