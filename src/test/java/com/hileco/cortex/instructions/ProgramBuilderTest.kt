package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test

class ProgramBuilderTest {
    @Test
    fun test() {
        val programBuilder = ProgramBuilder()
        with(programBuilder) {
            jumpDestination("repeat")
            jumpIf(equals(load(CALL_DATA, push(0)), push(123)), "repeat")
        }
        Assert.assertEquals(listOf(
                JUMP_DESTINATION(),
                PUSH(0),
                LOAD(CALL_DATA),
                PUSH(123),
                EQUALS(),
                PUSH(0),
                JUMP_IF()
        ), programBuilder.build())
    }
}