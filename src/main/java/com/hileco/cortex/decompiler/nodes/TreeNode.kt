package com.hileco.cortex.decompiler.nodes

import java.io.PrintStream


open class TreeNode(val nodes: List<TreeNode> = listOf(),
                    private val name: String = "UNDEFINED") {

    fun display(printStream: PrintStream, offset: Int = 0) {
        printStream.print(name)
        if (!nodes.isEmpty()) {
            printStream.println("{")
            nodes.forEach {
                it.display(printStream, offset + 2)
            }
            printStream.println("}")
        } else {
            printStream.println()
        }
    }
}
