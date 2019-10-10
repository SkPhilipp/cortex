package com.hileco.cortex.primitives;

import com.hileco.vm.primitives.layer.LayeredVmSet;
import com.hileco.vm.primitives.test.Variation;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LayeredVmSetTest {
    @Test
    public void testSet() {
        Variation.fuzzed(100, variation -> {
            var layeredSet = new LayeredVmSet<Integer>();
            variation.maybe(layeredSet::copy);
            layeredSet.add(1);
            variation.maybe(layeredSet::copy);
            layeredSet.add(2);
            variation.maybe(layeredSet::copy);
            layeredSet.add(3);
            variation.maybe(layeredSet::copy);
            assertTrue(layeredSet.contains(1));
            assertTrue(layeredSet.contains(2));
            assertTrue(layeredSet.contains(3));
            variation.maybe(layeredSet::copy);
            layeredSet.remove(1);
            variation.maybe(layeredSet::copy);
            layeredSet.remove(2);
            variation.maybe(layeredSet::copy);
            layeredSet.remove(3);
            variation.maybe(layeredSet::copy);
            assertFalse(layeredSet.contains(1));
            assertFalse(layeredSet.contains(2));
            assertFalse(layeredSet.contains(3));
        });
    }
}