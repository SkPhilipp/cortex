package com.hileco.cortex.server;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.constraints.Solver;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.pathing.PathIterator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static com.hileco.cortex.context.data.ProgramStoreZone.CALL_DATA;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    // TODO: remove from demo and add example to documentation
    @GetMapping("/constraints.json")
    public ResponseEntity<Map> constraints() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(0xffffffL).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(0L).toByteArray()));
        builder.addInstruction(new LOAD(CALL_DATA));
        builder.addInstruction(new ADD());
        builder.addInstruction(new MODULO());
        builder.addInstruction(new LESS_THAN());
        var solver = new Solver();
        return ResponseEntity.ok(Map.of("expression", builder.getCurrentExpression().toString(),
                                        "solution", solver.solve(builder.getCurrentExpression()).toString()));
    }

    // TODO: remove from demo and add to graph api
    @GetMapping("/fuzzer.json")
    public ResponseEntity<Map> fuzzer(@RequestParam(value = "seed", defaultValue = "0") Integer seed) {
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        var graphBuilder = new GraphBuilder(processors);
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        return ResponseEntity.ok(Map.of("program", program.toString().split("\n"),
                                        "graph", graph.toString().split("\n")));
    }

    // TODO: remove from demo and add to graph api
    @GetMapping("/jump-mapping.json")
    public ResponseEntity<Map> jumpMapping(@RequestParam(value = "seed", defaultValue = "0") Integer seed) {
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
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        var jumpMapping = new HashMap<Integer, Set<Integer>>();
        graph.getEdges().stream()
                .flatMap(EdgeFlowMapping.UTIL::filter)
                .forEach(edge -> edge.getJumpMapping().forEach((source, targets) -> {
                    var joiner = new StringJoiner(", ");
                    targets.forEach(target -> joiner.add(target.toString()));
                    jumpMapping.put(source, targets);
                }));
        return ResponseEntity.ok(Map.of("program", program.toString().split("\n"),
                                        "jumpMapping", jumpMapping));
    }

    // TODO: remove from demo and add to graph api
    @GetMapping("/optimizer.json")
    public ResponseEntity<Map> optimizer() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor()
        ));
        var optimizedGraphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var program = new Program(List.of(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        var graph = graphBuilder.build(program.getInstructions());
        var optimizedGraph = optimizedGraphBuilder.build(program.getInstructions());
        return ResponseEntity.ok(Map.of("program", program.toString().split("\n"),
                                        "graph", graph.toString().split("\n"),
                                        "optimizedGraph", optimizedGraph.toString().split("\n")));
    }

    @GetMapping("/pathing.json")
    public ResponseEntity<Map> pathing(@RequestParam(value = "seed", defaultValue = "0") Integer seed) {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        var edgeFlowMapping = graph.getEdges().stream()
                .flatMap(EdgeFlowMapping.UTIL::filter)
                .findAny().get();
        var pathIterator = new PathIterator(edgeFlowMapping, 1);
        var instructions = graph.toInstructions();
        var paths = new ArrayList<String>();
        pathIterator.forEachRemaining(integers -> {
            var stringBuilder = new StringBuilder();
            for (var i = 0; i < integers.size() - 1; i++) {
                var current = integers.get(i);
                var next = integers.get(i + 1);
                var instruction = instructions.get(current);
                if (current == 1 || instruction instanceof JUMP_DESTINATION) {
                    stringBuilder.append('\n');
                    for (int index = current; index <= next; index++) {
                        instruction = instructions.get(index);
                        stringBuilder.append(String.format("[%03d] %s", index, instruction));
                        stringBuilder.append('\n');
                    }
                }
            }
            var current = integers.get(integers.size() - 1);
            var instruction = instructions.get(current);
            if (stringBuilder.length() > 0) {
                stringBuilder.append('\n');
            }
            stringBuilder.append(String.format("[%03d] %s", current, instruction));
            paths.add(stringBuilder.toString());
        });
        return ResponseEntity.ok(Map.of("program", program.toString().split("\n"),
                                        "paths", paths));
    }
}
