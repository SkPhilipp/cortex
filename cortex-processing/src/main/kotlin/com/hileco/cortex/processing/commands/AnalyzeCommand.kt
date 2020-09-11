package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.processing.database.AnalysisReportModel
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.PathTreeExploreStrategy
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine

class AnalyzeCommand : CliktCommand(name = "analyze", help = "Analyze the next available program") {
    private val network: NetworkModel by optionNetwork()
    private val programAddress: String by optionAddress().required()

    private enum class AnalysisStage {
        EXPLORING,
        SOLVING,
        SOLVED
    }

    override fun run() {
        val modelClient = ModelClient()
        val ethereumParser = EthereumParser()
        val ethereumTranspiler = EthereumTranspiler()
        val program = program(network, programAddress)
        var stage = AnalysisStage.EXPLORING
        val report = try {
            val ethereumInstructions = ethereumParser.parse(program.bytecode.deserializeBytes())
            val instructions = ethereumTranspiler.transpile(ethereumInstructions)
            val symbolicProgram = SymbolicProgram(instructions)
            val symbolicProgramContext = SymbolicProgramContext(symbolicProgram)
            val symbolicVirtualMachine = SymbolicVirtualMachine(symbolicProgramContext)
            val exploreStrategy = PathTreeExploreStrategy()
            val symbolicProgramExplorer = SymbolicProgramExplorer(exploreStrategy)
            @Suppress("UNUSED_VALUE")
            stage = AnalysisStage.EXPLORING
            symbolicProgramExplorer.explore(symbolicVirtualMachine)
            @Suppress("UNUSED_VALUE")
            stage = AnalysisStage.SOLVING
            val solution = exploreStrategy.solve()
            stage = AnalysisStage.SOLVED
            AnalysisReportModel(
                    type = ANALYSIS_REPORT_TYPE,
                    completed = true,
                    solution = solution.values.toString(),
                    solvable = solution.solvable
            )
        } catch (e: Exception) {
            AnalysisReportModel(
                    type = ANALYSIS_REPORT_TYPE,
                    completed = false,
                    errorStage = stage.name,
                    errorCause = e.message
            )
        }
        Logger.logger.log(program, "Analysis complete: $report")
        program.analyses.add(report)
        modelClient.programUpdate(program)
    }

    companion object {
        private const val ANALYSIS_REPORT_TYPE = "EXPLORE"
    }
}
