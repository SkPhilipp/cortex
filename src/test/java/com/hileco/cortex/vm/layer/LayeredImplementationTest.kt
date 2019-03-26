package com.hileco.cortex.vm.layer

import com.hileco.cortex.vm.layer.test.DelegateLayeredStack
import com.hileco.cortex.vm.layer.test.LayeredNavigator
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class LayeredImplementationTest {
    private val layeredNavigator = LayeredNavigator()

    private fun <T : Layered<T>> test(source: T,
                                      postProcessor: (T) -> Unit = { _ -> }) {
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 0)

        /**
         * x -> source
         * x -> branch
         */
        postProcessor(source)
        val branch = source.branch()
        postProcessor(branch)
        postProcessor(source)

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.depth(branch), 1)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 1)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch), 0)
        Assert.assertEquals(source.parent(), branch.parent())
        Assert.assertEquals(source.parent()!!.children().size, 2)
        /**
         * x -> source
         * x -> y -> branch
         * x -> y -> branch2
         */
        val branch2 = branch.branch()
        postProcessor(branch)
        postProcessor(branch2)

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.depth(branch), 2)
        Assert.assertEquals(layeredNavigator.depth(branch2), 2)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 2)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch2), 0)
        Assert.assertEquals(layeredNavigator.root(source), layeredNavigator.root(branch))
        Assert.assertEquals(layeredNavigator.root(branch), layeredNavigator.root(branch2))
        Assert.assertEquals(branch.parent(), branch2.parent())
        Assert.assertEquals(source.parent()!!.children().size, 2)
        Assert.assertEquals(branch.parent()!!.children().size, 2)

        /**
         * x -> source
         * x -> y -> branch
         * x -> y -> z -> branch2
         * x -> y -> z -> branch3
         */
        val branch3 = branch2.branch()
        postProcessor(branch2)
        postProcessor(branch3)

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.depth(branch), 2)
        Assert.assertEquals(layeredNavigator.depth(branch2), 3)
        Assert.assertEquals(layeredNavigator.depth(branch3), 3)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 3)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch2), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch3), 0)
        Assert.assertEquals(layeredNavigator.root(source), layeredNavigator.root(branch))
        Assert.assertEquals(layeredNavigator.root(branch), layeredNavigator.root(branch2))
        Assert.assertEquals(layeredNavigator.root(branch2), layeredNavigator.root(branch3))
        Assert.assertEquals(branch2.parent(), branch3.parent())
        Assert.assertEquals(source.parent()!!.children().size, 2)
        Assert.assertEquals(branch.parent()!!.children().size, 2)
        Assert.assertEquals(branch2.parent()!!.children().size, 2)

        /**
         * x -> source
         * x -> y -> branch
         * x -> y -> z -> branch2 (or x -> y -> z+branch2 when mergesOnSingleChild)
         */
        branch3.dispose()

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.depth(branch), 2)
        Assert.assertEquals(layeredNavigator.depth(branch2), 3)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 3)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch2), 0)
        Assert.assertEquals(layeredNavigator.root(source), layeredNavigator.root(branch))
        Assert.assertEquals(layeredNavigator.root(branch), layeredNavigator.root(branch2))
        /**
         * x -> source
         * x -> y -> branch (or x -> y+branch when mergesOnSingleChild)
         */
        branch2.dispose()

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.depth(branch), 2)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 2)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
        Assert.assertEquals(layeredNavigator.remainingDepth(branch), 0)
        Assert.assertEquals(layeredNavigator.root(source), layeredNavigator.root(branch))

        /**
         * x -> source (or x+source when mergesOnSingleChild)
         */
        branch.dispose()

        Assert.assertEquals(layeredNavigator.depth(source), 1)
        Assert.assertEquals(layeredNavigator.remainingDepth(layeredNavigator.root(source)), 1)
        Assert.assertEquals(layeredNavigator.remainingDepth(source), 0)
    }

    @Test
    fun testLayeredStack() {
        test(LayeredStack<Int>(), postProcessor = { stack ->
            stack.push(1)
            stack.push(2)
            stack.push(3)
            stack.push(4)
            stack.push(5)
            stack.push(6)
            stack.push(7)
        })
    }

    @Test
    fun testDelegateLayeredStack() {
        test(DelegateLayeredStack<Int>())
    }

    @Test
    fun testLayeredBytes() {
        test(LayeredBytes())
    }

    @Test
    fun testLayeredMap() {
        val counter = AtomicInteger()
        test(LayeredMap<Int, Int>(), postProcessor = { stack ->
            stack[counter.incrementAndGet()] = counter.get()
            stack[counter.incrementAndGet()] = counter.get()
            stack[counter.incrementAndGet()] = counter.get()
            stack[counter.incrementAndGet()] = counter.get()
            stack[counter.incrementAndGet()] = counter.get()
        })
    }
}