# ThorchainKit Android

Native THORChain SDK for Android — RUNE and THORChain-native assets (TCY, RUJI, Secured Assets).

- HD key derivation `m/44'/931'/0'/0/0`, bech32 `thor1...` addresses
- Cosmos-style protobuf transaction signing (`SIGN_MODE_DIRECT`) with THORChain's native `MsgSend` / `MsgDeposit`
- THORNode REST for balances, account state, fees, and broadcast
- Midgard for transaction history
- Networks: `Mainnet` (prefix `thor`) and `Stagenet` (prefix `sthor`)

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
