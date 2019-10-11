package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.symbolic.Expression

data class SymbolicPathEntry(val source: Int,
                             val target: Expression,
                             val taken: Boolean,
                             val condition: Expression)
