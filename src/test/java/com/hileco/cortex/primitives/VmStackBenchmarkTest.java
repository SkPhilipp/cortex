package com.hileco.cortex.primitives;

import com.hileco.vm.primitives.heavy.BackedVmStack;
import com.hileco.vm.primitives.layer.LayeredVmStack;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class VmStackBenchmarkTest {

    private long benchmarkPreloadBranchModify(VmStack<Integer, ?> vmStack, int preloaded, int branches) {
        var start = System.currentTimeMillis();
        for (var i = 0; i < preloaded; i++) {
            vmStack.push(i);
        }
        for (var i = preloaded; i < preloaded + branches; i++) {
            vmStack.push(i);
            var vmStackCopy = vmStack.copy();
            Assert.assertEquals(vmStack.pop(), vmStackCopy.peek());
        }
        var end = System.currentTimeMillis();
        return end - start;
    }

    @Test
    public void preloaded() {
        var durationBacked = benchmarkPreloadBranchModify(new BackedVmStack<>(), 50000, 50000);
        var durationLayered = benchmarkPreloadBranchModify(new LayeredVmStack<>(), 50000, 50000);
        System.out.println("preloaded");
        System.out.println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", (float) durationLayered * 100 / durationBacked));
        System.out.println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", (float) durationBacked * 100 / durationLayered));
    }

    @Test
    public void nonPreloaded() {
        var durationBacked = benchmarkPreloadBranchModify(new BackedVmStack<>(), 0, 50000);
        var durationLayered = benchmarkPreloadBranchModify(new LayeredVmStack<>(), 0, 50000);
        System.out.println("nonPreloaded");
        System.out.println(String.format(Locale.US, "layered took %.2f%% of backed implementation's time", (float) durationLayered * 100 / durationBacked));
        System.out.println(String.format(Locale.US, "backed took %.2f%% of layered implementation's time", (float) durationBacked * 100 / durationLayered));
    }
}
