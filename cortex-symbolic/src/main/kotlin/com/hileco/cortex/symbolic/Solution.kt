package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression

data class Solution(val values: Map<Expression, ByteArray> = mapOf(),
                    val solvable: Boolean = false,
                    val condition: Expression)
