package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class DestinationNode(val line: Int, val nodes: List<TreeNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printBlock(printStream, "DESTINATION $line", nodes, offset + 2)
    }
}