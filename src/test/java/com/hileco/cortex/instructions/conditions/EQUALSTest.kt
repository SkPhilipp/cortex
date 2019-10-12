package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression.True
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class EQUALSTest : InstructionTest() {

    @Test
    fun symbolicEqualsValuetoValue() {
        val result = runSymbolic(EQUALS(), Value(1), Value(1))
        Assert.assertEquals(True, result)
    }
}
