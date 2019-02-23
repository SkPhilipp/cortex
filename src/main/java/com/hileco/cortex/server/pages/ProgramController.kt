package com.hileco.cortex.server.pages

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.analysis.attack.Attacker
import com.hileco.cortex.database.Database
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.concrete.ProgramRunner
import com.hileco.cortex.server.serialization.InstructionParser
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import io.javalin.Context
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

class ProgramController {
    data class UiProgram(val id: String, val source: String) {
        constructor(program: Program) :
                this(program.address.toString(), program.instructions.joinToString(separator = "\n") { "$it" })
    }

    fun newPage(ctx: Context) {
        ctx.render("programs-new.j2")
    }

    fun new(ctx: Context) {
        val id = BigInteger(ctx.formParam("id") ?: "0")
        val source = ctx.formParam("source") ?: "NOOP"
        val instructionParser = InstructionParser()
        val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
        val program = Program(instructions, id)
        if (Database.programRepository.findOne(id) == null) {
            Database.programRepository.save(program)
            ctx.redirect("/programs/view/$id")
        } else {
            ctx.status(409)
        }
    }

    fun viewPage(ctx: Context) {
        val program = Database.programRepository.findOne(BigInteger(ctx.pathParam("id")))
        if (program != null) {
            ctx.render("programs-view.j2", mapOf(
                    "program" to UiProgram(program)
            ))
        } else {
            ctx.status(404)
        }
    }

    fun solve(ctx: Context) {
        val start = System.currentTimeMillis()
        val source = ctx.formParam("source") ?: "NOOP"
        val instructionParser = InstructionParser()
        val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
        val program = Program(instructions)
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solution = attacker.solve(graph)
        ctx.json(mapOf(
                "solutions" to solution,
                "milliseconds" to System.currentTimeMillis() - start
        ))
    }

    fun optimize(ctx: Context) {
        val start = System.currentTimeMillis()
        val source = ctx.formParam("source") ?: "NOOP"
        val instructionParser = InstructionParser()
        val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
        val program = Program(instructions)
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
        ctx.json(mapOf(
                "original" to instructions,
                "optimized" to graph.toInstructions(),
                "milliseconds" to System.currentTimeMillis() - start
        ))
    }

    fun run(ctx: Context) {
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
            mapOf("result" to "Halted at instruction position ${programContext.instructionPosition}: ${e.reason}",
                    "stack" to programContext.stack,
                    "milliseconds" to System.currentTimeMillis() - start)
        }
        ctx.json(result)
    }

    fun graph(ctx: Context) {
        val source = ctx.formParam("source") ?: "NOOP"
        val instructionParser = InstructionParser()
        val instructions = source.split(Pattern.compile("[\r\n]+")).map { instructionParser.parse(it) }
        val program = Program(instructions)
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
        val visualGraph = VisualGraph()
        visualGraph.map(graph)
        ctx.render("programs-graph.j2", mapOf(
                "image" to Base64.getEncoder().encodeToString(visualGraph.toBytes())
        ))
    }
}
