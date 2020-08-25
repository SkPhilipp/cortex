package com.hileco.cortex.vm.instructions

import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramRunner
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.bytes.asUInt256
import com.hileco.cortex.vm.instructions.InstructionsBuilder.FunctionCallConvention.MEMORY_INDEX_AND_ADDRESS
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class ProgramBuilderTest {
    @Test
    fun test() {
        val programBuilder = InstructionsBuilder()
        with(programBuilder) {
            jumpDestination("repeat")
            jumpIf(equals(load(CALL_DATA, push(0.asUInt256())), push(123.asUInt256())), "repeat")
        }
        Assert.assertEquals(listOf(
                JUMP_DESTINATION(),
                PUSH(0.asUInt256()),
                LOAD(CALL_DATA),
                PUSH(123.asUInt256()),
                EQUALS(),
                PUSH(0.asUInt256()),
                JUMP_IF()
        ), programBuilder.build())
    }

    @Test
    fun testFunctionCall() {
        val programBuilder = InstructionsBuilder()
        with(programBuilder) {
            internalFunctionCall("multiply", {
                push(123.asUInt256())
                push(123.asUInt256())
            })
            internalFunctionCall("multiply", {
                push(123.asUInt256())
                push(123.asUInt256())
            })
            internalFunction("multiply", {
                multiply()
            })
        }
        val program = Program(programBuilder.build())
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals((123 * 123).asUInt256(), programContext.stack.pop())
        Assert.assertEquals((123 * 123).asUInt256(), programContext.stack.pop())
        Assert.assertTrue(programContext.stack.isEmpty())
    }

    @Test
    fun testNestedFunctionCalls() {
        val programBuilder = InstructionsBuilder()
        with(programBuilder) {
            internalFunctionCall("cube", {
                push(123.asUInt256())
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
        }
        val program = Program(programBuilder.build())
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals((123 * 123 * 123).asUInt256(), programContext.stack.pop())
        Assert.assertTrue(programContext.stack.isEmpty())
    }

    @Test
    fun testNestedFunctionCallsThroughMemory() {
        val programBuilder = InstructionsBuilder()
        with(programBuilder) {
            configureCallConvention()
            internalFunctionCall("cube", {
                push(123.asUInt256())
            }, callConvention = MEMORY_INDEX_AND_ADDRESS)
            internalFunction("cube", {
                internalFunctionCall("square", {
                    duplicate()
                }, callConvention = MEMORY_INDEX_AND_ADDRESS)
                multiply()
            }, callConvention = MEMORY_INDEX_AND_ADDRESS)
            internalFunction("square", {
                duplicate()
                multiply()
            }, callConvention = MEMORY_INDEX_AND_ADDRESS)
        }
        val program = Program(programBuilder.build())
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals((123 * 123 * 123).asUInt256(), programContext.stack.pop())
        Assert.assertTrue(programContext.stack.isEmpty())
    }
}