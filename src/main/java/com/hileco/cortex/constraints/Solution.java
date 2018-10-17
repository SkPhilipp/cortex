package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.ReferenceExpression;
import lombok.Value;

import java.util.Map;

@Value
public class Solution {
    private Map<ReferenceExpression, Integer> possibleValues;
    private boolean solvable;
}
