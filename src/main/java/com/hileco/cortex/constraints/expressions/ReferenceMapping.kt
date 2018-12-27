package com.hileco.cortex.constraints.expressions

interface ReferenceMapping {
    val referencesForward: MutableMap<Expression.Reference, String>
    val referencesBackward: MutableMap<String, Expression.Reference>
}
