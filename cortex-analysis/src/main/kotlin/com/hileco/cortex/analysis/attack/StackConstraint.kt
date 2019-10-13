package com.hileco.cortex.analysis.attack

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.instructions.Instruction

data class StackConstraint(val applicabilityPredicate: (index: Int, line: Int, instruction: Instruction) -> Boolean,
                           val constraintBuilder: (expression: Expression) -> Expression,
                           val stackPosition: Int)
