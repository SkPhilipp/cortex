package com.hileco.cortex.constraints;

import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA;

public class SolverTest {

    @Test
    public void testSolve() {
        var instructions = List.of(
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new PUSH(BigInteger.valueOf(0xffffffL).toByteArray()),
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new PUSH(BigInteger.valueOf(0L).toByteArray()),
                new LOAD(CALL_DATA),
                new ADD(),
                new MODULO(),
                new LESS_THAN()
        );
        var expressionGenerator = new ExpressionGenerator();
        instructions.forEach(expressionGenerator::addInstruction);
        var solver = new Solver();
        var solution = solver.solve(expressionGenerator.getCurrentExpression());
        var onlyValue = solution.getPossibleValues().values().iterator().next();
        Documentation.of(Solver.class.getSimpleName())
                .headingParagraph(Solver.class.getSimpleName())
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting expression:").source(expressionGenerator.getCurrentExpression())
                .paragraph("Suggested solution for expression to be true:").source(solution);
        Assert.assertTrue(solution.isSolvable());
        Assert.assertTrue((10L + onlyValue) % 0xffffffL < 10);
    }
}