package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.bytes.BackedInteger

data class Solution(val values: Map<Expression.Reference, BackedInteger> = mapOf(),
                    val solvable: Boolean = false,
                    val condition: Expression)
