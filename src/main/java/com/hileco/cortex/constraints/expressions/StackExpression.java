package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StackExpression implements Expression {

    private int address;

    @Override
    public String toString() {
        return String.format("STACK[%d]", this.address);
    }

    @Override
    public Expr build(Context context, ReferenceMapping referenceMapping) {
        throw new IllegalArgumentException(String.format("Missing stack: %d.", this.address));
    }
}
