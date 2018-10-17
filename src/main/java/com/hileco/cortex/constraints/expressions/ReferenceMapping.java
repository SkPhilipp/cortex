package com.hileco.cortex.constraints.expressions;

import java.util.Map;

public interface ReferenceMapping {
    Map<ReferenceExpression, String> getReferencesForward();

    Map<String, ReferenceExpression> getReferencesBackward();
}
