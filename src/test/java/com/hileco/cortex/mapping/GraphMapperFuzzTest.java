package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphMapperFuzzTest {

    private static final int EXPECTED_MINIMUM_AVERAGE_JUMPS_MAPPED = 10;
    private static final int LIMIT_RUNS = 50;

    @Test
    public void testTreeMapper() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor(),
                new FlowProcessor()
        ));

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
                var graph = graphBuilder.build(instructions);
                var edgeFlowMapping = EdgeFlowMapping.UTIL.findAny(graph).get();
                Assert.assertEquals(instructions.size(), edgeFlowMapping.getLineMapping().size());
                jumpsMapped += edgeFlowMapping.getFlowsFromSource().values().stream().mapToInt(Set::size).sum();
            }
        }
        Assert.assertTrue(jumpsMapped / LIMIT_RUNS > EXPECTED_MINIMUM_AVERAGE_JUMPS_MAPPED);
    }
}
