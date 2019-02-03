package com.hileco.cortex.server.pages

import com.hileco.cortex.analysis.BarrierProgram
import io.javalin.Context

class SampleController {

    data class UiSample(val id: String, val pseudocode: String, val source: String, val name: String, val description: String) {
        constructor(id: String, name: String, barrierProgram: BarrierProgram) :
                this(id, barrierProgram.pseudocode, barrierProgram.instructions.joinToString(separator = "\n") { "$it" }, name, barrierProgram.description)
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
        val SAMPLES = mapOf(
                "barrier-01" to UiSample("barrier-01", "Barrier 01", BarrierProgram.BARRIER_01),
                "barrier-02" to UiSample("barrier-02", "Barrier 02", BarrierProgram.BARRIER_02)
        )
    }
}