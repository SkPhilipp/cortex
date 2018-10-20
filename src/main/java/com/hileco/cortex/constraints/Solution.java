package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.Expression;
import lombok.Value;

import java.util.Map;

@Value
public class Solution {
    private Map<Expression.Reference, Integer> possibleValues;
    private boolean solvable;
}
