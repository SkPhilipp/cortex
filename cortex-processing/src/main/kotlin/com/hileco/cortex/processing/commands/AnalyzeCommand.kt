package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.defaultByName
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.collections.serialize
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.processing.database.AnalysisReportModel
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.CustomExploreStrategy
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.collections.backed.BackedInteger
import com.hileco.cortex.collections.backed.toBackedInteger
import java.io.PrintWriter
import java.io.StringWriter

class AnalyzeCommand : CliktCommand(name = "analyze", help = "Analyze the next available program") {
    private val selection by option()
            .groupChoice("address" to AddressSelectionContext(), "blocks" to BlocksSelectionContext())
            .defaultByName("address")
    private val beneficiaryAddress: BackedInteger by option(help = "Address which is to receive the digital asset")
            .backedInteger()
            .default("0xdeadd00d".toBackedInteger())

    // TODO: Analysis timeout

    override fun run() {
        val modelClient = ModelClient()
        val programSelection = selection.programs(modelClient)
        programSelection.forEachRemaining { program ->
            Logger.logger.log(program, "Analyzing")
            val report = analyze(program)
            Logger.logger.log(program, "Analysis complete: $report")
            program.analyses.add(report)
            modelClient.programUpdate(program)
        }
    }

    private fun reportException(exceptions: List<Exception>): AnalysisReportModel {
        val stringWriter = StringWriter()
        exceptions.forEach { exception ->
            exception.printStackTrace(PrintWriter(stringWriter))
        }
        return AnalysisReportModel(
                type = REPORT_TYPE_EXPLORE,
                completed = false,
                errorCause = stringWriter.toString()
        )
    }

    private fun analyze(program: ProgramModel): AnalysisReportModel {
        try {
            val ethereumParser = EthereumParser()
            val ethereumInstructions = ethereumParser.parse(program.bytecode.deserializeBytes())
            val ethereumTranspiler = EthereumTranspiler()
            val instructions = ethereumTranspiler.transpile(ethereumInstructions)
            val symbolicProgram = SymbolicProgram(instructions)
            val symbolicProgramContext = SymbolicProgramContext(symbolicProgram)
            val symbolicVirtualMachine = SymbolicVirtualMachine(symbolicProgramContext)
            val exploreStrategy = CustomExploreStrategy()
            exploreStrategy.withCompleteFilter { svm -> !svm.transfers.isEmpty() }
            exploreStrategy.withCondition { svm ->
                val valueTransferConditions = svm.transfers.asSequence()
                        .map { symbolicTransfer ->
                            val targetIsExplorer = Expression.Equals(symbolicTransfer.target, Expression.Value(beneficiaryAddress))
                            val valueIsPositive = Expression.GreaterThan(symbolicTransfer.value, Expression.Value(BackedInteger.ZERO_32))
                            Expression.constructAnd(listOf(targetIsExplorer, valueIsPositive))
                        }
                        .toList()
                Expression.constructOr(valueTransferConditions)
            }
            exploreStrategy.withCondition {
                val callDataSize = Expression.Variable("CALL_DATA_SIZE", 256)
                Expression.LessThan(callDataSize, Expression.Value(5000.toBackedInteger()))
            }
            val symbolicProgramExplorer = SymbolicProgramExplorer(exploreStrategy)
            symbolicProgramExplorer.explore(symbolicVirtualMachine)
            val solution = exploreStrategy.solve()
            if (!solution.solvable && exploreStrategy.exceptions().isNotEmpty()) {
                return reportException(exploreStrategy.exceptions())
            }
            return AnalysisReportModel(
                    type = REPORT_TYPE_EXPLORE,
                    completed = true,
                    solution = solution.values.mapValues { entry -> entry.value.serialize() }.toString(),
                    solvable = solution.solvable
            )
        } catch (e: Exception) {
            return reportException(listOf(e))
        }
    }

    companion object {
        private const val REPORT_TYPE_EXPLORE = "EXPLORE"
    }
}
