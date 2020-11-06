package com.hileco.cortex.symbolic.instructions.io

import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.symbolic.ProgramStoreZone.MEMORY
import com.hileco.cortex.symbolic.instructions.StackParameter

/**
 * Indicates byte for byte copy of another program's program store zone to this program's [MEMORY].
 */
class EXTERNAL_COPY(val sourceProgramStoreZone: ProgramStoreZone) : IoInstruction(MEMORY) {
    override val stackAdds: List<Int>
        get() = listOf()

    override val stackParameters: List<StackParameter>
        get() = listOf(SOURCE_ADDRESS, DESTINATION_OFFSET, OFFSET, LENGTH)

    companion object {
        val SOURCE_ADDRESS = StackParameter("sourceAddress", 0)
        val DESTINATION_OFFSET = StackParameter("destinationOffset", 0)
        val OFFSET = StackParameter("offset", 1)
        val LENGTH = StackParameter("length", 2)
        const val SIZE = 32
    }
}
