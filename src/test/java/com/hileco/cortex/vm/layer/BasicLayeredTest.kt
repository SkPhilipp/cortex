package com.hileco.cortex.vm.layer

import org.junit.Assert
import org.junit.Test

class TestLayered(private val empty: Boolean, parent: TestLayered? = null) : BasicLayered<TestLayered>(parent) {
    override fun layerExtract(): TestLayered {
        return TestLayered(empty)
    }

    override fun layerIsEmpty(): Boolean {
        return empty
    }

    override fun layerCreate(parent: TestLayered?): TestLayered {
        return TestLayered(empty, parent)
    }

    override fun layerMergeUpwards() {
    }
}

class BasicLayeredTest {
    @Test
    fun testBranch() {
        val testLayered = TestLayered(false)
        val branchLayered = testLayered.branch()
        val parent = testLayered.parent()
        Assert.assertNotNull(parent)
        Assert.assertEquals(parent, branchLayered.parent())
        Assert.assertTrue(parent!!.children().containsAll(listOf(testLayered, branchLayered)))
    }

    @Test
    fun testBranchEmptyLayer() {
        val testLayered = TestLayered(true)
        val branchLayered = testLayered.branch()
        Assert.assertNull(testLayered.parent())
        Assert.assertNull(branchLayered.parent())
    }

    @Test
    fun testClose() {
        val testLayered = TestLayered(false)
        val branchLayered = testLayered.branch()
        branchLayered.close()
        val parent = testLayered.parent()
        Assert.assertEquals(parent!!.children().size, 1)
        Assert.assertTrue(parent.children().contains(testLayered))
    }
}