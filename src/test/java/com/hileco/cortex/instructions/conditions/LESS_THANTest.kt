package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression.True
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class LESS_THANTest : InstructionTest() {

    @Test
    fun symbolicLessThanValueToValue() {
        val result = runSymbolic(LESS_THAN(), Value(1), Value(2))
        Assert.assertEquals(True, result)
    }
}
