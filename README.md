# ThorchainKit Android

Native THORChain SDK for Android — RUNE and THORChain-native assets (TCY, RUJI, Secured Assets).

- HD key derivation `m/44'/931'/0'/0/0`, bech32 `thor1...` addresses
- Cosmos-style protobuf transaction signing (`SIGN_MODE_DIRECT`) with THORChain's native `MsgSend` / `MsgDeposit`
- THORNode REST for balances, account state, fees, and broadcast
- Midgard for transaction history
- Networks: `Mainnet` (prefix `thor`) and `Stagenet` (prefix `sthor`)

## Denoms

The kit is denom-agnostic: balances and sends work with plain bank denom strings.
All THORChain native assets are bank denoms with **8 decimals**:

| Denom | Asset | Kind |
|---|---|---|
| `rune` | `THOR.RUNE` | native |
| `tcy` | `THOR.TCY` | native token |
| `x/ruji` | `THOR.RUJI` | native token |
| `btc-btc` | `BTC-BTC` | secured asset |
| `btc/btc` | `BTC/BTC` | synth (deprecated) |

`Denom.assetFor(denom)` / `Denom.denomFor(asset)` convert between the two notations.
Naming and metadata for these tokens live on the wallet side; the kit does not carry a token list.

## Modules

- `thorchainkit` — the library
- `app` — sample app

## Installation

Add [JitPack](https://jitpack.io) to repositories and the dependency:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.horizontalsystems:thorchain-kit-android:<commit-hash>'
}
```

## License

MIT
