package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class IfThenElseNode(val condition: List<TreeNode>,
                     val thenBlock: List<TreeNode>,
                     val elseBlock: List<TreeNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printBlock(printStream, "IF", condition, offset + 2)
        printBlock(printStream, "THEN", thenBlock, offset + 2)
        printBlock(printStream, "ELSE", elseBlock, offset + 2)
    }
}