package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueExpression implements Expression {

    private Long constant;

    public static ValueExpression value(Long constant) {
        return new ValueExpression(constant);
    }

    @Override
    public Expr build(Context context, ReferenceMapping referenceMapping) {
        return context.mkInt(this.constant);
    }

    @Override
    public String toString() {
        return Long.toString(this.constant);
    }
}
