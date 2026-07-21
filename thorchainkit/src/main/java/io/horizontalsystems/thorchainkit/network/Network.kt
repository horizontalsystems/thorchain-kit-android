package io.horizontalsystems.thorchainkit.network

import java.net.URL

enum class Network(
    val id: Int,
    val coinType: Int,
    val addressPrefix: String,
    // pinned chain-id: used for signing instead of the node-reported value, so a
    // malicious or misconfigured endpoint can never make the kit sign a transaction
    // that is valid on a different network
    val chainId: String,
    val thornodeUrls: List<URL>,
    val midgardUrls: List<URL>
) {
    Mainnet(
        id = 1,
        coinType = 931,
        addressPrefix = "thor",
        chainId = "thorchain-1",
        thornodeUrls = listOf(
            URL("https://gateway.liquify.com/chain/thorchain_api/"),
            URL("https://thornode.thorchain.liquify.com/")
        ),
        midgardUrls = listOf(
            URL("https://gateway.liquify.com/chain/thorchain_midgard/")
        )
    ),
    // Public stagenet endpoints are in transition (Nine Realms endpoints are gone,
    // new URLs not yet published) — override base URLs via ThorchainKit until then
    Stagenet(
        id = 2,
        coinType = 931,
        addressPrefix = "sthor",
        chainId = "thorchain-stagenet-2",
        thornodeUrls = listOf(
            URL("https://stagenet-thornode.ninerealms.com/")
        ),
        midgardUrls = listOf(
            URL("https://stagenet-midgard.ninerealms.com/")
        )
    )
}
