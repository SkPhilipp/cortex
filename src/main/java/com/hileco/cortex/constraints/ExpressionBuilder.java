package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.constraints.expressions.Operation2Expression;
import com.hileco.cortex.constraints.expressions.ReferenceExpression;
import com.hileco.cortex.constraints.expressions.ValueExpression;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExpressionBuilder {

    private final SlidingConsumerMap<Expression> map = new SlidingConsumerMap<>();

    public void bindConstraint(int position, Consumer<Expression> value) {
        this.map.set(position, value);
    }

    public void addInstruction(Instruction instruction) {

        Expression expression = null;
        var consumersToFill = instruction.getStackAdds()
                .stream()
                .map(this.map::remove)
                .collect(Collectors.toList());

        instruction.getStackAdds().forEach(entry -> this.map.backward());

        // BITWISE_NOT

        // BITWISE_AND

        // BITWISE_OR

        // BITWISE_XOR

        // CALL
        // --

        // CALL_RETURN
        // --

        // EQUALS

        // GREATER_THAN

        // IS_ZERO

        // LESS_THAN

        // HALT
        // --

        // NOOP
        // --

        // LOAD
        if (instruction instanceof LOAD) {
            var loadInstruction = (LOAD) instruction;
            var referenceExpression = ReferenceExpression.reference(loadInstruction.getProgramStoreZone(), null);
            this.map.set(LOAD.ADDRESS.getPosition(), referenceExpression::setAddress);
            expression = referenceExpression;
        }

        // SAVE
        // --

        // EXIT
        // --

        // JUMP
        // --

        // JUMP_DESTINATION
        // --

        // JUMP_IF
        // --

        // ADD
        if (instruction instanceof ADD) {
            var operation2Expression = Operation2Expression.Type2.ADD.on(null, null);
            this.map.set(ADD.LEFT.getPosition(), operation2Expression::setLeft);
            this.map.set(ADD.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }

        // DIVIDE

        // HASH

        // MODULO
        if (instruction instanceof MODULO) {
            var operation2Expression = Operation2Expression.Type2.MODULO.on(null, null);
            this.map.set(MODULO.LEFT.getPosition(), operation2Expression::setLeft);
            this.map.set(MODULO.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }

        // SUBTRACT
        if (instruction instanceof SUBTRACT) {
            var operation2Expression = Operation2Expression.Type2.SUBTRACT.on(null, null);
            this.map.set(SUBTRACT.LEFT.getPosition(), operation2Expression::setLeft);
            this.map.set(SUBTRACT.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }

        // MULTIPLY
        if (instruction instanceof MULTIPLY) {
            var operation2Expression = Operation2Expression.Type2.MULTIPLY.on(null, null);
            this.map.set(MULTIPLY.LEFT.getPosition(), operation2Expression::setLeft);
            this.map.set(MULTIPLY.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }

        // DUPLICATE

        // POP
        if (instruction instanceof POP) {
            this.map.set(POP.INPUT.getPosition(), (ignored) -> {
            });
        }

        // PUSH
        if (instruction instanceof PUSH) {
            var pushInstruction = (PUSH) instruction;
            var value = new BigInteger(pushInstruction.getBytes());
            expression = ValueExpression.value(value.longValue());
        }

        // SWAP

        var stackParameters = instruction.getStackParameters();
        stackParameters.forEach(entry -> this.map.forward());

        for (var consumer : consumersToFill) {
            consumer.accept(expression);
        }
    }
}
