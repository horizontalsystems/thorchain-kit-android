package io.horizontalsystems.thorchainkit.transaction

import fr.acinq.secp256k1.Secp256k1
import io.horizontalsystems.hdwalletkit.ECKey
import io.horizontalsystems.hdwalletkit.HDWallet
import io.horizontalsystems.hdwalletkit.Utils
import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.network.Network
import java.math.BigInteger

class Signer(
    private val privateKey: BigInteger
) {

    val publicKey: ByteArray = ECKey(privateKey, true).pubKey

    // Signs a 32-byte digest, returns a 64-byte r||s signature (RFC6979 deterministic, low-S),
    // the format Cosmos SIGN_MODE_DIRECT expects
    fun sign(digest: ByteArray): ByteArray {
        require(digest.size == 32) { "Invalid digest size: ${digest.size}" }

        return Secp256k1.get().sign(digest, Utils.bigIntegerToBytes(privateKey, 32))
    }

    companion object {
        fun getInstance(seed: ByteArray, network: Network): Signer {
            return Signer(privateKey(seed, network))
        }

        fun privateKey(seed: ByteArray, network: Network): BigInteger {
            val hdWallet = HDWallet(seed, network.coinType, HDWallet.Purpose.BIP44)
            return hdWallet.privateKey(0, 0, true).privKey
        }

        fun address(privateKey: BigInteger, network: Network): Address {
            val publicKey = ECKey(privateKey, true).pubKey
            return Address.fromPublicKey(publicKey, network)
        }
    }
}
