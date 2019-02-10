package com.hileco.cortex.decompiler.nodes

import com.hileco.cortex.analysis.GraphNode
import java.io.PrintStream

class InstructionsNode(val instructions: List<GraphNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        instructions.forEach {
            printlnOffset(printStream, it.instruction.toString(), offset + 2)
        }
    }
}