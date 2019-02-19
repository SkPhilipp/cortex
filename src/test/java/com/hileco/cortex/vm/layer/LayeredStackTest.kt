package com.hileco.cortex.vm.layer

import org.junit.Assert.*
import org.junit.Test

class LayeredStackTest {
    @Test
    fun testInheritance() {
        val rootStack = LayeredStack<Int>()
        rootStack.push(1)
        val stackA = LayeredStack(rootStack)
        stackA.push(2)
        val stackB = LayeredStack(rootStack)
        assertEquals(2, stackA.pop())
        assertEquals(1, stackA.pop())
        assertTrue(stackA.isEmpty())
        assertEquals(1, stackB.pop())
        assertTrue(stackB.isEmpty())
        assertEquals(1, rootStack.pop())
        assertTrue(rootStack.isEmpty())
    }

    @Test
    fun testMapOperations() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        stack[0] = 123
        stack[1] = 456
        assertEquals(123, stack[0])
        assertEquals(456, stack[1])
        assertEquals(2, stack.size())
    }

    @Test
    fun testStackOperations() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        stack.swap(0, 1)
        assertEquals(100, stack[1])
        assertEquals(500, stack[0])
    }

    @Test
    fun testIsEmpty() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        assertFalse(stack.isEmpty())
        stack.pop()
        stack.pop()
        assertTrue(stack.isEmpty())
    }

    @Test
    fun testDuplicate() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        stack.duplicate(1)
        stack.duplicate(1)
        assertEquals(100, stack[0])
        assertEquals(500, stack[1])
        assertEquals(100, stack[2])
        assertEquals(500, stack[3])
    }

    @Test
    fun testSequence() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        val list = stack.asSequence().toList()
        assertEquals(list, listOf(100, 500))
    }

    @Test
    fun testClear() {
        val stack = LayeredStack<Int>()
        stack.push(100)
        stack.push(500)
        stack.clear()
        assertTrue(stack.isEmpty())
    }

    @Test
    fun testEquals() {
        val stackA = LayeredStack<Int>()
        stackA.push(100)
        stackA.push(500)
        val rootStack = LayeredStack<Int>()
        rootStack.push(100)
        val stackB = LayeredStack(rootStack)
        stackB.push(500)
        assertEquals(stackA, stackB)
    }
}