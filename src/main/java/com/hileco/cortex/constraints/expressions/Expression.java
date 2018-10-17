package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

public interface Expression {

    Expr build(Context context, ReferenceMapping referenceMapping);
}
