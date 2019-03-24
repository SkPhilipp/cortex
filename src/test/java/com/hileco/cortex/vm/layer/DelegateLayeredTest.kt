package com.hileco.cortex.vm.layer

import com.hileco.cortex.vm.layer.test.DelegateLayeredStack
import org.junit.Assert
import org.junit.Test

class DelegateLayeredTest {
    @Test
    fun testParentLinkage() {
        val delegateLayeredStack = DelegateLayeredStack<Int>()
        delegateLayeredStack.delegate.push(1)
        val branchedDelegateLayeredStack = delegateLayeredStack.branch()
        branchedDelegateLayeredStack.delegate.push(2)
        delegateLayeredStack.delegate.push(3)
        Assert.assertEquals(delegateLayeredStack.delegate.asSequence().toList(), listOf(1, 3))
        Assert.assertEquals(branchedDelegateLayeredStack.delegate.asSequence().toList(), listOf(1, 2))
        Assert.assertNotNull(delegateLayeredStack.parent())
        Assert.assertEquals(delegateLayeredStack.parent(), branchedDelegateLayeredStack.parent())
        Assert.assertEquals(delegateLayeredStack.parent().delegate.asSequence().toList(), listOf(1))
    }

    @Test
    fun testChildLinkage() {
        val delegateLayeredStack = DelegateLayeredStack<Int>()
        val branchedDelegateLayeredStack = delegateLayeredStack.branch()
        Assert.assertNotNull(delegateLayeredStack.parent().children().single { it === delegateLayeredStack })
        Assert.assertNotNull(delegateLayeredStack.parent().children().single { it === branchedDelegateLayeredStack })
    }

    @Test
    fun testChildClosing() {
        val delegateLayeredStack = DelegateLayeredStack<Int>()
        val branchedDelegateLayeredStack = delegateLayeredStack.branch()
        delegateLayeredStack.dispose()
        Assert.assertNull(delegateLayeredStack.parent().children().singleOrNull { it === delegateLayeredStack })
        Assert.assertNotNull(delegateLayeredStack.parent().children().single { it === branchedDelegateLayeredStack })
    }
}