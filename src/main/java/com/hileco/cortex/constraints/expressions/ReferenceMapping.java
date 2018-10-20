package com.hileco.cortex.constraints.expressions;

import java.util.Map;

public interface ReferenceMapping {
    Map<Expression.Reference, String> getReferencesForward();

    Map<String, Expression.Reference> getReferencesBackward();
}
