package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.ProgramBuilder.FunctionCallConvention.*
import com.hileco.cortex.instructions.bits.BITWISE_AND
import com.hileco.cortex.instructions.bits.BITWISE_NOT
import com.hileco.cortex.instructions.bits.BITWISE_OR
import com.hileco.cortex.instructions.bits.BITWISE_XOR
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.GREATER_THAN
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.conditions.LESS_THAN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.*
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.POP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import java.math.BigInteger
import java.util.*

class ProgramBuilderHandle

@Suppress("UNUSED_PARAMETER")
class ProgramBuilder {
    private val handle: ProgramBuilderHandle = ProgramBuilderHandle()
    private val instructions: MutableList<() -> Instruction> = ArrayList()
    private val labelAddresses: MutableMap<String, Int> = HashMap()

    fun bitwiseAnd(right: ProgramBuilderHandle = handle,
                   left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { BITWISE_AND() }
        return handle
    }

    fun bitwiseNot(value: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { BITWISE_NOT() }
        return handle
    }

    fun bitwiseOr(right: ProgramBuilderHandle = handle,
                  left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { BITWISE_OR() }
        return handle
    }

    fun bitwiseXor(right: ProgramBuilderHandle = handle,
                   left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { BITWISE_XOR() }
        return handle
    }

    fun call(outSize: ProgramBuilderHandle = handle,
             outOffset: ProgramBuilderHandle = handle,
             inSize: ProgramBuilderHandle = handle,
             inOffset: ProgramBuilderHandle = handle,
             valueTransferred: ProgramBuilderHandle = handle,
             recipientAddress: ProgramBuilderHandle = handle) {
        instructions.add { CALL() }
    }

    fun callReturn(size: ProgramBuilderHandle = handle,
                   offset: ProgramBuilderHandle = handle) {
        instructions.add { CALL_RETURN() }
    }

    fun equals(right: ProgramBuilderHandle = handle,
               left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { EQUALS() }
        return handle
    }

    fun greaterThan(right: ProgramBuilderHandle = handle,
                    left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { GREATER_THAN() }
        return handle
    }

    fun isZero(value: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { IS_ZERO() }
        return handle
    }

    fun lessThan(right: ProgramBuilderHandle = handle,
                 left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
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
             address: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { LOAD(programStoreZone) }
        return handle
    }

    fun save(programStoreZone: ProgramStoreZone,
             bytes: ProgramBuilderHandle = handle,
             address: ProgramBuilderHandle = handle) {
        instructions.add { SAVE(programStoreZone) }
    }

    fun exit() {
        instructions.add { EXIT() }
    }

    fun jump(address: ProgramBuilderHandle = handle) {
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

    fun jumpIf(condition: ProgramBuilderHandle = handle,
               address: ProgramBuilderHandle = handle) {
        instructions.add { JUMP_IF() }
    }

    fun jumpIf(condition: ProgramBuilderHandle = handle,
               label: String) {
        push(label)
        instructions.add { JUMP_IF() }
    }

    fun add(right: ProgramBuilderHandle = handle,
            left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { ADD() }
        return handle
    }

    fun divide(right: ProgramBuilderHandle = handle,
               left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { DIVIDE() }
        return handle
    }

    fun hash(hashMethod: String,
             right: ProgramBuilderHandle = handle,
             left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { HASH(hashMethod) }
        return handle
    }

    fun modulo(right: ProgramBuilderHandle = handle,
               left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { MODULO() }
        return handle
    }

    fun multiply(right: ProgramBuilderHandle = handle,
                 left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { MULTIPLY() }
        return handle
    }

    fun subtract(right: ProgramBuilderHandle = handle,
                 left: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { SUBTRACT() }
        return handle
    }

    fun duplicate(topOffset: Int): ProgramBuilderHandle {
        instructions.add { DUPLICATE(topOffset) }
        return handle
    }

    fun duplicate(value: ProgramBuilderHandle = handle): ProgramBuilderHandle {
        instructions.add { DUPLICATE(0) }
        return handle
    }

    fun pop(value: ProgramBuilderHandle = handle) {
        instructions.add { POP() }
    }

    fun push(value: ByteArray): ProgramBuilderHandle {
        instructions.add { PUSH(value) }
        return handle
    }

    fun push(value: Long): ProgramBuilderHandle {
        instructions.add { PUSH(value) }
        return handle
    }

    fun push(label: String): ProgramBuilderHandle {
        instructions.add {
            val address = labelAddresses[label]
            if (address == null) {
                throw IllegalStateException("No label for name $label")
            } else {
                PUSH(BigInteger.valueOf(address.toLong()).toByteArray())
            }
        }
        return handle
    }

    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        instructions.add { SWAP(topOffsetLeft, topOffsetRight) }
    }

    fun swap(right: ProgramBuilderHandle = handle,
             left: ProgramBuilderHandle = handle) {
        instructions.add { SWAP(0, 1) }
    }

    fun blockLoop(loopBody: () -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        loopBody()
        jump(startLabel)
    }

    fun blockWhile(conditionBody: () -> Unit,
                   loopBody: () -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        jumpDestination(startLabel)
        conditionBody()
        jumpIf(isZero(), endLabel)
        loopBody()
        jump(startLabel)
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

    fun internalFunctionCall(label: String, body: () -> Unit = {}, callConvention: FunctionCallConvention = STACK_ADDRESS_WITH_RETURN) {
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
    }

    fun build(): List<Instruction> {
        return instructions.map { it() }
    }

    companion object {
        const val FUNCTION_CALL_INDEX = 8005L
    }
}
