package com.hileco.cortex.analysis

import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import org.junit.Assert
import org.junit.Test

class GraphTest {
    @Test
    fun test() {
        val graph = Graph(listOf(
                JUMP_DESTINATION(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ))
        Assert.assertEquals(3, graph.graphBlocks.size)
    }
}