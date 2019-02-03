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
        val SAMPLES = listOf(
                UiSample("barrier-00", "Barrier 00", BarrierProgram.BARRIER_00),
                UiSample("barrier-01", "Barrier 01", BarrierProgram.BARRIER_01),
                UiSample("barrier-02", "Barrier 02", BarrierProgram.BARRIER_02),
                UiSample("barrier-03", "Barrier 03", BarrierProgram.BARRIER_03),
                UiSample("barrier-04", "Barrier 04", BarrierProgram.BARRIER_04),
                UiSample("barrier-05", "Barrier 05", BarrierProgram.BARRIER_05),
                UiSample("barrier-06", "Barrier 06", BarrierProgram.BARRIER_06),
                UiSample("barrier-07", "Barrier 07", BarrierProgram.BARRIER_07),
                UiSample("barrier-08", "Barrier 08", BarrierProgram.BARRIER_08),
                UiSample("barrier-09", "Barrier 09", BarrierProgram.BARRIER_09),
                UiSample("barrier-10", "Barrier 10", BarrierProgram.BARRIER_10),
                UiSample("barrier-11", "Barrier 11", BarrierProgram.BARRIER_11)
        ).associateBy { it.id }
    }
}