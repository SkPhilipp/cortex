package com.hileco.cortex.server.pages

import com.hileco.cortex.database.Database
import io.javalin.Context

class IndexController {
    fun indexPage(ctx: Context) {
        ctx.render("index.j2", mapOf(
                "mode" to ctx.queryParam("mode"),
                "programs" to Database.programRepository.findAll().map { ProgramController.UiProgram(it) }.toList(),
                "samples" to SampleController.SAMPLES.values
        ))
    }
}
