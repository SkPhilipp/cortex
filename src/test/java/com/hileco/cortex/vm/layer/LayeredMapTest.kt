package com.hileco.cortex.vm.layer

import org.junit.Assert.*
import org.junit.Test

class LayeredMapTest {
    @Test
    fun testOperations() {
        val map = LayeredMap<Int, Int>()
        map[10] = 100
        map[20] = 200
        assertEquals(100, map[10])
        assertEquals(200, map[20])
        assertEquals(2, map.size())
    }

    @Test
    fun testInheritance() {
        val root = LayeredMap<Int, Int>()
        root[10] = 100
        root[20] = 200
        root[30] = 300
        val child = LayeredMap(root)
        child[10] = 1000
        child.remove(20)
        assertEquals(1000, child[10])
        assertNull(child[20])
        assertEquals(300, child[30])
        assertEquals(2, child.size())
    }

    @Test
    fun testKeySet() {
        val map = LayeredMap<Int, Int>()
        map[10] = 100
        map[20] = 200
        val keySet = map.keySet()
        assertEquals(2, keySet.size)
        assertTrue(keySet.contains(10))
        assertTrue(keySet.contains(20))
    }

    @Test
    fun testEquals() {
        val root = LayeredMap<Int, Int>()
        root[10] = 100
        root[20] = 200
        root[30] = 300
        val childA = LayeredMap(root)
        childA.remove(30)
        val mapB = LayeredMap<Int, Int>()
        mapB[10] = 100
        mapB[20] = 200
        assertEquals(mapB, childA)
    }
}