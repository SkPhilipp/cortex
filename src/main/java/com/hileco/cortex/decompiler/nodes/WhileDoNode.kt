package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class WhileDoNode(val condition: List<TreeNode>,
                  val doBlock: List<TreeNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printBlock(printStream, "WHILE", condition, offset + 2)
        printBlock(printStream, "DO", doBlock, offset + 2)
    }
}