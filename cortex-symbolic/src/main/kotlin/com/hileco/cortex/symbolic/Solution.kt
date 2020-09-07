package com.hileco.cortex.symbolic

import com.hileco.cortex.collections.serialize
import com.hileco.cortex.symbolic.expressions.Expression

data class Solution(val values: Map<Expression, ByteArray> = mapOf(),
                    val solvable: Boolean = false,
                    val condition: Expression) {
    override fun toString(): String {
        val result = StringBuffer()
        result.append("Solution(values=")
        result.append(values.map { (key, value) ->
            key to value.serialize()
        })
        result.append(", solvable=")
        result.append(solvable)
        result.append(", condition=")
        val conditionString = condition.toString()
        result.append(conditionString.take(2048))
        if (conditionString.length > 2048) {
            result.append("...")
        }
        result.append(")")
        return result.toString()
    }
}
