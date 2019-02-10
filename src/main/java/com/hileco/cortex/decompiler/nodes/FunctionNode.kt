package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class FunctionNode(val body: List<TreeNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printBlock(printStream, "FUNCTION", body, offset + 2)
    }
}