package com.hileco.cortex.analysis.explore.strategy

import com.hileco.cortex.analysis.explore.strategies.PathTreeConditionBuilder
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.layer.LayeredStack
import com.hileco.cortex.vm.layer.LayeredStack.Companion.MINIMUM_LAYER_SIZE
import com.hileco.cortex.vm.symbolic.SymbolicPathEntry
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.core.AnyOf.anyOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PathTreeConditionBuilderTest {
    private val exploreConditionBuilder = PathTreeConditionBuilder()
    private val originalLayeredStackMinimumLayerSize = MINIMUM_LAYER_SIZE

    private fun testExpression(constant: Long): Expression {
        return Equals(Reference(ProgramStoreZone.CALL_DATA, Value(constant)), Value(constant))
    }

    /**
     * Configure [LayeredStack] to never merge, to allow for comparing tree structures.
     */
    @Before
    fun before() {
        LayeredStack.MINIMUM_LAYER_SIZE = 0
    }

    /**
     * Reconfigure [LayeredStack] to its original state.
     */
    @After
    fun after() {
        LayeredStack.MINIMUM_LAYER_SIZE = originalLayeredStackMinimumLayerSize
    }

    @Test
    fun testRootWithBranches() {
        val stack = LayeredStack<SymbolicPathEntry>()
        stack.push(SymbolicPathEntry(0, Value(0), true, testExpression(0)))
        val branch = stack.branch()
        stack.push(SymbolicPathEntry(0, Value(0), true, testExpression(1)))
        branch.push(SymbolicPathEntry(0, Value(0), true, testExpression(2)))
        val treeExpression = exploreConditionBuilder.build(listOf(stack, branch))
        val expectedExpression = And(listOf(testExpression(0), Or(listOf(testExpression(1), testExpression(2)))))
        val expectedExpressionReordered = And(listOf(testExpression(0), Or(listOf(testExpression(2), testExpression(1)))))
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
        val stack = LayeredStack<SymbolicPathEntry>()
        stack.push(SymbolicPathEntry(0, Value(0), true, testExpression(0)))
        val emptyBranch = stack.branch()
        stack.push(SymbolicPathEntry(0, Value(0), true, testExpression(1)))
        val treeExpression = exploreConditionBuilder.build(listOf(stack, emptyBranch))
        val expectedExpression = testExpression(0)
        Assert.assertEquals("$expectedExpression", "$treeExpression")
    }
}