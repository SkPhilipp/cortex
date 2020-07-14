package com.hileco.cortex.vm.instructions

import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.instructions.InstructionsBuilder.FunctionCallConvention.*
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_NOT
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.BITWISE_XOR
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.calls.CALL_RETURN
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.GREATER_THAN
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.conditions.LESS_THAN
import com.hileco.cortex.vm.instructions.debug.DROP
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.*
import com.hileco.cortex.vm.instructions.stack.*
import java.util.*

class InstructionsBuilderHandle

@Suppress("UNUSED_PARAMETER")
class InstructionsBuilder {
    private val handle: InstructionsBuilderHandle = InstructionsBuilderHandle()
    private val instructions: MutableList<() -> Instruction> = ArrayList()
    private val labelAddresses: MutableMap<String, Int> = HashMap()

    fun bitwiseAnd(right: InstructionsBuilderHandle = handle,
                   left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { BITWISE_AND() }
        return handle
    }

    fun bitwiseNot(value: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { BITWISE_NOT() }
        return handle
    }

    fun bitwiseOr(right: InstructionsBuilderHandle = handle,
                  left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { BITWISE_OR() }
        return handle
    }

    fun bitwiseXor(right: InstructionsBuilderHandle = handle,
                   left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { BITWISE_XOR() }
        return handle
    }

    fun call(outSize: InstructionsBuilderHandle = handle,
             outOffset: InstructionsBuilderHandle = handle,
             inSize: InstructionsBuilderHandle = handle,
             inOffset: InstructionsBuilderHandle = handle,
             valueTransferred: InstructionsBuilderHandle = handle,
             recipientAddress: InstructionsBuilderHandle = handle) {
        instructions.add { CALL() }
    }

    fun callReturn(size: InstructionsBuilderHandle = handle,
                   offset: InstructionsBuilderHandle = handle) {
        instructions.add { CALL_RETURN() }
    }

    fun equals(right: InstructionsBuilderHandle = handle,
               left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { EQUALS() }
        return handle
    }

    fun greaterThan(right: InstructionsBuilderHandle = handle,
                    left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { GREATER_THAN() }
        return handle
    }

    fun isZero(value: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { IS_ZERO() }
        return handle
    }

    fun lessThan(right: InstructionsBuilderHandle = handle,
                 left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { LESS_THAN() }
        return handle
    }

    fun halt(reason: ProgramException.Reason) {
        instructions.add { HALT(reason) }
    }

    fun noop() {
        instructions.add { NOOP() }
    }

    fun load(programStoreZone: ProgramStoreZone,
             address: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { LOAD(programStoreZone) }
        return handle
    }

    fun save(programStoreZone: ProgramStoreZone,
             bytes: InstructionsBuilderHandle = handle,
             address: InstructionsBuilderHandle = handle) {
        instructions.add { SAVE(programStoreZone) }
    }

    fun exit() {
        instructions.add { EXIT() }
    }

    fun jump(address: InstructionsBuilderHandle = handle) {
        instructions.add { JUMP() }
    }

    fun jump(label: String) {
        push(label)
        instructions.add { JUMP() }
    }

    fun jumpDestination(name: String) {
        if (labelAddresses.containsKey(name)) {
            throw IllegalArgumentException("Name $name is already taken")
        }
        labelAddresses[name] = instructions.size
        instructions.add { JUMP_DESTINATION() }
    }

    fun jumpDestination() {
        instructions.add { JUMP_DESTINATION() }
    }

    fun jumpIf(condition: InstructionsBuilderHandle = handle,
               address: InstructionsBuilderHandle = handle) {
        instructions.add { JUMP_IF() }
    }

    fun jumpIf(condition: InstructionsBuilderHandle = handle,
               label: String) {
        push(label)
        instructions.add { JUMP_IF() }
    }

    fun add(right: InstructionsBuilderHandle = handle,
            left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { ADD() }
        return handle
    }

    fun exponent(right: InstructionsBuilderHandle = handle,
                 left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { EXPONENT() }
        return handle
    }

    fun divide(right: InstructionsBuilderHandle = handle,
               left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { DIVIDE() }
        return handle
    }

    fun hash(hashMethod: String,
             input: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { HASH(hashMethod) }
        return handle
    }

    fun modulo(right: InstructionsBuilderHandle = handle,
               left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { MODULO() }
        return handle
    }

    fun multiply(right: InstructionsBuilderHandle = handle,
                 left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { MULTIPLY() }
        return handle
    }

    fun subtract(right: InstructionsBuilderHandle = handle,
                 left: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { SUBTRACT() }
        return handle
    }

    fun duplicate(topOffset: Int): InstructionsBuilderHandle {
        instructions.add { DUPLICATE(topOffset) }
        return handle
    }

    fun duplicate(value: InstructionsBuilderHandle = handle): InstructionsBuilderHandle {
        instructions.add { DUPLICATE(0) }
        return handle
    }

    fun pop(value: InstructionsBuilderHandle = handle) {
        instructions.add { POP() }
    }

    fun drop(vararg values: InstructionsBuilderHandle) {
        instructions.add { DROP(values.size) }
    }

    fun push(value: ByteArray): InstructionsBuilderHandle {
        instructions.add { PUSH(value) }
        return handle
    }

    fun push(value: Long): InstructionsBuilderHandle {
        instructions.add { PUSH(value) }
        return handle
    }

