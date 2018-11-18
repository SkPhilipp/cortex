package com.hileco.cortex.server;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.attack.Attacker;
import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.constraints.Solver;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.StackParameter;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_NOT;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.bits.BITWISE_XOR;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.calls.CALL_RETURN;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.io.SAVE;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.math.HASH;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;
import com.hileco.cortex.visual.VisualGraph;
import com.hileco.cortex.vm.Program;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.VirtualMachine;
import guru.nidi.graphviz.engine.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructions")
public class InstructionsController {

    private static final Instruction[] INSTRUCTIONS = {
            new PUSH(null),
            new POP(),
            new SWAP(1, 2),
            new SWAP(2, 3),
            new SWAP(3, 4),
            new DUPLICATE(1),
            new DUPLICATE(2),
            new DUPLICATE(3),
            new EQUALS(),
            new GREATER_THAN(),
            new IS_ZERO(),
            new LESS_THAN(),
            new CALL(),
            new CALL_RETURN(),
            new BITWISE_AND(),
            new BITWISE_NOT(),
            new BITWISE_OR(),
            new BITWISE_XOR(),
            new LOAD(null),
            new SAVE(null),
            new EXIT(),
            new JUMP(),
            new JUMP_DESTINATION(),
            new JUMP_IF(),
            new ADD(),
            new DIVIDE(),
            new HASH(null),
            new MULTIPLY(),
            new MODULO(),
            new SUBTRACT()
    };

    private static final GraphBuilder GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor(),
            new ExitTrimProcessor(),
            new JumpIllegalProcessor(),
            new KnownJumpIfProcessor(),
            new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
            new KnownProcessor()
    ));

    public static final String ATTACK_TARGET_METHOD_CALL = "anyCall";
    public static final String ATTACK_TARGET_METHOD_HALT_WINNER = "winner";

    private static final Map<String, Attacker> ATTACK_TARGET_METHODS = Map.of(
            ATTACK_TARGET_METHOD_CALL, new Attacker(Attacker.TARGET_IS_CALL),
            ATTACK_TARGET_METHOD_HALT_WINNER, new Attacker(Attacker.TARGET_IS_HALT_WINNER)
    );

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Representation {
        private String name;
        private List<String> takes;
        private List<Integer> provides;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ProgramRequest {
        private List<Instruction> instructions;
    }

    @GetMapping("/list")
    public Map list() {
        return Map.of("instructions", Arrays.stream(INSTRUCTIONS)
                .map(instruction -> new Representation(instruction.getClass().getSimpleName(),
                                                       instruction.getStackParameters().stream().map(StackParameter::getName).collect(Collectors.toList()),
                                                       instruction.getStackAdds()))
                .collect(Collectors.toList()));
    }

    @PostMapping("/constraints")
    public Map constraints(@RequestBody ProgramRequest request) {
        var builder = new ExpressionGenerator();
        request.getInstructions().forEach(builder::addInstruction);
        return Map.of("expression", builder.getCurrentExpression());
    }

    @PostMapping("/execute")
    public Map execute(@RequestBody ProgramRequest request) throws ProgramException {
        var program = new Program(BigInteger.ZERO, request.getInstructions());
        var programContext = new ProgramContext(program);
        var processContext = new VirtualMachine(programContext);
        var programRunner = new ProgramRunner(processContext);
        programRunner.run();
        return Map.of("stack", programContext.getStack());
    }

    @GetMapping("/fuzzer")
    public Map fuzzer(@RequestParam(value = "seed", defaultValue = "0") Integer seed) {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        return Map.of("instructions", program.getInstructions());
    }

    @PostMapping("/optimizer")
    public Map optimizer(@RequestBody ProgramRequest request) {
        var program = new Program(request.getInstructions());
        var optimizedGraph = GRAPH_BUILDER.build(program.getInstructions());
        return Map.of("instructions", optimizedGraph.toInstructions());
    }

    @PostMapping("/solve")
    public Map solve(@RequestBody ProgramRequest request) {
        var builder = new ExpressionGenerator();
        request.getInstructions().forEach(builder::addInstruction);
        var solver = new Solver();
        return Map.of("expression", builder.getCurrentExpression(),
                      "solution", solver.solve(builder.getCurrentExpression()));
    }

    @PostMapping(value = "/visualize", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] visualize(@RequestBody ProgramRequest request) throws IOException {
        var graph = GRAPH_BUILDER.build(request.getInstructions());
        var visualGraph = new VisualGraph();
        visualGraph.map(graph);
        var outputStream = new ByteArrayOutputStream();
        visualGraph.getVizGraph().render(Format.PNG).toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    @PostMapping("/attack")
    public Map attack(@RequestBody ProgramRequest request,
                      @RequestParam(value = "targetMethod", defaultValue = ATTACK_TARGET_METHOD_CALL) String targetMethod) {
        var attacker = ATTACK_TARGET_METHODS.get(targetMethod);
        var graph = GRAPH_BUILDER.build(request.getInstructions());
        if (attacker == null) {
            throw new IllegalArgumentException("Allowed target methods: " + ATTACK_TARGET_METHODS.keySet());
        }
        return Map.of("solutions", attacker.solve(graph));
    }
}
