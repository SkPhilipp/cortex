package com.hileco.cortex.analysis.decompile

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.instructions.ProgramBuilder
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class DecompilerTest {
    @Test
    fun decompile() {
        val instructions = with(ProgramBuilder()) {
            halt(WINNER)
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue(model.contains { it is Line })
    }

    @Ignore
    @Test
    fun decompileSingleBranch() {
        val instructions = with(ProgramBuilder()) {
            blockIf(conditionBody = {
                equals(push(1), load(CALL_DATA, push(0)))
            }, thenBody = {
                halt(WINNER)
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
    }

    @Ignore
    @Test
    fun decompileMultipleBranches() {
        val instructions = with(ProgramBuilder()) {
            blockIfElse(conditionBody = {
                equals(push(1), load(CALL_DATA, push(0)))
            }, thenBody = {
                halt(WINNER)
            }, elseBody = {
                exit()
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
    }

    @Ignore
    @Test
    fun decompileSwitch() {
        val instructions = with(ProgramBuilder()) {
            blockSwitch(controlBody = {
                load(CALL_DATA, push(0))
            }, cases = listOf(
                    1L to {
                        halt(WINNER)
                    },
                    2L to {
                        halt(WINNER)
                    },
                    3L to {
                        exit()
                    }
            ))
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
    }

    @Ignore
    @Test
    fun decompileLoop() {
        val instructions = with(ProgramBuilder()) {
            val varX = 2345L
            val varY = 6789L
            save(ProgramStoreZone.MEMORY, load(CALL_DATA, push(0)), push(varX))
            save(ProgramStoreZone.MEMORY, push(0), push(varY))
            blockWhile(conditionBody = {
                save(ProgramStoreZone.MEMORY, subtract(push(1), load(ProgramStoreZone.MEMORY, push(varX))), push(varX))
                load(ProgramStoreZone.MEMORY, push(varX))
            }, loopBody = { _, _ ->
                save(ProgramStoreZone.MEMORY, add(push(1), load(ProgramStoreZone.MEMORY, push(varY))), push(varY))
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains a loop", model.contains { it is Loop })
    }

    @Ignore
    @Test
    fun decompileLoopBranches() {
        val instructions = with(ProgramBuilder()) {
            val varX = 2345L
            val varY = 6789L
            save(ProgramStoreZone.MEMORY, load(CALL_DATA, push(0)), push(varX))
            save(ProgramStoreZone.MEMORY, push(0), push(varY))
            blockWhile(conditionBody = {
                save(ProgramStoreZone.MEMORY, subtract(push(1), load(ProgramStoreZone.MEMORY, push(varX))), push(varX))
                load(ProgramStoreZone.MEMORY, push(varX))
            }, loopBody = { _, _ ->
                blockIf(conditionBody = {
                    equals(push(10), load(ProgramStoreZone.MEMORY, push(varX)))
                }, thenBody = {
                    halt(WINNER)
                })
                save(ProgramStoreZone.MEMORY, add(push(1), load(ProgramStoreZone.MEMORY, push(varY))), push(varY))
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains a loop", model.contains { it is Loop })
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
    }

    @Ignore
    @Test
    fun decompileLoopBranchesContinue() {
        val instructions = with(ProgramBuilder()) {
            val varX = 2345L
            val varY = 6789L
            save(ProgramStoreZone.MEMORY, load(CALL_DATA, push(0)), push(varX))
            save(ProgramStoreZone.MEMORY, push(0), push(varY))
            blockWhile(conditionBody = {
                save(ProgramStoreZone.MEMORY, subtract(push(1), load(ProgramStoreZone.MEMORY, push(varX))), push(varX))
                load(ProgramStoreZone.MEMORY, push(varX))
            }, loopBody = { doContinue, _ ->
                blockIf(conditionBody = {
                    equals(push(10), load(ProgramStoreZone.MEMORY, push(varX)))
                }, thenBody = {
                    doContinue()
                })
                save(ProgramStoreZone.MEMORY, add(push(1), load(ProgramStoreZone.MEMORY, push(varY))), push(varY))
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains a loop", model.contains { it is Loop })
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
        Assert.assertTrue("Model for $instructions contains continue", model.contains { it is Continue })
    }

    @Ignore
    @Test
    fun decompileLoopBranchesBreak() {
        val instructions = with(ProgramBuilder()) {
            val varX = 2345L
            val varY = 6789L
            save(ProgramStoreZone.MEMORY, load(CALL_DATA, push(0)), push(varX))
            save(ProgramStoreZone.MEMORY, push(0), push(varY))
            blockWhile(conditionBody = {
                save(ProgramStoreZone.MEMORY, subtract(push(1), load(ProgramStoreZone.MEMORY, push(varX))), push(varX))
                load(ProgramStoreZone.MEMORY, push(varX))
            }, loopBody = { doContinue, _ ->
                blockIf(conditionBody = {
                    equals(push(10), load(ProgramStoreZone.MEMORY, push(varX)))
                }, thenBody = {
                    doContinue()
                })
                save(ProgramStoreZone.MEMORY, add(push(1), load(ProgramStoreZone.MEMORY, push(varY))), push(varY))
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains a loop", model.contains { it is Loop })
        Assert.assertTrue("Model for $instructions contains branches", model.contains { it is Branches })
        Assert.assertTrue("Model for $instructions contains continue", model.contains { it is Break })
    }

    @Ignore
    @Test
    fun decompileFunctionDefinitionsFunctionCalls() {
        val instructions = with(ProgramBuilder()) {
            blockIf(conditionBody = {
                internalFunctionCall("cube", {
                    load(CALL_DATA, push(1))
                })
            }, thenBody = {
                halt(WINNER)
            })
            internalFunction("cube", {
                internalFunctionCall("square", {
                    duplicate(1)
                })
                multiply()
            })
            internalFunction("square", {
                duplicate()
                multiply()
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val model = Decompiler().decompile(graph)
        Assert.assertTrue("Model for $instructions contains function definitions", model.contains { it is FunctionDefinition })
        Assert.assertTrue("Model for $instructions contains function calls", model.contains { it is FunctionCall })
        Assert.assertTrue("Model for $instructions contains function returns", model.contains { it is FunctionReturn })
        Assert.assertTrue("Model for $instructions contains function branches", model.contains { it is Branches })
    }
}