package com.hileco.cortex.documentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.documentation.source.ByteArraySerializer
import com.hileco.cortex.documentation.source.ExpressionSerializer
import com.hileco.cortex.documentation.source.InstructionSerializer
import com.hileco.cortex.documentation.source.LayeredStackSerializer
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.LayeredStack
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object Documentation {

    private val DOCS_PATH: Path
    private val OPEN_DOCUMENTS: MutableMap<String, Document>
    private val OBJECT_MAPPER: ObjectMapper

    init {
        val userDir = System.getProperty("user.dir")
        DOCS_PATH = Paths.get(userDir, "build/generated-snippets")
        OPEN_DOCUMENTS = HashMap()
        val module = SimpleModule()
        module.addSerializer(LayeredStack::class.java, LayeredStackSerializer())
        module.addSerializer(Instruction::class.java, InstructionSerializer())
        module.addSerializer(Expression::class.java, ExpressionSerializer())
        module.addSerializer(ByteArray::class.java, ByteArraySerializer())
        OBJECT_MAPPER = ObjectMapper()
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT)
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        OBJECT_MAPPER.registerModule(module)

    }

    fun of(snippetPath: String): Document {
        val separator = System.getProperty("file.separator")
        val path = DOCS_PATH.resolve(String.format("%s.adoc", snippetPath.replace("/", separator)))
        return (OPEN_DOCUMENTS).computeIfAbsent(path.toString()) {
            try {
                val file = path.toFile()
                if (!file.exists()) {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                }
                val outputStream = FileOutputStream(file, !file.exists())
                Document(snippetPath, outputStream, OBJECT_MAPPER)
            } catch (e: IOException) {
                throw IllegalStateException(String.format("Erred while interacting with file: %s", path), e)
            }
        }
    }
}
