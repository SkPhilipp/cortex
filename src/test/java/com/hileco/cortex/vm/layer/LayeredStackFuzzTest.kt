package com.hileco.cortex.vm.layer

import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class LayeredStackFuzzTest {

    private val random = Random()

    /**
     * Constructs a list of stacks containing distinct elements, with an inheritance depth of size [size].
     */
    private fun construct(size: Int,
                          branches: Int = 2,
                          stack: LayeredStack<Int> = LayeredStack(),
                          idReference: AtomicInteger = AtomicInteger(0),
                          appender: (part: LayeredStack<Int>, id: Int) -> Unit = { part, id -> part.push(id) }): Set<LayeredStack<Int>> {
        appender(stack, idReference.incrementAndGet())
        return if (size > 0) {
            val setA = construct(size - 1, branches, stack, idReference, appender)
            var resultSet = setA
            for (i in 0 until branches) {
                val branch = stack.branch()
                val setB = construct(size - 1, branches, branch, idReference, appender)
                resultSet = resultSet.union(setB)
            }
            resultSet
        } else {
            setOf(stack)
        }
    }

    @Test
    fun testConstruct() {
        val stacks = construct(5)
        stacks.forEach { stack ->
            Assert.assertTrue("Constructed stacks should be of at least the given size.", 5 <= stack.size())
            Assert.assertEquals("Constructed stacks should consist only of distinct elements", stack.asSequence().count(), stack.asSequence().distinct().count())
        }
    }

    @Test
    fun fuzz() {
        for (fuzzIteration in 1..100) {
            construct(5).shuffled().forEach { stack ->
                for (caseIteration in 1..1000) {
                    when (random.nextInt(5)) {
                        0 -> testPushPeekPop(stack)
                        1 -> testDuplicate(stack)
                        2 -> testSwap(stack)
                        3 -> testGetSet(stack)
                        4 -> stack.branch()
                    }
                }
                if (random.nextBoolean()) {
                    testClear(stack)
                }
                if (random.nextBoolean()) {
                    stack.close()
                }
            }
        }
    }

    private fun testPushPeekPop(stack: LayeredStack<Int>) {
        val element = random.nextInt()
        stack.push(element)
        Assert.assertEquals("peek following push should return the pushed element", element, stack.peek())
        Assert.assertEquals("pop following push should return the pushed element", element, stack.pop())
    }

    private fun testDuplicate(stack: LayeredStack<Int>) {
        val offset = random.nextInt(5)
        val element = stack.peek(offset)
        stack.duplicate(offset)
        Assert.assertEquals("peek should return the duplicated element", element, stack.peek())
    }

    private fun testSwap(stack: LayeredStack<Int>) {
        val offsetA = random.nextInt(5)
        val offsetB = random.nextInt(5)
        val elementA = stack.peek(offsetA)
        val elementB = stack.peek(offsetB)
        stack.swap(offsetA, offsetB)
        Assert.assertEquals("elementA should take the position of offsetB", elementA, stack.peek(offsetB))
        Assert.assertEquals("elementB should take the position of offsetA", elementB, stack.peek(offsetA))
    }

    private fun testGetSet(stack: LayeredStack<Int>) {
        val index = random.nextInt(5)
        val element = stack[index]
        stack[index] = element + 1
        Assert.assertEquals("the element should be incremented", element + 1, stack[index])
    }

    private fun testClear(stack: LayeredStack<Int>) {
        stack.clear()
        Assert.assertEquals("after clearing, the stack should be empty", 0, stack.size())
        Assert.assertTrue("after clearing, the stack should be empty", stack.isEmpty())
    }
}