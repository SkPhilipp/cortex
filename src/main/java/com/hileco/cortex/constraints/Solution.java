package com.hileco.cortex.constraints;

import lombok.Value;

import java.util.Map;

@Value
public class Solution {
    private Map<Reference, Integer> possibleValues;
    private boolean solvable;
}
