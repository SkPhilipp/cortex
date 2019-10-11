package com.hileco.cortex.vm.instructions.io

import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.instructions.StackParameter

class SAVE(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, BYTES)

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val BYTES = StackParameter("bytes", 1)
    }
}
