package com.hileco.cortex.pathing;

import com.hileco.cortex.mapping.TreeMapping;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PathIteratorTest {

    private static final int OFFSET = 3;
    private static final int[][] MAPPING_4K = {
            {1},
            {OFFSET, OFFSET * 2, OFFSET * 3, OFFSET * 4},
            {OFFSET * 5, OFFSET * 6, OFFSET * 7, OFFSET * 8},
            {OFFSET * 9, OFFSET * 10, OFFSET * 11, OFFSET * 12},
            {OFFSET * 13, OFFSET * 14, OFFSET * 15, OFFSET * 16},
            {OFFSET * 17, OFFSET * 18, OFFSET * 19, OFFSET * 20},
            {OFFSET * 21, OFFSET * 22, OFFSET * 23, OFFSET * 24},
    };
    private static final int[][] MAPPING_90M = {
            {1},
            {OFFSET, OFFSET * 2, OFFSET * 3, OFFSET * 4, OFFSET * 5, OFFSET * 6, OFFSET * 7, OFFSET * 8, OFFSET * 9},
            {OFFSET * 10, OFFSET * 11, OFFSET * 12, OFFSET * 13, OFFSET * 14, OFFSET * 15, OFFSET * 16, OFFSET * 17, OFFSET * 18, OFFSET * 19},
            {OFFSET * 20, OFFSET * 21, OFFSET * 22, OFFSET * 23, OFFSET * 24, OFFSET * 25, OFFSET * 26, OFFSET * 27, OFFSET * 28, OFFSET * 29},
            {OFFSET * 30, OFFSET * 31, OFFSET * 32, OFFSET * 33, OFFSET * 34, OFFSET * 35, OFFSET * 36, OFFSET * 37, OFFSET * 38, OFFSET * 39},
            {OFFSET * 40, OFFSET * 41, OFFSET * 42, OFFSET * 43, OFFSET * 44, OFFSET * 45, OFFSET * 46, OFFSET * 47, OFFSET * 48, OFFSET * 49},
            {OFFSET * 50, OFFSET * 51, OFFSET * 52, OFFSET * 53, OFFSET * 54, OFFSET * 55, OFFSET * 56, OFFSET * 57, OFFSET * 58, OFFSET * 59},
            {OFFSET * 60, OFFSET * 61, OFFSET * 62, OFFSET * 63, OFFSET * 64, OFFSET * 65, OFFSET * 66, OFFSET * 67, OFFSET * 68, OFFSET * 69},
            {OFFSET * 70, OFFSET * 71, OFFSET * 72, OFFSET * 73, OFFSET * 74, OFFSET * 75, OFFSET * 76, OFFSET * 77, OFFSET * 78, OFFSET * 79}
    };

    private TreeMapping generateMapping(int[][] template) {
        var treeMapping = new TreeMapping();
        for (var i = 0; i < template.length - 1; i++) {
            for (var source : template[i]) {
                for (var target : template[i + 1]) {
                    treeMapping.putJumpMapping(source, target);
                }
            }
        }
        return treeMapping;
    }

    @Test
    public void test() {
        var treeMapping = this.generateMapping(MAPPING_4K);
        var pathIterator = new PathIterator(treeMapping, 1);
        var atomicInteger = new AtomicInteger();
        pathIterator.forEachRemaining(integers -> atomicInteger.incrementAndGet());
        var combinations = Stream.of(MAPPING_4K)
                .map(mapping -> mapping.length)
                .reduce((sizeA, sizeB) -> sizeA * sizeB).get();
        Assert.assertEquals(combinations.intValue(), atomicInteger.get());
    }

    @Test
    public void testMemory() {
        var treeMapping = this.generateMapping(MAPPING_90M);
        var pathIterator = new PathIterator(treeMapping, 1);
        var atomicInteger = new AtomicInteger();
        pathIterator.forEachRemaining(integers -> atomicInteger.incrementAndGet());
        var combinations = Stream.of(MAPPING_90M)
                .map(mapping -> mapping.length)
                .reduce((sizeA, sizeB) -> sizeA * sizeB).get();
        Assert.assertEquals(combinations.intValue(), atomicInteger.get());
        System.out.println(combinations);
    }
}
