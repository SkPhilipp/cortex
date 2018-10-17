package com.hileco.cortex.server.api;

import com.hileco.cortex.instructions.Instruction;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class InstructionsListApi implements HandlerFunction<ServerResponse> {

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

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return status(HttpStatus.OK)
                .syncBody(Arrays.stream(INSTRUCTIONS)
                                  .map(instruction -> new Representation(instruction.getClass().getSimpleName(),
                                                                         instruction.getStackParameters().stream().map(StackParameter::getName).collect(Collectors.toList()),
                                                                         instruction.getStackAdds()))
                                  .collect(Collectors.toList()));
    }

    @Data
    @AllArgsConstructor
    private static class Representation {
        private String name;
        private List<String> takes;
        private List<Integer> provides;
    }
}
