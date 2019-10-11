package com.hileco.cortex.data

import com.hileco.cortex.data.backed.BackedVmStack
import com.hileco.cortex.data.layer.LayeredVmStack
import org.junit.Assert
import org.junit.Test
import java.util.*

class VmStackBenchmarkTest {

    private fun benchmarkPreloadBranchModify(vmStack: VmStack<Int, *>, preloaded: Int, branches: Int): Long {
        val start = System.currentTimeMillis()
        for (i in 0 until preloaded) {
            vmStack.push(i)
        }
        for (i in preloaded until preloaded + branches) {
            vmStack.push(i)
            val vmStackCopy = vmStack.copy()
            Assert.assertEquals(vmStack.pop(), vmStackCopy.peek())
        }
        val end = System.currentTimeMillis()
        return end - start
    }

    @Test
    fun preloadedFewBranches() {
        val durationBacked = benchmarkPreloadBranchModify(BackedVmStack(), 50000, 10000)
        val durationLayered = benchmarkPreloadBranchModify(LayeredVmStack(), 50000, 10000)
        println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", durationLayered.toFloat() * 100 / durationBacked))
        println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", durationBacked.toFloat() * 100 / durationLayered))
    }

    @Test
    fun nonPreloadedFewBranches() {
        val durationBacked = benchmarkPreloadBranchModify(BackedVmStack(), 0, 10000)
        val durationLayered = benchmarkPreloadBranchModify(LayeredVmStack(), 0, 10000)
        println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", durationLayered.toFloat() * 100 / durationBacked))
        println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", durationBacked.toFloat() * 100 / durationLayered))
    }

    @Test
    fun preloadedManyBranches() {
        val durationBacked = benchmarkPreloadBranchModify(BackedVmStack(), 50000, 100000)
        val durationLayered = benchmarkPreloadBranchModify(LayeredVmStack(), 50000, 100000)
        println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", durationLayered.toFloat() * 100 / durationBacked))
        println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", durationBacked.toFloat() * 100 / durationLayered))
    }

    @Test
    fun nonPreloadedManyBranches() {
        val durationBacked = benchmarkPreloadBranchModify(BackedVmStack(), 0, 100000)
        val durationLayered = benchmarkPreloadBranchModify(LayeredVmStack(), 0, 100000)
        println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", durationLayered.toFloat() * 100 / durationBacked))
        println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", durationBacked.toFloat() * 100 / durationLayered))
    }
}
