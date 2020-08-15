package com.hileco.cortex.processing.processes

import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.ethereum.deserializeBytes
import com.hileco.cortex.processing.database.AnalysisReportModel
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.PathTreeExploreStrategy
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine

class ProgramAnalysisProcess : BaseProcess() {
    private val modelClient = ModelClient()
    private val ethereumParser = EthereumParser()
    private val ethereumTranspiler = EthereumTranspiler()

    private enum class AnalysisStage {
        EXPLORING,
        SOLVING,
        SOLVED
    }

    @Suppress("UNUSED_VALUE")
    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        val programModel = modelClient.programLeastRecentUnanalyzed(networkModel) ?: return
        var stage = AnalysisStage.EXPLORING
        val report = try {
            val ethereumInstructions = ethereumParser.parse(programModel.bytecode.deserializeBytes())
            val instructions = ethereumTranspiler.transpile(ethereumInstructions)
            val symbolicProgram = SymbolicProgram(instructions)
            val symbolicProgramContext = SymbolicProgramContext(symbolicProgram)
            val symbolicVirtualMachine = SymbolicVirtualMachine(symbolicProgramContext)
            val exploreStrategy = PathTreeExploreStrategy()
            val symbolicProgramExplorer = SymbolicProgramExplorer(exploreStrategy)
            stage = AnalysisStage.EXPLORING
            symbolicProgramExplorer.explore(symbolicVirtualMachine)
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
        programModel.analyses.add(report)
        modelClient.programUpdate(programModel)
    }

    companion object {
        private const val ANALYSIS_REPORT_TYPE = "EXPLORE"
    }
}