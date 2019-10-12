package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import org.junit.Assert
import org.junit.Test

class MODULOTest : InstructionTest() {

    @Test
    fun symbolicModuloValueToValue() {
        val result = runSymbolic(MODULO(), Value(101), Value(10))
        Assert.assertEquals(Value(1), result)
    }
}
