package com.hileco.cortex.processing.web3rpc

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult
import org.web3j.protocol.http.HttpService
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import java.math.BigInteger

class Web3Client(endpoint: String) {
    private val web3j: Web3j = Web3j.build(HttpService(endpoint))
    private val transactionReceiptProcessor = PollingTransactionReceiptProcessor(web3j, 100, 100)

    fun loadNetworkId(): String {
        val ethNetVersion = web3j.netVersion().send()
        return ethNetVersion.netVersion
    }

    private fun loadAccount(): String {
        val ethAccounts = web3j.ethAccounts().send()
        return ethAccounts.accounts.first() ?: throw IllegalStateException("No accounts available")
    }

    private fun loadNOnce(account: String): BigInteger? {
        val ethGetTransactionCount = web3j.ethGetTransactionCount(account, DefaultBlockParameterName.LATEST).send()
        return ethGetTransactionCount.transactionCount
    }

    fun loadBlockNumber(): BigInteger {
        val ethBlockNumber = web3j.ethBlockNumber().send()
        return ethBlockNumber.blockNumber
    }

    fun createContract(bytecode: String): String {
        val account = loadAccount()
        val nOnce = loadNOnce(account)
        val gas = BigInteger.valueOf(4000000)
        val contractCreateTransaction = Transaction.createContractTransaction(account, nOnce, gas, bytecode)
        val ethTransaction = web3j.ethSendTransaction(contractCreateTransaction).send()
        return transactionReceiptProcessor.waitForTransactionReceipt(ethTransaction.transactionHash).transactionHash
    }

    fun sendWei(beneficiary: String, amount: BigInteger): String {
        val account = loadAccount()
        val nOnce = loadNOnce(account)
        val gas = BigInteger.valueOf(21000)
        val contractCreateTransaction = Transaction.createEtherTransaction(account, nOnce, gas, gas, beneficiary, amount)
        val ethTransaction = web3j.ethSendTransaction(contractCreateTransaction).send()
        return transactionReceiptProcessor.waitForTransactionReceipt(ethTransaction.transactionHash).transactionHash
    }

    private fun loadContract(blockContent: BigInteger, blockCreated: BigInteger, transactionHash: String): Web3Contract? {
        val ethTransactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(transactionHash)
        if (ethTransactionReceipt.gasUsed.toLong() > GAS_CONTRACT_CREATE + GAS_TRANSACTION_CREATE) {
            val blockNumberContentParameter = DefaultBlockParameter.valueOf(blockContent)
            val ethCode = web3j.ethGetCode(ethTransactionReceipt.contractAddress, blockNumberContentParameter).send()
            val ethGetBalance = web3j.ethGetBalance(ethTransactionReceipt.contractAddress, blockNumberContentParameter).send()
            return Web3Contract(
                    transactionHash,
                    ethCode.code,
                    ethTransactionReceipt.contractAddress,
                    ethGetBalance.balance,
                    blockCreated,
                    blockContent
            )
        }
        return null
    }

    fun loadContracts(blockStart: Long, blockEnd: Long): List<Web3Contract> {
        val contracts = mutableListOf<Web3Contract>()
        val blockNumberLatest = loadBlockNumber()
        for (blockNumber in blockStart..blockEnd) {
            val ethBlockNumber = DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber))
            val ethBlock = web3j.ethGetBlockByNumber(ethBlockNumber, false).send()
            ethBlock.block.transactions.asSequence().forEach { ethTransaction: TransactionResult<*> ->
                if (ethTransaction is EthBlock.TransactionHash) {
                    val contract = loadContract(blockNumberLatest, BigInteger.valueOf(blockNumber), ethTransaction.get())
                    if (contract != null) {
                        contracts.add(contract)
                    }
                }
            }
        }
        return contracts
    }

    companion object {
        const val GAS_TRANSACTION_CREATE = 21000
        const val GAS_CONTRACT_CREATE = 32000
    }
}