    fun variable(executionVariable: ExecutionVariable): InstructionsBuilderHandle {
        instructions.add { VARIABLE(executionVariable) }
        return handle
    }

    fun push(label: String): InstructionsBuilderHandle {
        instructions.add {
            val address = labelAddresses[label]
            if (address == null) {
                throw IllegalStateException("No label for name $label")
            } else {
                PUSH(address.toBigInteger().toByteArray())
            }
        }
        return handle
    }

    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        instructions.add { SWAP(topOffsetLeft, topOffsetRight) }
    }

    fun swap(right: InstructionsBuilderHandle = handle,
             left: InstructionsBuilderHandle = handle) {
        instructions.add { SWAP(0, 1) }
    }

    fun blockLoop(loopBody: (doContinue: () -> Unit, doBreak: () -> Unit) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        loopBody({
            jump(startLabel)
        }, {
            jump(endLabel)
        })
        jump(startLabel)
        jumpDestination(endLabel)
    }

    fun blockLoop(loopBody: () -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        loopBody()
        jump(startLabel)
    }

    fun blockWhile(conditionBody: () -> Unit,
                   loopBody: (doContinue: () -> Unit, doBreak: () -> Unit) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        conditionBody()
        jumpIf(isZero(), endLabel)
        loopBody({
            jump(startLabel)
        }, {
            jump(endLabel)
        })
        jump(startLabel)
        jumpDestination(endLabel)
    }

    fun blockDoWhile(body: (doContinue: () -> Unit, doBreak: () -> Unit) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        body({
            jump(startLabel)
        }, {
            jump(endLabel)
        })
        jumpIf(label = startLabel)
        jumpDestination(endLabel)
    }

    fun blockDoWhile(body: () -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        body()
        jumpIf(label = startLabel)
    }

    fun blockIf(conditionBody: () -> Unit, thenBody: () -> Unit) {
        val endLabel = UUID.randomUUID().toString()
        conditionBody()
        jumpIf(isZero(), endLabel)
        thenBody()
        jumpDestination(endLabel)
    }

    fun blockIfElse(conditionBody: () -> Unit, thenBody: () -> Unit, elseBody: () -> Unit) {
        val endLabel = UUID.randomUUID().toString()
        val elseLabel = UUID.randomUUID().toString()
        conditionBody()
        jumpIf(isZero(), elseLabel)
        thenBody()
        jump(endLabel)
        jumpDestination(elseLabel)
        elseBody()
        jumpDestination(endLabel)
    }

    fun blockSwitch(controlBody: () -> Unit = {}, cases: List<Pair<Long, () -> Unit>>) {
        blockSwitch(controlBody, cases.map { it.first }, { caseNumber -> cases.first { it.first == caseNumber } })
    }

    fun blockSwitch(controlBody: () -> Unit = {}, cases: List<Long>, caseBuilder: (Long) -> Unit) {
        controlBody()
        val labels = HashMap<Long, String>()
        val endLabel = UUID.randomUUID().toString()
        for (case in cases) {
            val label = UUID.randomUUID().toString()
            labels[case] = label
            jumpIf(equals(push(case), duplicate()), label)
        }
        pop()
        jump(endLabel)
        for (label in labels) {
            jumpDestination(label.value)
            caseBuilder(label.key)
            jump(endLabel)
        }
        jumpDestination(endLabel)
    }

    enum class FunctionCallConvention {
        STACK_ADDRESS_WITH_RETURN,
        STACK_ADDRESS_NO_RETURN,
        MEMORY_INDEX_AND_ADDRESS
    }

    fun internalFunction(label: String, body: () -> Unit = {}, callConvention: FunctionCallConvention = STACK_ADDRESS_WITH_RETURN) {
        exit()
        jumpDestination(label)
        body()
        when (callConvention) {
            STACK_ADDRESS_WITH_RETURN -> {
                swap(0, 1)
            }
            STACK_ADDRESS_NO_RETURN -> {
            }
            MEMORY_INDEX_AND_ADDRESS -> {
                push(FUNCTION_CALL_INDEX)
                load(MEMORY)
                load(MEMORY)
                save(MEMORY, subtract(push(1), load(MEMORY, push(FUNCTION_CALL_INDEX))), push(FUNCTION_CALL_INDEX))
            }
        }
        jump()
    }

    fun configureCallConvention() {
        save(MEMORY, push(FUNCTION_CALL_INDEX + 1), push(FUNCTION_CALL_INDEX))
    }

    fun internalFunctionCall(label: String, body: () -> Unit = {}, callConvention: FunctionCallConvention = STACK_ADDRESS_WITH_RETURN): InstructionsBuilderHandle {
        val returnLabel = UUID.randomUUID().toString()
        when (callConvention) {
            STACK_ADDRESS_WITH_RETURN -> {
                push(returnLabel)
            }
            STACK_ADDRESS_NO_RETURN -> {
                push(returnLabel)
            }
            MEMORY_INDEX_AND_ADDRESS -> {
                save(MEMORY, add(load(MEMORY, push(FUNCTION_CALL_INDEX)), push(1)), push(FUNCTION_CALL_INDEX))
                save(MEMORY, push(returnLabel), load(MEMORY, push(FUNCTION_CALL_INDEX)))
            }
        }
        body()
        jump(push(label))
        jumpDestination(returnLabel)
        return handle
    }

    fun build(): List<Instruction> {
        return instructions.map { it() }
    }

    companion object {
        const val FUNCTION_CALL_INDEX = 8005L
    }
}
