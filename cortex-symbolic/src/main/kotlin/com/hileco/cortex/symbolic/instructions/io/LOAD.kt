package com.hileco.cortex.symbolic.instructions.io

import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.symbolic.instructions.StackParameter

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

