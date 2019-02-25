package com.hileco.cortex.constraints.expressions

import com.microsoft.z3.FuncDecl

interface ReferenceMapping {
    val hashFunctions: MutableMap<String, FuncDecl>
    val referencesForward: MutableMap<Expression.Reference, String>
    val referencesBackward: MutableMap<String, Expression.Reference>
}
