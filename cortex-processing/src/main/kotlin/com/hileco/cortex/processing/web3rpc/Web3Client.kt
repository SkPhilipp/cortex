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


class Web3Client {

    private val web3j: Web3j = Web3j.build(HttpService())
    private val transactionReceiptProcessor = PollingTransactionReceiptProcessor(web3j, 100, 100)

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

    fun loadContract(blockContent: BigInteger, blockCreated: BigInteger, transactionHash: String): Web3Contract? {
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
        val blockNumberLatestParameter = DefaultBlockParameter.valueOf(blockNumberLatest)
        for (blockNumber in blockStart..blockEnd) {
            val ethBlockNumber = DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber))
            val ethBlock = web3j.ethGetBlockByNumber(ethBlockNumber, false).send()
            ethBlock.block.transactions.asSequence().forEach { ethTransaction: TransactionResult<*> ->
                if (ethTransaction is EthBlock.TransactionHash) {
                    val ethTransactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(ethTransaction.get())
                    if (ethTransactionReceipt.gasUsed.toLong() > GAS_CONTRACT_CREATE + GAS_TRANSACTION_CREATE) {
                        val ethCode = web3j.ethGetCode(ethTransactionReceipt.contractAddress, blockNumberLatestParameter).send()
                        val ethGetBalance = web3j.ethGetBalance(ethTransactionReceipt.contractAddress, blockNumberLatestParameter).send()
                        contracts.add(Web3Contract(
                                ethTransaction.get(),
                                ethCode.code,
                                ethTransactionReceipt.contractAddress,
                                ethGetBalance.balance,
                                BigInteger.valueOf(blockNumber),
                                blockNumberLatest
                        ))
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

fun main() {
    val web3ContractLoader = Web3Client()
    web3ContractLoader.loadBlockNumber()
    web3ContractLoader.createContract("6080604052348015600f57600080fd5b5060cf8061001e6000396000f3fe608060405260043610601c5760003560e01c8063d0679d3414601e575b005b606760048036036040811015603257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506069565b005b8173ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f1935050505050505056fea165627a7a72305820718d58054961ff1078cc59a06e5c86f0f68a02b3e3320c8b1c6bd665caf06c1b0029")
    val loadContracts = web3ContractLoader.loadContracts(0, web3ContractLoader.loadBlockNumber().toLong())
    loadContracts.forEach { println(it) }
}