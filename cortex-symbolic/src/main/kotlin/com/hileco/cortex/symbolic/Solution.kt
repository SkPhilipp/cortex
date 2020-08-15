package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression

data class Solution(val values: Map<Expression.Reference, Long> = mapOf(),
                    val solvable: Boolean = false)
