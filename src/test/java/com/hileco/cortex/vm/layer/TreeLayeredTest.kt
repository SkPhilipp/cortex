package com.hileco.cortex.vm.layer

import com.hileco.cortex.vm.layer.test.BasicTreeLayered
import org.junit.Assert
import org.junit.Test


class TreeLayeredTest {
    @Test
    fun testBranch() {
        val testLayered = BasicTreeLayered(false)
        val branchLayered = testLayered.branch()
        val parent = testLayered.parent()!!
        Assert.assertEquals(parent, branchLayered.parent())
        Assert.assertTrue(parent.children().containsAll(listOf(testLayered, branchLayered)))
    }

    @Test
    fun testBranchEmptyLayer() {
        val testLayered = BasicTreeLayered(true)
        val branchLayered = testLayered.branch()
        Assert.assertNull(testLayered.parent())
        Assert.assertNull(branchLayered.parent())
    }

    @Test
    fun testClose() {
        val testLayered = BasicTreeLayered(false)
        val branchLayered = testLayered.branch()
        branchLayered.dispose()
        val parent = testLayered.parent()
        Assert.assertEquals(testLayered.parent(), parent)
    }
}