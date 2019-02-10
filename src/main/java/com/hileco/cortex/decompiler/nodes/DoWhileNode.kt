package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream

class DoWhileNode(val doLine: Int,
                  val doBlock: List<TreeNode>,
                  val conditionLine: Int,
                  val condition: List<TreeNode>) : TreeNode() {
    override fun print(printStream: PrintStream, offset: Int) {
        printBlock(printStream, "DO", doBlock, offset + 2)
        printBlock(printStream, "WHILE", condition, offset + 2)
    }
}