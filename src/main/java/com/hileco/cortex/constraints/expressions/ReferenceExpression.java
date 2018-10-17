package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReferenceExpression implements Expression {

    public enum ReferenceType {
        STACK,
        MEMORY,
        DISK,
        CALL_DATA
    }

    private ReferenceType type;
    private Expression address;

    public static ReferenceExpression reference(ReferenceType type, Expression address) {
        return new ReferenceExpression(type, address);
    }

    @Override
    public Expr build(Context context, ReferenceMapping referenceMapping) {
        var reference = referenceMapping.getReferencesForward().computeIfAbsent(this, unmappedReference -> {
            var key = Integer.toString(referenceMapping.getReferencesForward().size());
            referenceMapping.getReferencesBackward().put(key, unmappedReference);
            return key;
        });
        var referenceSymbol = context.mkSymbol(reference);
        return context.mkIntConst(referenceSymbol);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", this.type, this.address);
    }
}
