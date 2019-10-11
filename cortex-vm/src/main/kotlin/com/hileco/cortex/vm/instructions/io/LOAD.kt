package com.hileco.cortex.vm.instructions.io

import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.instructions.StackParameter

class LOAD(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    companion object {
        val ADDRESS = StackParameter("address", 0)
        const val SIZE = 32
    }
}

