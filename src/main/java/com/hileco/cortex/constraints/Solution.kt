package com.hileco.cortex.constraints

import com.hileco.cortex.constraints.expressions.Expression

data class Solution(val values: Map<Expression.Reference, Long>,
                    val solvable: Boolean = false)
