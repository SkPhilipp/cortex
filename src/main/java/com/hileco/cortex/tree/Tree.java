package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.List;
import java.util.stream.Stream;

public interface Tree {

    enum Attribute {
        SELF_CONTAINED,
        PSEUDO_RANDOM_NUMBER_GENERATOR
    }

    interface Route {
    }

    Stream<Instruction> stream();

    boolean is(Attribute attribute);

    List<Route> getRoute(Instruction instruction);

}
