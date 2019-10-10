package com.hileco.cortex.primitives;

import com.hileco.vm.primitives.layer.LayeredVmStack;
import com.hileco.vm.primitives.test.Variation;
import org.junit.Test;

public class LayeredVmStackTest {
    @Test
    public void testStack() {
        Variation.fuzzed(100, variation -> {
            var layeredStack = new LayeredVmStack<Integer>();
            variation.maybe(layeredStack::copy);
            layeredStack.push(1);
            variation.maybe(layeredStack::copy);
            layeredStack.push(2);
            variation.maybe(layeredStack::copy);
            layeredStack.push(3);
            variation.maybe(layeredStack::copy);
            assertEquals(Integer.valueOf(3), layeredStack.peek());
            assertEquals(Integer.valueOf(3), layeredStack.pop());
            variation.maybe(layeredStack::copy);
            assertEquals(Integer.valueOf(2), layeredStack.peek());
            assertEquals(Integer.valueOf(2), layeredStack.pop());
            variation.maybe(layeredStack::copy);
            assertEquals(Integer.valueOf(1), layeredStack.peek());
            assertEquals(Integer.valueOf(1), layeredStack.pop());
        });
    }
}