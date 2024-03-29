package com.hileco.cortex.collections

import org.junit.Assert
import org.junit.Test

class BranchedMapTest {

    @Test
    fun fuzz() {
        Variation.fuzzed(100) { variation ->
            val vmMap = BranchedMap<Int, String>()
            variation.maybe { vmMap.copy() }
            vmMap[1] = "1"
            variation.maybe { vmMap.copy() }
            vmMap[2] = "2"
            variation.maybe { vmMap.copy() }
            vmMap[3] = "3"
            variation.maybe { vmMap.copy() }
            Assert.assertEquals("1", vmMap[1])
            Assert.assertEquals("2", vmMap[2])
            Assert.assertEquals("3", vmMap[3])
            variation.maybe { vmMap.copy() }
            vmMap.remove(1)
            variation.maybe { vmMap.copy() }
            vmMap.remove(2)
            variation.maybe { vmMap.copy() }
            vmMap.remove(3)
            variation.maybe { vmMap.copy() }
            Assert.assertNull(vmMap[1])
            Assert.assertNull(vmMap[2])
            Assert.assertNull(vmMap[3])
        }
    }

    @Test
    fun testOperations() {
        val map = BranchedMap<Int, Int>()
        map[10] = 100
        map[20] = 200
        Assert.assertEquals(100, map[10])
        Assert.assertEquals(200, map[20])
        Assert.assertEquals(2, map.size)
    }

    @Test
    fun testInheritance() {
        val root = BranchedMap<Int, Int>()
        root[10] = 100
        root[20] = 200
        root[30] = 300
        val child = root.copy()
        child[10] = 1000
        child.remove(20)
        Assert.assertEquals(1000, child[10])
        Assert.assertNull(child[20])
        Assert.assertEquals(300, child[30])
        Assert.assertEquals(2, child.size)
    }

    @Test
    fun testKeySet() {
        val map = BranchedMap<Int, Int>()
        map[10] = 100
        map[20] = 200
        val keySet = map.keys
        Assert.assertEquals(2, keySet.size)
        Assert.assertTrue(keySet.contains(10))
        Assert.assertTrue(keySet.contains(20))
    }

    @Test
    fun testEquals() {
        val mapA = BranchedMap<Int, Int>()
        mapA[10] = 100
        val mapB = BranchedMap<Int, Int>()
        mapB[10] = 100
        Assert.assertNotEquals(mapA, mapB)
    }
}