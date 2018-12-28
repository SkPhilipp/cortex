package com.hileco.cortex.constraints

import com.hileco.cortex.constraints.expressions.Expression

data class Solution(val possibleValues: Map<Expression.Reference, Long>,
                    val isSolvable: Boolean = false)
