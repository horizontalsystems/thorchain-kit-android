package io.horizontalsystems.thorchainkit.models

// THORChain bank denom notation. All native assets are bank denoms with 8 decimals:
// "rune", "tcy", "x/ruji" (THOR-native tokens), "btc-btc" (secured), "btc/btc" (synth)
object Denom {

    const val RUNE = "rune"
    const val DECIMALS = 8

    // THOR-native denoms whose notation is irregular (not derivable from the asset)
    private val nativeAssets = mapOf(
        RUNE to Asset.Rune,
        "tcy" to Asset("THOR", "TCY", "TCY"),
        "x/ruji" to Asset("THOR", "RUJI", "RUJI")
    )

    fun assetFor(denom: String): Asset {
        val lowered = denom.lowercase()

        nativeAssets[lowered]?.let { return it }

        return if (lowered.none { it == '.' || it == '/' || it == '~' || it == '-' }) {
            // plain denom: a THOR-native token (like "tcy")
            Asset("THOR", lowered.uppercase(), lowered.uppercase())
        } else {
            // "chain-symbol" (secured) or "chain/symbol" (synth)
            Asset.fromString(lowered)
        }
    }

    fun denomFor(asset: Asset): String {
        nativeAssets.entries.firstOrNull { it.value == asset }?.let { return it.key }

        return if (asset.chain == "THOR" && !asset.synth && !asset.trade && !asset.secured) {
            asset.symbol.lowercase()
        } else {
            asset.toString().lowercase()
        }
    }
}
