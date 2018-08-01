package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.choices.Chanced;
import com.hileco.cortex.fuzzer.choices.FuzzExpression;
import com.hileco.cortex.fuzzer.choices.FuzzFunctionLayout;
import com.hileco.cortex.fuzzer.choices.FuzzProgramLayout;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class ProgramGenerator {

    private static final int LIMIT_INITIAL_PROGRAMS_PER_ATLAS = 10;
    private static final int LIMIT_INITIAL_FUNCTIONS_PER_PROGRAM = 10;
    private static final int LIMIT_INITIAL_CALL_DATA_LOADING_PER_PROGRAM = 10;
    private static final int LIMIT_SIZE_CALL_DATA = 8192;
    private static final String PROGRAM_END_LABEL = "end";

    private final Supplier<FuzzProgramLayout> randomFuzzProgramLayout;
    private final Supplier<FuzzFunctionLayout> randomFuzzFunctionLayout;
    private final Supplier<FuzzExpression> randomFuzzExpression;
    private final Random random;
    private final ProgramBuilderFactory programBuilderFactory;

    public ProgramGenerator() {
        programBuilderFactory = new ProgramBuilderFactory();
        randomFuzzProgramLayout = Chanced.atRandom(FuzzProgramLayout.values());
        randomFuzzFunctionLayout = Chanced.atRandom(FuzzFunctionLayout.values());
        randomFuzzExpression = Chanced.atRandom(FuzzExpression.values());
        random = new Random();
    }

    public LayeredMap<BigInteger, Program> generate() {
        LayeredMap<BigInteger, Program> atlas = new LayeredMap<>();
        int programs = random.nextInt(LIMIT_INITIAL_PROGRAMS_PER_ATLAS) + 1;
        for (int i = 0; i < programs; i++) {
            FuzzProgramLayout fuzzProgramLayout = randomFuzzProgramLayout.get();
            atlas.merge(generate(fuzzProgramLayout));
        }
        return atlas;
    }


    private LayeredMap<BigInteger, Program> generate(FuzzProgramLayout fuzzProgramLayout) {
        LayeredMap<BigInteger, Program> atlas = new LayeredMap<>();
        switch (fuzzProgramLayout) {
            case FUNCTION_TABLE: {
                Map<BigInteger, ProgramBuilder> functionBuilders = new HashMap<>();
                int functions = random.nextInt(LIMIT_INITIAL_FUNCTIONS_PER_PROGRAM) + 1;
                for (int i = 0; i < functions; i++) {
                    FuzzFunctionLayout fuzzFunctionLayout = randomFuzzFunctionLayout.get();
                    functionBuilders.put(BigInteger.valueOf(random.nextLong()), generate(fuzzFunctionLayout, atlas));
                }
                ProgramBuilder programBuilder = programBuilderFactory.builder();
                int loads = random.nextInt(LIMIT_INITIAL_CALL_DATA_LOADING_PER_PROGRAM) + 1;
                for (int i = 0; i < loads; i++) {
                    programBuilder.PUSH(BigInteger.valueOf(random.nextInt(LIMIT_SIZE_CALL_DATA)).toByteArray());
                    programBuilder.LOAD(ProgramStoreZone.CALL_DATA);
                }
                functionBuilders.forEach((address, functionBuilder) -> {
                    programBuilder.PUSH(BigInteger.valueOf(random.nextInt()).toByteArray());
                    programBuilder.EQUALS();
                    programBuilder.PUSH_LABEL(address.toString());
                    programBuilder.JUMP_IF();
                });
                programBuilder.PUSH_LABEL(PROGRAM_END_LABEL);
                programBuilder.JUMP();
                functionBuilders.forEach((address, functionBuilder) -> {
                    programBuilder.JUMP_DESTINATION_WITH_LABEL(address.toString());
                    programBuilder.include(functionBuilder);
                });
                programBuilder.JUMP_DESTINATION_WITH_LABEL(PROGRAM_END_LABEL);
                Program build = programBuilder.build(BigInteger.valueOf(random.nextInt()));
                atlas.put(build.getAddress(), build);
            }
            break;
            case FUNCTION: {
                FuzzFunctionLayout fuzzFunctionLayout = randomFuzzFunctionLayout.get();

                ProgramBuilder programBuilder = programBuilderFactory.builder();
                int loads = random.nextInt(LIMIT_INITIAL_CALL_DATA_LOADING_PER_PROGRAM) + 1;
                for (int i = 0; i < loads; i++) {
                    programBuilder.PUSH(BigInteger.valueOf(random.nextInt(LIMIT_SIZE_CALL_DATA)).toByteArray());
                    programBuilder.LOAD(ProgramStoreZone.CALL_DATA);
                }
                programBuilder.include(generate(fuzzFunctionLayout, atlas));
                Program build = programBuilder.build(BigInteger.valueOf(random.nextInt()));
                atlas.put(build.getAddress(), build);
            }
            break;
        }
        return atlas;
    }

    private ProgramBuilder generate(FuzzFunctionLayout fuzzFunctionLayout, LayeredMap<BigInteger, Program> atlas) {
        ProgramBuilder builder = programBuilderFactory.builder();
        switch (fuzzFunctionLayout) {
            case EXIT:
                builder.EXIT();
                break;
            case RETURN:
                builder.CALL_RETURN();
                break;
            case CALL_WITH_FUNDS:
                builder.PUSH(BigInteger.valueOf(random.nextInt()).toByteArray());
                builder.PUSH(BigInteger.valueOf(random.nextInt()).toByteArray());
                builder.SWAP(0, random.nextInt(10) + 1);
                builder.CALL();
                break;
            case CALL_LIBRARY:
                Set<BigInteger> choices = atlas.keySet();
                if (!choices.isEmpty()) {
                    BigInteger address = (BigInteger) choices.toArray()[random.nextInt(choices.size())];
                    builder.PUSH(BigInteger.valueOf(0).toByteArray());
                    builder.PUSH(address.toByteArray());
                    builder.CALL();
                } else {
                    builder.EXIT();
                }
                break;
            case SAVE:
                builder.SAVE(ProgramStoreZone.DISK);
                break;
        }
        return builder;
    }
}
