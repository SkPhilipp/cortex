package com.hileco.cortex.primitives;

import com.hileco.vm.primitives.layer.LayeredVmMap;
import com.hileco.vm.primitives.test.Variation;
import org.junit.Assert;
import org.junit.Test;

public class LayeredVmMapTest {
    @Test
    public void testMap() {
        Variation.fuzzed(100, variation -> {
            var layeredMap = new LayeredVmMap<Integer, String>();
            variation.maybe(layeredMap::copy);
            layeredMap.put(1, "1");
            variation.maybe(layeredMap::copy);
            layeredMap.put(2, "2");
            variation.maybe(layeredMap::copy);
            layeredMap.put(3, "3");
            variation.maybe(layeredMap::copy);
            Assert.assertEquals("1", layeredMap.get(1));
            Assert.assertEquals("2", layeredMap.get(2));
            Assert.assertEquals("3", layeredMap.get(3));
            variation.maybe(layeredMap::copy);
            layeredMap.remove(1);
            variation.maybe(layeredMap::copy);
            layeredMap.remove(2);
            variation.maybe(layeredMap::copy);
            layeredMap.remove(3);
            variation.maybe(layeredMap::copy);
            Assert.assertNull(layeredMap.get(1));
            Assert.assertNull(layeredMap.get(2));
            Assert.assertNull(layeredMap.get(3));
        });
    }
}