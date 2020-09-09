package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.processes.Logger.Companion.logger

class ProcessReport {
    private val modelClient = ModelClient()

    fun run(programAddress: String) {
        val networkModel = modelClient.networkProcessing() ?: return
        val programModel = modelClient.program(networkModel, programAddress)
        if (programModel == null) {
            logger.log(networkModel, "No program with address $programAddress")
            return
        }
        logger.log(networkModel, "Reports:")
        programModel.analyses.forEach { report ->
            logger.log(programModel, report.toString())
        }
    }
}
