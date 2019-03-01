package com.hileco.cortex.documentation

import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object Documentation {
    private val DOCS_PATH: Path
    private val OPEN_DOCUMENTS: MutableMap<String, Document>

    init {
        val userDir = System.getProperty("user.dir")
        DOCS_PATH = Paths.get(userDir, "build/generated-snippets")
        OPEN_DOCUMENTS = HashMap()
    }

    fun of(snippetPath: String): Document {
        val separator = System.getProperty("file.separator")
        val path = DOCS_PATH.resolve(snippetPath.replace("/", separator) + ".adoc")
        return (OPEN_DOCUMENTS).computeIfAbsent("$path") {
            try {
                val file = path.toFile()
                if (!file.exists()) {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                }
                val outputStream = FileOutputStream(file, false)
                Document(snippetPath, outputStream)
            } catch (e: IOException) {
                throw IllegalStateException("Erred while interacting with file: $path", e)
            }
        }
    }
}
