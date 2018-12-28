package com.hileco.cortex.documentation

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hileco.cortex.instructions.Instruction
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.stream.Collectors

class Document constructor(private val snippetPath: String,
                                    private val outputStream: OutputStream,
                                    private val objectMapper: ObjectMapper) {
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
        val source = instructions.stream()
                .map { Objects.toString(it) }
                .collect(Collectors.joining("\n"))
        return this.append("```\n", source, "\n```\n\n")
    }

    fun source(source: Any): Document {
        try {
            this.append("```\n", objectMapper.writeValueAsString(source), "\n```\n\n")
        } catch (e: JsonProcessingException) {
            exceptionHandler(e)
        }

        return this
    }

    fun include(otherSnippetPath: String): Document {
        return this.append("include::{snippets}/", otherSnippetPath, "[]\n\n")
    }

    fun include(document: Document): Document {
        return this.append("include::{snippets}/", document.snippetPath, "[]\n\n")
    }

    fun headingDocument(body: String): Document {
        return this.append("== ", body, "\n\n")
    }

    fun headingSection(body: String): Document {
        return this.append("=== ", body, "\n\n")
    }

    fun headingParagraph(body: String): Document {
        return this.append("==== ", body, "\n\n")
    }

    fun paragraph(body: String): Document {
        return this.append(body, "\n\n")
    }

    fun image(bytes: ByteArray): Document {
        return this.append("++++\n<p style=\"text-align: center\">\n<img src=\"data:image/png;base64,",
                Base64.getEncoder().encodeToString(bytes), "\"/>\n</p>\n++++\n\n")
    }
}
