package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class BreakNode : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printlnOffset(printStream, "BREAK", offset + 2)
    }
}