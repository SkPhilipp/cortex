package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.instructions.Instruction

data class PathStreamElement(val instruction: Instruction,
                             val line: Int,
                             val wrappingFlow: Flow,
                             val nextFlow: Flow?)
