package com.hileco.cortex.analysis.attack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.io.serialization.InstructionParser
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class BarrierTest {
    private fun assertBarrierFile(path: String, instructions: List<Instruction>) {
        val instructionParser = InstructionParser()
        val fileInstructions = this::class.java.getResource(path)
                .openStream()
                .reader()
                .readLines()
                .map { instructionParser.parse(it) }
        Assert.assertEquals(fileInstructions, instructions)
    }

    @Test
    fun barrier01Unconditional() {
        val builder = InstructionsBuilder()
        builder.include { HALT(WINNER) }
        assertBarrierFile("/assembly/barrier-01-immediate.cxasm", builder.build())
    }

    @Test
    fun barrier02BasicMaths() {
        val builder = InstructionsBuilder()
        builder.includeIf(conditionBody = {
            builder.include { PUSH(2) }
            builder.include { PUSH(1) }
            builder.include { LOAD(CALL_DATA) }
            builder.include { DIVIDE() }
            builder.include { PUSH(12345) }
            builder.include { EQUALS() }
        }, blockBody = {
            builder.include { HALT(WINNER) }
        })
        assertBarrierFile("/assembly/barrier-02-basic.cxasm", builder.build())
    }

    @Test
    fun barrier03OverflowMaths() {
        val builder = InstructionsBuilder()
        builder.includeIf(conditionBody = {
            builder.include { PUSH(VirtualMachine.NUMERICAL_LIMIT.minus(BigInteger.ONE).toByteArray()) }
            builder.include { PUSH(1) }
            builder.include { LOAD(CALL_DATA) }
            builder.include { ADD() }
            builder.include { PUSH(12345) }
            builder.include { EQUALS() }
        }, blockBody = {
            builder.include { HALT(WINNER) }
        })
        assertBarrierFile("/assembly/barrier-03-overflow.cxasm", builder.build())
    }

    @Test
    fun barrier04Loops() {
        val builder = InstructionsBuilder()
        builder.include { PUSH(0) }
        builder.include { LOAD(CALL_DATA) }
        builder.include { PUSH(0) }
        builder.include { SAVE(MEMORY) }
        builder.include { PUSH(0) }
        builder.include { PUSH(1000) }
        builder.include { SAVE(MEMORY) }
        builder.includeLoop(conditionBody = {
            builder.decrement(MEMORY, BigInteger.valueOf(0).toByteArray())
            builder.include { PUSH(0) }
            builder.include { LOAD(MEMORY) }
            builder.include { PUSH(0) }
            builder.include { EQUALS() }
            builder.include { IS_ZERO() }
        }, loopBody = {
            builder.increment(MEMORY, BigInteger.valueOf(1000).toByteArray())
        })
        builder.includeIf(conditionBody = {
            builder.include { PUSH(1000) }
            builder.include { LOAD(MEMORY) }
            builder.include { PUSH(5) }
            builder.include { EQUALS() }
        }, blockBody = {
            builder.include { HALT(WINNER) }
        })
        assertBarrierFile("/assembly/barrier-04-loops.cxasm", builder.build())
    }

    @Test
    fun barrier05InfiniteLoops() {
    }

    @Test
    fun barrier06Memory() {
    }

    @Test
    fun barrier07Library() {
    }

    @Test
    fun barrier08PredictableRandomness() {
    }

    @Test
    fun barrier09PermanentDisk() {
    }

    @Test
    fun barrier10DynamicDisk() {
    }

    @Test
    fun barrier11MultipleCalls() {
    }
}