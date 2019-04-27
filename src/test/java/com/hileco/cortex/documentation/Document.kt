package com.hileco.cortex.documentation

import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.instructions.Instruction
import java.io.IOException
import java.io.OutputStream

class Document(private val outputStream: OutputStream) {
    private val exceptionHandler: (IOException) -> Nothing

    init {
        exceptionHandler = { e -> throw IllegalStateException("Unexpected exception", e) }
    }

    private fun append(vararg strings: String): Document {
        try {
            for (string in strings) {
                this.outputStream.write(string.toByteArray())
            }
        } catch (e: IOException) {
            exceptionHandler(e)
        }
        return this
    }

    fun source(instructions: List<Instruction>): Document {
        val source = instructions.joinToString(prefix = "```\n", separator = "\n", postfix = "\n```\n\n") { "$it" }
        return this.append(source)
    }

    fun source(source: Any): Document {
        this.append("```\n", "$source", "\n```\n\n")
        return this
    }

    fun headingParagraph(body: String): Document {
        return this.append("==== ", body, "\n\n")
    }

    fun paragraph(body: String): Document {
        return this.append(body, "\n\n")
    }

    fun image(visualGraph: VisualGraph): Document {
        try {
            append("++++\n")
            visualGraph.render(this.outputStream)
            append("\n++++\n\n")
        } catch (e: IOException) {
            exceptionHandler(e)
        }
        return this
    }
}
