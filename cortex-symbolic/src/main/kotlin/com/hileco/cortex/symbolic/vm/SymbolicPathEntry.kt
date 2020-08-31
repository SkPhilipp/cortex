package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.symbolic.expressions.Expression

// TODO: Replace SymbolicPathEntry with Expression so nobody has to do Expression.Not, then clean up PathTreeExploreStrategy
data class SymbolicPathEntry(val source: Int,
                             val target: Expression,
                             val taken: Boolean,
                             val condition: Expression)
