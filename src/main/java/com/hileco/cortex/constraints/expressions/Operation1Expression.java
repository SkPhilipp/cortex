package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Operation1Expression implements Expression {

    public enum Type1 {
        HASH;

        public Operation1Expression of(Expression input) {
            return new Operation1Expression(this, input);
        }
    }

    private Type1 type;
    private Expression input;

    @Override
    public Expr build(Context context, ReferenceMapping referenceMapping) {
        throw new IllegalStateException(String.format("Unimplemented: %s", this.type));
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", this.type, this.input);
    }
}
