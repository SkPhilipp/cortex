package com.hileco.cortex.analysis.attack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.io.serialization.InstructionParser
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
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
        builder.IF({
            builder.include(
                    listOf(
                            PUSH(BigInteger.valueOf(2).toByteArray()),
                            PUSH(BigInteger.valueOf(1).toByteArray()),
                            LOAD(CALL_DATA),
                            DIVIDE(),
                            PUSH(BigInteger.valueOf(12345).toByteArray()),
                            EQUALS())
            )
        }, {
            builder.include { HALT(WINNER) }
        })
        assertBarrierFile("/assembly/barrier-02-basic.cxasm", builder.build())
    }

    @Test
    fun barrier03OverflowMaths() {
        val builder = InstructionsBuilder()
        builder.IF({
            builder.include(
                    listOf(
                            PUSH(VirtualMachine.NUMERICAL_LIMIT.min(BigInteger.ONE).toByteArray()),
                            PUSH(BigInteger.valueOf(1).toByteArray()),
                            LOAD(CALL_DATA),
                            ADD(),
                            PUSH(BigInteger.valueOf(12345).toByteArray()),
                            EQUALS())
            )
        }, {
            builder.include { HALT(WINNER) }
        })
        assertBarrierFile("/assembly/barrier-03-overflow.cxasm", builder.build())
    }

    @Test
    fun barrier04Loops() {
        TODO("Implement")
    }

    @Test
    fun barrier05InfiniteLoops() {
        TODO("Implement")
    }

    @Test
    fun barrier06Memory() {
        TODO("Implement")
    }

    @Test
    fun barrier07Library() {
        TODO("Implement")
    }

    @Test
    fun barrier08PredictableRandomness() {
        TODO("Implement")
    }

    @Test
    fun barrier09PermanentDisk() {
        TODO("Implement")
    }

    @Test
    fun barrier10DynamicDisk() {
        TODO("Implement")
    }

    @Test
    fun barrier11MultipleCalls() {
        TODO("Implement")
    }
}