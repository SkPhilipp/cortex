package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.symbolic.expressions.Expression

data class SymbolicTransfer(val source: Expression,
                            val target: Expression,
                            val value: Expression)
