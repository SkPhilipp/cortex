package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class ContinueNode : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printlnOffset(printStream, "CONTINUE", offset + 2)
    }
}