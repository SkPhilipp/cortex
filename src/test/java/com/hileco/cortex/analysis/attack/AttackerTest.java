package com.hileco.cortex.analysis.attack;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpUnreachableProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.InstructionsBuilder;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.WINNER;
import static com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA;

public class AttackerTest {

    @Test
    public void test() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpUnreachableProcessor()
        ));
        var instructionsBuilder = new InstructionsBuilder();
        instructionsBuilder.IF(conditionBuilder -> conditionBuilder.include(List.of(new PUSH(BigInteger.valueOf(2).toByteArray()),
                                                                                    new PUSH(BigInteger.valueOf(1).toByteArray()),
                                                                                    new LOAD(CALL_DATA),
                                                                                    new DIVIDE(),
                                                                                    new PUSH(BigInteger.valueOf(12345).toByteArray()),
                                                                                    new EQUALS())),
                               contentBuilder -> contentBuilder.include(List.of(new HALT(WINNER))));
        var instructions = instructionsBuilder.build();
        var graph = graphBuilder.build(instructions);
        var attacker = new Attacker(Attacker.TARGET_IS_HALT_WINNER);
        var solutions = attacker.solve(graph);
        Documentation.of(Attacker.class.getSimpleName())
                .headingParagraph(Attacker.class.getSimpleName())
                .paragraph("Program:").source(instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solutions);
        Assert.assertEquals(1, solutions.size());
        var solution = solutions.get(0);
        Assert.assertTrue(solution.isSolvable());
        Assert.assertEquals(1, solution.getPossibleValues().size());
        var entry = solution.getPossibleValues().entrySet().iterator().next();
        Assert.assertEquals(new Expression.Reference(CALL_DATA, new Expression.Value(1L)), entry.getKey());
        Assert.assertEquals(Long.valueOf(24690L), entry.getValue());
    }
}