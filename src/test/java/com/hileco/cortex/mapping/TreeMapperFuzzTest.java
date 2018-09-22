package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.JumpTableProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TreeMapperFuzzTest {

    private static final int EXPECTED_MINIMUM_AVERAGE_JUMPS_MAPPED = 10;
    private static final int LIMIT_RUNS = 50;

    @Test
    public void testTreeMapper() {
        var treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor(),
                new JumpTableProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));

        var treeMapper = new TreeMapper();

        var seed = System.currentTimeMillis() * LIMIT_RUNS;
        var runs = 0;
        var jumpsMapped = 0;
        while (runs++ < LIMIT_RUNS) {
            var programGenerator = new ProgramGenerator();
            var runSeed = seed + runs;
            var generatedOptimized = programGenerator.generate(runSeed);
            for (var address : generatedOptimized.keySet()) {
                var program = generatedOptimized.get(address);
                var instructions = program.getInstructions();
                var tree = treeBuilder.build(instructions);
                var treeMapping = treeMapper.map(tree);
                Assert.assertEquals(instructions.size(), treeMapping.getLineMapping().size());
                jumpsMapped += treeMapping.getJumpMappings().values().stream().mapToInt(Map::size).sum();
            }
        }
        Assert.assertTrue(jumpsMapped / LIMIT_RUNS > EXPECTED_MINIMUM_AVERAGE_JUMPS_MAPPED);
    }
}
