package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.hileco.cortex.instructions.Instruction;
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
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.debug.NOOP;
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.hileco.cortex.vm.ProgramStoreZone.valueOf;
import static com.hileco.cortex.instructions.ProgramException.Reason;

public class InstructionDeserializer extends StdScalarDeserializer<Instruction> {

    private final StringDeserializer stringDeserializer;

    private static final Map<String, Builder> MAP;

    @FunctionalInterface
    private interface Builder {
        Instruction build(String[] split) throws IOException;
    }

    private static Builder require(int amount, Builder function) {
        return split -> {
            var params = split.length - 1;
            if (params != amount) {
                throw new IOException(String.format("Type requires %d parameters, %d given.", amount, params));
            }
            return function.build(split);
        };
    }

    static {
        MAP = new HashMap<>();
        MAP.put(BITWISE_AND.class.getSimpleName(), require(0, (parameters) -> new BITWISE_AND()));
        MAP.put(BITWISE_NOT.class.getSimpleName(), require(0, (parameters) -> new BITWISE_NOT()));
        MAP.put(BITWISE_OR.class.getSimpleName(), require(0, (parameters) -> new BITWISE_OR()));
        MAP.put(BITWISE_XOR.class.getSimpleName(), require(0, (parameters) -> new BITWISE_XOR()));
        MAP.put(CALL.class.getSimpleName(), require(0, (parameters) -> new CALL()));
        MAP.put(CALL_RETURN.class.getSimpleName(), require(0, (parameters) -> new CALL_RETURN()));
        MAP.put(EQUALS.class.getSimpleName(), require(0, (parameters) -> new EQUALS()));
        MAP.put(GREATER_THAN.class.getSimpleName(), require(0, (parameters) -> new GREATER_THAN()));
        MAP.put(IS_ZERO.class.getSimpleName(), require(0, (parameters) -> new IS_ZERO()));
        MAP.put(LESS_THAN.class.getSimpleName(), require(0, (parameters) -> new LESS_THAN()));
        MAP.put(HALT.class.getSimpleName(), require(1, (parameters) -> new HALT(Reason.valueOf(parameters[1]))));
        MAP.put(NOOP.class.getSimpleName(), require(0, (parameters) -> new NOOP()));
        MAP.put(LOAD.class.getSimpleName(), require(1, (parameters) -> new LOAD(valueOf(parameters[1]))));
        MAP.put(SAVE.class.getSimpleName(), require(1, (parameters) -> new SAVE(valueOf(parameters[1]))));
        MAP.put(EXIT.class.getSimpleName(), require(0, (parameters) -> new EXIT()));
        MAP.put(JUMP.class.getSimpleName(), require(0, (parameters) -> new JUMP()));
        MAP.put(JUMP_DESTINATION.class.getSimpleName(), require(0, (parameters) -> new JUMP_DESTINATION()));
        MAP.put(JUMP_IF.class.getSimpleName(), require(0, (parameters) -> new JUMP_IF()));
        MAP.put(ADD.class.getSimpleName(), require(0, (parameters) -> new ADD()));
        MAP.put(DIVIDE.class.getSimpleName(), require(0, (parameters) -> new DIVIDE()));
        MAP.put(HASH.class.getSimpleName(), require(1, (parameters) -> new HASH(parameters[1])));
        MAP.put(MODULO.class.getSimpleName(), require(0, (parameters) -> new MODULO()));
        MAP.put(MULTIPLY.class.getSimpleName(), require(0, (parameters) -> new MULTIPLY()));
        MAP.put(SUBTRACT.class.getSimpleName(), require(0, (parameters) -> new SUBTRACT()));
        MAP.put(DUPLICATE.class.getSimpleName(), require(1, (parameters) -> new DUPLICATE(Integer.valueOf(parameters[1]))));
        MAP.put(POP.class.getSimpleName(), require(0, (parameters) -> new POP()));
        MAP.put(PUSH.class.getSimpleName(), require(1, (parameters) -> new PUSH(new BigInteger(parameters[1]).toByteArray())));
        MAP.put(SWAP.class.getSimpleName(), require(2, (parameters) -> new SWAP(Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2]))));
    }

    public InstructionDeserializer() {
        super(Instruction.class);
        this.stringDeserializer = new StringDeserializer();
    }

    @Override
    public Instruction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var string = this.stringDeserializer.deserialize(p, ctxt);
        if (string == null) {
            return null;
        }
        var split = string.split("\\s+");
        var type = split[0];
        var builder = MAP.get(type);
        if (builder == null) {
            throw new JsonMappingException(p, String.format("%s is not a known instruction type.", type));
        }
        try {
            return builder.build(split);
        } catch (IOException e) {
            throw new JsonMappingException(p, String.format("Cloud not parse instruction of type %s: %s", type, e.getMessage()), e);
        }
    }
}
