# Publishing & wallet integration

The kit publishes via [JitPack](https://jitpack.io), exactly like every other
HorizontalSystems `*-kit-android` library. `jitpack.yml` (`jdk: openjdk17`) and the
`maven-publish` block in `thorchainkit/build.gradle` mirror `tron-kit-android`.

JitPack builds the multi-module repo, skips the `app` (application) module, and
publishes the library module under the **repo-name** coordinate:

```
com.github.horizontalsystems:thorchain-kit-android:<commit-or-tag>
```

The published POM declares all runtime dependencies transitively (verified with
`./gradlew :thorchainkit:publishToMavenLocal`), including `secp256k1-kmp-jni-android`,
`protobuf-javalite`, Room and Retrofit — consumers get them automatically.

## Publish steps

1. Create the GitHub repo `horizontalsystems/thorchain-kit-android` and push `main`.
2. (Optional) Tag a release, e.g. `git tag 1.0.0 && git push --tags`. A raw commit
   hash also works as a version, which is how the wallet currently pins its kits.
3. Trigger the first JitPack build: open
   `https://jitpack.io/#horizontalsystems/thorchain-kit-android` and look up the
   commit/tag, or just let the wallet's first dependency resolution build it.

## Wallet-side wiring (`unstoppable-wallet-android`)

Two files, following the existing kit convention.

`gradle/libs.versions.toml`, under `# Wallet Kits`:

```toml
thorchainKit = "<commit-hash>"
```

and under the kit module list:

```toml
kit-thorchain = { module = "com.github.horizontalsystems:thorchain-kit-android", version.ref = "thorchainKit" }
```

`walletkit/build.gradle.kts`, alongside the other `api(libs.kit.*)` lines:

```kotlin
api(libs.kit.thorchain)
```

After this the wallet-side integration begins: `BlockchainType.Thorchain` in
market-kit + HS backend coin data, adapters/managers/UI wiring (Stellar as the
closest template), then RUNE in/out on the existing `ThorChainProvider`.
