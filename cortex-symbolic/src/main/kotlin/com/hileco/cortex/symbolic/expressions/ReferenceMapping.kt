package com.hileco.cortex.symbolic.expressions

import com.microsoft.z3.BitVecExpr
import com.microsoft.z3.FuncDecl

data class ReferenceMapping(
        val hashFunctions: MutableMap<String, FuncDecl> = HashMap(),
        val variables: MutableMap<String, BitVecExpr> = HashMap(),
        val referencesForward: MutableMap<Expression, String> = HashMap(),
        val referencesBackward: MutableMap<String, Expression> = HashMap()
)
