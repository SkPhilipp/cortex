package com.hileco.cortex.server

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
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

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
            call.respond(PebbleContent("index.html", mapOf()))
        }
    }
}

fun main() {
    embeddedServer(Netty, 8080, watchPaths = listOf(), module = Application::module).start()
}