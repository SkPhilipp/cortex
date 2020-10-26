package com.hileco.cortex.processing.web3rpc

import com.hileco.cortex.processing.database.Network
import com.hileco.cortex.processing.web3rpc.parallelism.ParallelTask
import okhttp3.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger


class Web3Client(network: Network) {
    private val web3j: Web3j
    private val transactionReceiptProcessor: PollingTransactionReceiptProcessor

    init {
        val clientBuilder = OkHttpClient.Builder()

        if (network.credentials != null) {
            clientBuilder.authenticator(object : Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    val credential: String = Credentials.basic(network.credentials.first, network.credentials.second)
                    return response.request.newBuilder().header("Authorization", credential).build()
                }
            })
        }
        val service = HttpService(network.endpoint, clientBuilder.build(), false)
        this.web3j = Web3j.build(service)
        this.transactionReceiptProcessor = PollingTransactionReceiptProcessor(web3j, 100, 100)
    }


    fun loadNetworkId(): String {
        val ethNetVersion = web3j.netVersion().send()
        return ethNetVersion.netVersion
    }

    private fun loadAccount(): String {
        val ethAccounts = web3j.ethAccounts().send()
        return ethAccounts.accounts.first() ?: throw IllegalStateException("No accounts available")
    }

    private fun loadNOnce(account: String): BigInteger? {
        val ethGetTransactionCount = web3j.ethGetTransactionCount(account, LATEST).send()
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

    private fun loadContract(transactionReceipt: TransactionReceipt): Web3Contract? {
        val ethCode = web3j.ethGetCode(transactionReceipt.contractAddress, LATEST).send()
        if (ethCode.code == EMPTY_CONTRACT) {
            return null
        }
        val ethGetBalance = web3j.ethGetBalance(transactionReceipt.contractAddress, LATEST).send()
        return Web3Contract(
                transactionReceipt.transactionHash,
                ethCode.code,
                transactionReceipt.contractAddress,
                ethGetBalance.balance,
                transactionReceipt.blockNumber
        )
    }

    private fun couldCreateContract(transactionObject: EthBlock.TransactionObject): Boolean {
        return transactionObject.gas.toLong() > GAS_CONTRACT_CREATE + GAS_TRANSACTION_CREATE
    }

    private fun loadContracts(ethBlock: EthBlock): Sequence<Web3Contract> {
        return ethBlock.block.transactions.asSequence()
                .filterIsInstance(EthBlock.TransactionObject::class.java)
                .filter { couldCreateContract(it) }
                .map { transactionReceiptProcessor.waitForTransactionReceipt(it.hash) }
                .filter { it.contractAddress != null }
                .mapNotNull { loadContract(it) }
    }

    fun loadContracts(blockStart: Long,
                      blockEnd: Long,
                      threads: Int,
                      onContractLoaded: (Web3Contract) -> Unit): ParallelTask {
        val totalErrors = AtomicInteger(0)
        val parallelTask = ParallelTask(blockStart, blockEnd, threads, onError = {
            val currentTotalErrors = totalErrors.incrementAndGet()
            it.printStackTrace()
            currentTotalErrors < 100
        }) { blockNumber ->
            val ethBlockNumber = DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber))
            val ethBlock = web3j.ethGetBlockByNumber(ethBlockNumber, true).send()
            loadContracts(ethBlock).forEach {
                onContractLoaded(it)
            }
            totalErrors.getAndUpdate { operand -> (operand - 1).coerceAtLeast(0) }
        }
        parallelTask.start()
        return parallelTask
    }

    companion object {
        const val EMPTY_CONTRACT = "0x"
        const val GAS_TRANSACTION_CREATE = 21000
        const val GAS_CONTRACT_CREATE = 32000
    }
}
