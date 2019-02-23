package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.ProgramBuilder.FunctionCallConvention.MEMORY_INDEX_AND_ADDRESS
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class ProgramBuilderTest {
    @Test
    fun test() {
        val programBuilder = ProgramBuilder()
        with(programBuilder) {
            jumpDestination("repeat")
            jumpIf(equals(load(CALL_DATA, push(0)), push(123)), "repeat")
        }
        Assert.assertEquals(listOf(
                JUMP_DESTINATION(),
                PUSH(0),
                LOAD(CALL_DATA),
                PUSH(123),
                EQUALS(),
                PUSH(0),
                JUMP_IF()
        ), programBuilder.build())
    }

    @Test
    fun testFunctionCall() {
        val programBuilder = ProgramBuilder()
        with(programBuilder) {
            internalFunctionCall("multiply", {
                push(123)
                push(123)
            })
            internalFunctionCall("multiply", {
                push(123)
                push(123)
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
        Assert.assertEquals((123 * 123).toBigInteger(), BigInteger(programContext.stack.pop()))
        Assert.assertEquals((123 * 123).toBigInteger(), BigInteger(programContext.stack.pop()))
        Assert.assertTrue(programContext.stack.isEmpty())
    }

    @Test
    fun testNestedFunctionCalls() {
        val programBuilder = ProgramBuilder()
        with(programBuilder) {
            internalFunctionCall("cube", {
                push(123)
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
        Assert.assertEquals((123 * 123 * 123).toBigInteger(), BigInteger(programContext.stack.pop()))
        Assert.assertTrue(programContext.stack.isEmpty())
    }

    @Test
    fun testNestedFunctionCallsThroughMemory() {
        val programBuilder = ProgramBuilder()
        with(programBuilder) {
            configureCallConvention()
            internalFunctionCall("cube", {
                push(123)
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
        Assert.assertEquals((123 * 123 * 123).toBigInteger(), BigInteger(programContext.stack.pop()))
        Assert.assertTrue(programContext.stack.isEmpty())
    }
}