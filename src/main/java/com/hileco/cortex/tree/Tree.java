package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.List;
import java.util.stream.Stream;

public interface Tree {

    enum Attribute {
        SELF_CONTAINED,
        PSEUDO_RANDOM_NUMBER_GENERATOR
    }

    enum Zones {
        STACK,
        MEMORY,
        DISK,
        PROGRAM_COUNTER
    }

    interface Route {
    }

    Stream<Instruction> stream();

    boolean is(Attribute attribute);

    List<Zones> getZones();

    List<Route> getRoute(Instruction instruction);

}
