package io.horizontalsystems.thorchainkit.transaction

import io.horizontalsystems.thorchainkit.models.Address
import io.horizontalsystems.thorchainkit.models.Asset
import io.horizontalsystems.thorchainkit.network.ThornodeApiProvider
import java.math.BigInteger

class TransactionSender(
    private val address: Address,
    private val thornodeApiProvider: ThornodeApiProvider
) {

    private var chainId: String? = null

    suspend fun send(to: Address, amount: BigInteger, denom: String, memo: String?, signer: Signer): String {
        val message = TxBuilder.msgSend(address, to, amount, denom)
        return signAndBroadcast(listOf(message), memo ?: "", TxBuilder.DEFAULT_GAS_LIMIT, signer)
    }

    suspend fun deposit(asset: Asset, amount: BigInteger, memo: String, signer: Signer): String {
        val message = TxBuilder.msgDeposit(asset, amount, memo, address)
        return signAndBroadcast(listOf(message), "", TxBuilder.DEPOSIT_GAS_LIMIT, signer)
    }

    private suspend fun signAndBroadcast(
        messages: List<com.google.protobuf.Any>,
        memo: String,
        gasLimit: Long,
        signer: Signer
    ): String {
        val signerAddress = Address(address.prefix, io.horizontalsystems.hdwalletkit.Utils.sha256Hash160(signer.publicKey))
        if (signerAddress != address) {
            throw SendError.SignerMismatch()
        }

        val account = thornodeApiProvider.fetchAccount(address)
            ?: throw SendError.AccountNotFound()

        val chainId = this.chainId
            ?: thornodeApiProvider.fetchChainId().also { this.chainId = it }

        val txRaw = TxBuilder.buildSigned(
            messages = messages,
            memo = memo,
            accountNumber = account.accountNumber,
            sequence = account.sequence,
            chainId = chainId,
            gasLimit = gasLimit,
            signer = signer
        )

        return thornodeApiProvider.broadcast(txRaw)
    }

    sealed class SendError : Throwable() {
        // the account has never received funds, so it does not exist on-chain yet
        class AccountNotFound : SendError()

        // the signer's key does not derive the kit's address (e.g. watch-account kit)
        class SignerMismatch : SendError()
    }
}
