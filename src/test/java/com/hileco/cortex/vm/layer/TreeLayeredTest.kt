package com.hileco.cortex.vm.layer

import org.junit.Assert
import org.junit.Test

class TestTreeLayered(private val empty: Boolean, parent: TestTreeLayered? = null) : TreeLayered<TestTreeLayered>(parent) {
    override fun extractParentLayer(parent: TestTreeLayered?): TestTreeLayered {
        return TestTreeLayered(empty, parent)
    }

    override fun isLayerEmpty(): Boolean {
        return empty
    }

    override fun createSibling(): TestTreeLayered {
        return TestTreeLayered(empty, parent())
    }

    override fun mergeParent() {
    }
}

class TreeLayeredTest {
    @Test
    fun testBranch() {
        val testLayered = TestTreeLayered(false)
        val branchLayered = testLayered.branch()
        val parent = testLayered.parent()
        Assert.assertNotNull(parent)
        Assert.assertEquals(parent, branchLayered.parent())
        Assert.assertTrue(parent.children().containsAll(listOf(testLayered, branchLayered)))
    }

    @Test
    fun testBranchEmptyLayer() {
        val testLayered = TestTreeLayered(true)
        val branchLayered = testLayered.branch()
        Assert.assertEquals(testLayered.parent(), testLayered)
        Assert.assertEquals(branchLayered.parent(), branchLayered)
    }

    @Test
    fun testClose() {
        val testLayered = TestTreeLayered(false)
        val branchLayered = testLayered.branch()
        branchLayered.dispose()
        val parent = testLayered.parent()
        Assert.assertEquals(testLayered.parent(), parent)
    }
}