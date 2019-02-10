package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class FunctionCallNode(val reference: Int) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printlnOffset(printStream, "FUNCTION_CALL $reference", offset + 2)
    }
}