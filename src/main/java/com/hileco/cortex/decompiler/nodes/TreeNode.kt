package com.hileco.cortex.decompiler.nodes

import java.io.ByteArrayOutputStream
import java.io.PrintStream


abstract class TreeNode {

    protected fun printlnOffset(printStream: PrintStream, text: String, offset: Int = 0) {
        for (i in 1..offset) {
            printStream.print(" ")
        }
        printStream.println(text)
    }

    protected fun printBlock(printStream: PrintStream, name: String, nodes: List<TreeNode>, offset: Int = 0) {
        for (i in 1..offset) {
            printStream.print(" ")
        }
        printStream.print(name)
        if (!nodes.isEmpty()) {
            printStream.println(" {")
            nodes.forEach {
                it.print(printStream, offset + 2)
            }
            for (i in 1..offset) {
                printStream.print(" ")
            }
            printStream.println("}")
        } else {
            printStream.println()
        }
    }

    abstract fun print(printStream: PrintStream, offset: Int = -2)

    override fun toString(): String {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        print(printStream)
        return "$outputStream"
    }
}
