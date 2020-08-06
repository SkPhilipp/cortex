package com.hileco.cortex.server.uploads

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData

suspend fun MultiPartData.mapped(): HashMap<String, String> {
    val mapping = HashMap<String, String>()
    while (true) {
        val part = readPart() ?: break
        if (part is PartData.FormItem) {
            val partName = part.name
            if (partName != null) {
                mapping[partName] = part.value
            }
        }
        part.dispose()
    }
    return mapping
}
