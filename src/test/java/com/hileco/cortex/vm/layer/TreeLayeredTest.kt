package com.hileco.cortex.vm.layer

import com.hileco.cortex.vm.layer.test.BasicTreeLayered
import org.junit.Assert
import org.junit.Test


class TreeLayeredTest {
    @Test
    fun testBranch() {
        val testLayered = BasicTreeLayered(false)
        val branchLayered = testLayered.branch()
        val parent = testLayered.parent()
        Assert.assertNotNull(parent)
        Assert.assertEquals(parent, branchLayered.parent())
        Assert.assertTrue(parent.children().containsAll(listOf(testLayered, branchLayered)))
    }

    @Test
    fun testBranchEmptyLayer() {
        val testLayered = BasicTreeLayered(true)
        val branchLayered = testLayered.branch()
        Assert.assertEquals(testLayered.parent(), testLayered)
        Assert.assertEquals(branchLayered.parent(), branchLayered)
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