package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.expressions.Expression

data class SymbolicPathEntry(val source: Int,
                             val target: Expression,
                             val taken: Boolean,
                             val condition: Expression)
