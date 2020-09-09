package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient


class ProcessReset {
    fun run() {
        ModelClient.databaseClient.reset()
    }
}
