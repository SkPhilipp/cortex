package com.hileco.cortex.server

import com.hileco.cortex.ethereum.*
import com.hileco.cortex.server.models.BytecodeModel
import com.hileco.cortex.server.uploads.mapped
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.pebble.Pebble
import io.ktor.pebble.PebbleContent
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val bytecodes = ArrayList<BytecodeModel>()
val ethereumTranspiler = EthereumTranspiler()
val ethereumParser = EthereumParser()
val ethereumBarriers = EthereumBarriers()

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates"
        })
    }
    install(Routing) {
        static("css") {
            resources("css")
        }
        get("/") {
            call.respond(PebbleContent("index.html", mapOf(
                    "bytecodes" to bytecodes
            )))
        }
        get("/explorer/upload") {
            call.respond(PebbleContent("explorer-upload.html", mapOf()))
        }
        post("/explorer/upload") {
            val multipart = call.receiveMultipart().mapped()
            val bytecode = multipart["bytecode"] ?: throw IllegalArgumentException("Bytecode missing")
            val ethereumInstructions = ethereumParser.parse(bytecode.deserializeBytes())
            val cortexInstructions = ethereumTranspiler.transpile(ethereumInstructions)
            bytecodes.add(BytecodeModel(
                    bytecode = bytecode,
                    ethereumInstructions = ethereumInstructions,
                    cortexInstructions = cortexInstructions
            ))
            call.respondRedirect("/explorer/view/" + (bytecodes.size - 1))
        }
        get("/explorer/view/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Id missing")
            call.respond(PebbleContent("explorer-view.html", mapOf(
                    "id" to id,
                    "model" to bytecodes[id.toInt()]
            )))
        }
    }
}

fun main() {
    ethereumBarriers.all().forEach { ethereumBarrier: EthereumBarrier ->
        bytecodes.add(BytecodeModel(
                ethereumBarrier.contractCode,
                ethereumBarrier.cortexInstructions,
                ethereumBarrier.ethereumInstructions
        ))
    }
    embeddedServer(Netty, port = 8080, watchPaths = listOf(), module = Application::module).start()
}
