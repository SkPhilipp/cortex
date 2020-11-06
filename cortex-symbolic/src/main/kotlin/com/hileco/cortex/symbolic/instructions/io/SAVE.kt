package com.hileco.cortex.symbolic.instructions.io

import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.symbolic.instructions.StackParameter

class SAVE(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, BYTES)

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val BYTES = StackParameter("bytes", 1)
    }
}
