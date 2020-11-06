package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.Value
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.backed.toBackedInteger
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Test


class PathTreeConditionBuilderTest {
    private val exploreConditionBuilder = PathTreeConditionBuilder()

    private fun testExpression(constant: Long): Expression {
        return Expression.Equals(Expression.VariableExtract(ProgramStoreZone.CALL_DATA, Value(constant.toBackedInteger())), Value(constant.toBackedInteger()))
    }

    @Test
    fun testRootWithBranches() {
        val stack = LayeredVmStack<SymbolicPathEntry>()
        stack.push(SymbolicPathEntry(0, Value(ZERO_32), true, testExpression(0)))
        val branch = stack.copy()
        stack.push(SymbolicPathEntry(0, Value(ZERO_32), true, testExpression(1)))
        branch.push(SymbolicPathEntry(0, Value(ZERO_32), true, testExpression(2)))
        val treeExpression = exploreConditionBuilder.build(listOf(stack.edge, branch.edge))
        val expectedExpression = Expression.And(listOf(testExpression(0), Expression.Or(listOf(testExpression(1), testExpression(2)))))
        val expectedExpressionReordered = Expression.And(listOf(testExpression(0), Expression.Or(listOf(testExpression(2), testExpression(1)))))
        Assert.assertThat("$treeExpression", anyOf(equalTo("$expectedExpression"), equalTo("$expectedExpressionReordered")))
    }

    /**
     * The tree expression should only contain the root layer's expression, as emptyBranch has an empty layer.
     * The complete expression would contain a TRUE:
     *
     *  ((CALL_DATA[0] == 0) && ((CALL_DATA[1] == 1) || (TRUE)))
     *
     * This implicitly means all sibling branches of emptyBranch become irrelevant, expression optimizations
     * then deal with all combining expressions, resulting in:
     *
     *  ((CALL_DATA[0] == 0) && (TRUE))
     *   (CALL_DATA[0] == 0)
     */
    @Test
    fun testRootWithAnEmptyBranch() {
        val stack = LayeredVmStack<SymbolicPathEntry>()
        stack.push(SymbolicPathEntry(0, Value(ZERO_32), true, testExpression(0)))
        val emptyBranch = stack.copy()
        stack.push(SymbolicPathEntry(0, Value(ZERO_32), true, testExpression(1)))
        val treeExpression = exploreConditionBuilder.build(listOf(stack.edge, emptyBranch.edge))
        val expectedExpression = testExpression(0)
        Assert.assertEquals("$expectedExpression", "$treeExpression")
    }
}