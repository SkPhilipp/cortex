package com.hileco.cortex.analysis.attack

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction

data class StackConstraint(val applicabilityPredicate: (index: Int, line: Int, instruction: Instruction) -> Boolean,
                           val constraintBuilder: (expression: Expression) -> Expression,
                           val stackPosition: Int)
