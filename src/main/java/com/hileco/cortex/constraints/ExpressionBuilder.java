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
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ExpressionBuilder {

    private Map<Integer, Consumer<Expression>> consumers;
    private int offset;

    public ExpressionBuilder() {
        this.consumers = new HashMap<>();
    }

    public void bind(int position, Consumer<Expression> consumer) {
        var existingConsumer = this.consumers.get(this.offset + position);
        var newConsumer = consumer;
        if (existingConsumer != null) {
            newConsumer = newConsumer.andThen(existingConsumer);
        }
        this.consumers.put(this.offset + position, newConsumer);
    }

    private void release(int position, Expression expression) {
        var consumer = this.consumers.remove(this.offset + position);
        if (consumer != null) {
            consumer.accept(expression);
        }
    }

    public void addInstruction(Instruction instruction) {
        Expression expression = null;
        var stackAdds = instruction.getStackAdds();
        this.offset += stackAdds.size();

        if (instruction instanceof ADD) {
            var operation2Expression = Operation2Expression.Type2.ADD.on(null, null);
            this.bind(ADD.LEFT.getPosition(), operation2Expression::setLeft);
            this.bind(ADD.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }
        if (instruction instanceof SUBTRACT) {
            var operation2Expression = Operation2Expression.Type2.SUBTRACT.on(null, null);
            this.bind(SUBTRACT.LEFT.getPosition(), operation2Expression::setLeft);
            this.bind(SUBTRACT.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }
        if (instruction instanceof MULTIPLY) {
            var operation2Expression = Operation2Expression.Type2.MULTIPLY.on(null, null);
            this.bind(MULTIPLY.LEFT.getPosition(), operation2Expression::setLeft);
            this.bind(MULTIPLY.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }
        if (instruction instanceof MODULO) {
            var operation2Expression = Operation2Expression.Type2.MODULO.on(null, null);
            this.bind(MODULO.LEFT.getPosition(), operation2Expression::setLeft);
            this.bind(MODULO.RIGHT.getPosition(), operation2Expression::setRight);
            expression = operation2Expression;
        }
        if (instruction instanceof PUSH) {
            var pushInstruction = (PUSH) instruction;
            var value = new BigInteger(pushInstruction.getBytes());
            expression = ValueExpression.value(value.longValue());
        }
        if (instruction instanceof LOAD) {
            var loadInstruction = (LOAD) instruction;
            var referenceExpression = ReferenceExpression.reference(loadInstruction.getProgramStoreZone(), null);
            this.bind(LOAD.ADDRESS.getPosition(), referenceExpression::setAddress);
            expression = referenceExpression;
        }
        for (var stackAdd : stackAdds) {
            this.release(stackAdd, expression);
        }
    }
}
