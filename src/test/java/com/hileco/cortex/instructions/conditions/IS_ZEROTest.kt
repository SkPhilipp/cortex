package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression.True
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class IS_ZEROTest : InstructionTest() {

    @Test
    fun symbolicIsZeroValue() {
        val result = runSymbolic(IS_ZERO(), Value(0))
        Assert.assertEquals(True, result)
    }
}
