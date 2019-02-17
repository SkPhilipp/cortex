package com.hileco.cortex.server.pages

import com.hileco.cortex.analysis.BarrierProgram
import io.javalin.Context

class SampleController {

    data class UiSample(val id: String, val pseudocode: String, val source: String, val name: String, val description: String, val implemented: Boolean) {
        constructor(id: String, name: String, barrierProgram: BarrierProgram) :
                this(id, barrierProgram.pseudocode, barrierProgram.instructions.joinToString(separator = "\n") { "$it" }, name, barrierProgram.description, barrierProgram.instructions.isNotEmpty())
    }

    fun viewPage(ctx: Context) {
        val id = ctx.pathParam("id")
        val sample = SAMPLES[id]
        if (sample != null) {
            ctx.render("samples-view.j2", mapOf(
                    "sample" to sample
            ))
        } else {
            ctx.status(404)
        }
    }

    companion object {
        val SAMPLES = BarrierProgram.BARRIERS
                .mapIndexed { index, barrierProgram ->
                    val formattedIndex = "$index".padStart(2, '0')
                    UiSample("barrier-$formattedIndex", "Barrier $formattedIndex", barrierProgram)
                }
                .associateBy { it.id }
    }
}