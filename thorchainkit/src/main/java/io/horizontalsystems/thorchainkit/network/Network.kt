package io.horizontalsystems.thorchainkit.network

import java.net.URL

enum class Network(
    val id: Int,
    val coinType: Int,
    val addressPrefix: String,
    val thornodeUrls: List<URL>,
    val midgardUrls: List<URL>
) {
    Mainnet(
        id = 1,
        coinType = 931,
        addressPrefix = "thor",
        thornodeUrls = listOf(
            URL("https://thornode.ninerealms.com/"),
            URL("https://thornode-v2.ninerealms.com/")
        ),
        midgardUrls = listOf(
            URL("https://midgard.ninerealms.com/")
        )
    ),
    Stagenet(
        id = 2,
        coinType = 931,
        addressPrefix = "sthor",
        thornodeUrls = listOf(
            URL("https://stagenet-thornode.ninerealms.com/")
        ),
        midgardUrls = listOf(
            URL("https://stagenet-midgard.ninerealms.com/")
        )
    )
}
