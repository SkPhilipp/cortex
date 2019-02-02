package com.hileco.cortex.server

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.attack.Attacker
import com.hileco.cortex.database.Database
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.server.Templates.Companion.OBJECT_MAPPER
import com.hileco.cortex.server.serialization.InstructionParser
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import java.math.BigInteger
import java.util.regex.Pattern

data class UiProgram(val id: String, val source: String) {
    constructor(program: Program) : this(program.address.toString(), program.instructions.joinToString(separator = "\n") { "$it" })
}

data class UiSample(val id: String, val pseudocode: String, val source: String, val name: String, val description: String)

fun main() {
    val templates = Templates()
    val app = Javalin.create().start(8080)
    app.get("/") { ctx ->
        ctx.contentType("text/html")
        ctx.result(templates.render("index.html", mapOf(
                "programs" to Database.programRepository.findAll().map { UiProgram(it) }.toList(),
                "samples" to listOf(
                        UiSample("5fa6e92e-4f6f-4e04-9ba5-630cd406712e",
                                "IF(CALL_DATA[1] / 2 == 12345) {\n" +
                                        "    HALT(WINNER)\n" +
                                        "}",
                                "PUSH 2\n" +
                                        "PUSH 1\n" +
                                        "LOAD CALL_DATA\n" +
                                        "DIVIDE\n" +
                                        "PUSH 12345\n" +
                                        "EQUALS\n" +
                                        "IS_ZERO\n" +
                                        "PUSH 10\n" +
                                        "JUMP_IF\n" +
                                        "HALT WINNER\n" +
                                        "JUMP_DESTINATION",
                                "Barrier 1",
                                "Basic math.")
                )
        )))
    }
    app.routes {
        path("/programs") {
            get("/new") { ctx ->
                ctx.contentType("text/html")
                ctx.result(templates.render("programs-new.html"))
            }
            post("/new") { ctx ->
                val id = BigInteger(ctx.formParam("id") ?: "0")
                val source = ctx.formParam("source") ?: "NOOP"
                val instructionParser = InstructionParser()
                val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
                val program = Program(instructions, id)
                if (Database.programRepository.findOne(id) == null) {
                    Database.programRepository.save(program)
                    ctx.contentType("text/html")
                    ctx.redirect("/programs/view/$id")
                } else {
                    ctx.status(409)
                }
            }
            get("/view/:id") { ctx ->
                ctx.contentType("text/html")
                val program = Database.programRepository.findOne(BigInteger(ctx.pathParam("id")))
                if (program != null) {
                    ctx.result(templates.render("programs-view.html", mapOf(
                            "program" to UiProgram(program)
                    )))
                } else {
                    ctx.status(404)
                }
            }
            post("/solve") { ctx ->
                val start = System.currentTimeMillis()
                val source = ctx.formParam("source") ?: "NOOP"
                val instructionParser = InstructionParser()
                val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
                val program = Program(instructions)
                val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
                val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
                val solution = attacker.solve(graph)
                ctx.contentType("application/json")
                ctx.result(OBJECT_MAPPER.writeValueAsString(mapOf(
                        "solutions" to solution,
                        "milliseconds" to System.currentTimeMillis() - start
                )))
            }
            post("/optimize") { ctx ->
                val start = System.currentTimeMillis()
                val source = ctx.formParam("source") ?: "NOOP"
                val instructionParser = InstructionParser()
                val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
                val program = Program(instructions)
                val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
                ctx.contentType("application/json")
                ctx.result(OBJECT_MAPPER.writeValueAsString(mapOf(
                        "original" to instructions,
                        "optimized" to graph.toInstructions(),
                        "milliseconds" to System.currentTimeMillis() - start
                )))
            }
            post("/run") { ctx ->
                val start = System.currentTimeMillis()
                val source = ctx.formParam("source") ?: "NOOP"
                val instructionParser = InstructionParser()
                val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
                val program = Program(instructions)
                val programContext = ProgramContext(program)
                val virtualMachine = VirtualMachine(programContext)
                val programRunner = ProgramRunner(virtualMachine)
                val result = try {
                    programRunner.run()
                    mapOf("result" to "Executed successfully",
                            "stack" to programContext.stack,
                            "milliseconds" to System.currentTimeMillis() - start)
                } catch (e: ProgramException) {
                    mapOf("result" to "Halted at instruction position ${e.programContext.instructionPosition}: ${e.reason}",
                            "stack" to programContext.stack,
                            "milliseconds" to System.currentTimeMillis() - start)
                }
                ctx.contentType("application/json")
                ctx.result(OBJECT_MAPPER.writeValueAsString(result))
            }
        }
        path("/samples") {
            get("/view/:id") { ctx ->
                ctx.contentType("text/html")
                ctx.result(templates.render("samples-view.html", mapOf(
                        "sample" to UiSample("5fa6e92e-4f6f-4e04-9ba5-630cd406712e",
                                "IF(CALL_DATA[1] / 2 == 12345) {\n" +
                                        "    HALT(WINNER)\n" +
                                        "}",
                                "PUSH 2\n" +
                                        "PUSH 1\n" +
                                        "LOAD CALL_DATA\n" +
                                        "DIVIDE\n" +
                                        "PUSH 12345\n" +
                                        "EQUALS\n" +
                                        "IS_ZERO\n" +
                                        "PUSH 10\n" +
                                        "JUMP_IF\n" +
                                        "HALT WINNER\n" +
                                        "JUMP_DESTINATION",
                                "Barrier 1",
                                "Basic math.")
                )))
            }
        }
    }
}
