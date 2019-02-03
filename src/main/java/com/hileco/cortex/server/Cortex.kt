package com.hileco.cortex.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.google.common.io.Resources
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.server.pages.IndexController
import com.hileco.cortex.server.pages.ProgramController
import com.hileco.cortex.server.pages.SampleController
import com.hileco.cortex.server.serialization.ExpressionSerializer
import com.hileco.cortex.server.serialization.InstructionDeserializer
import com.hileco.cortex.server.serialization.InstructionSerializer
import com.hileco.cortex.server.serialization.LayeredStackSerializer
import com.hubspot.jinjava.Jinjava
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.json.JavalinJackson
import io.javalin.rendering.FileRenderer
import io.javalin.rendering.JavalinRenderer


fun main() {
    val jinjava = Jinjava()
    JavalinRenderer.register(FileRenderer { filePath: String, model: Map<String, Any> ->
        val resource = Resources.getResource("templates/$filePath")
        val template = Resources.toString(resource, Charsets.UTF_8)
        jinjava.render(template, model)
    }, ".j2")
    JavalinJackson.configure(ObjectMapper().let {
        val module = SimpleModule()
        module.addSerializer(ByteArraySerializer())
        module.addSerializer(ExpressionSerializer())
        module.addSerializer(InstructionSerializer())
        module.addSerializer(LayeredStackSerializer())
        module.addDeserializer(Instruction::class.java, InstructionDeserializer())
        it.registerModule(module)
        it.enable(SerializationFeature.INDENT_OUTPUT)
    })
    val app = Javalin.create().start(8080)
    val indexController = IndexController()
    val programController = ProgramController()
    val sampleController = SampleController()
    app.routes {
        get("/", indexController::indexPage)
        path("/programs") {
            get("/new", programController::newPage)
            post("/new", programController::new)
            get("/view/:id", programController::viewPage)
            post("/solve", programController::solve)
            post("/optimize", programController::optimize)
            post("/run", programController::run)
            post("/graph", programController::graph)
        }
        path("/samples") {
            get("/view/:id", sampleController::viewPage)
        }
    }
}
