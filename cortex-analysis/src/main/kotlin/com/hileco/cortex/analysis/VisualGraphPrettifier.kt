package com.hileco.cortex.analysis

import com.hileco.cortex.collections.BranchedStack
import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.stack.DUPLICATE
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import com.hileco.cortex.symbolic.instructions.stack.SWAP

class VisualGraphPrettifier {

    class UnknownInstruction : Instruction() {
        override fun toString(): String {
            return "?"
        }
    }

    data class ConsumeMapping(
            val instruction: Instruction = UnknownInstruction(),
            val parameters: MutableList<ConsumeMapping> = mutableListOf(),
            var consumed: Boolean = false
    )

    fun format(instruction: Instruction): String {
        if (instruction is PUSH) {
            return instruction.value.toString().replaceFirst("^(00)+(?!$)".toRegex(), "")
        }
        return instruction.toString()
    }

    private fun format(consumeMapping: ConsumeMapping): String {
        val parameters = consumeMapping.parameters.joinToString(", ") {
            format(it)
        }
        val instruction = format(consumeMapping.instruction)
        if (parameters.isBlank()) {
            return instruction
        } else {
            return "$instruction($parameters)"
        }
    }

    fun prettify(instructions: List<Instruction>): List<String> {
        val mappings = instructions.map {
            ConsumeMapping(it, mutableListOf(), false)
        }
        val stack = BranchedStack<ConsumeMapping>()
        instructions.forEach { instruction ->
            val furthestParameter = instruction.stackParameters.maxBy { parameter -> parameter.position }
            val furthestParameterPosition = if (furthestParameter != null) furthestParameter.position + 1 else 0
            for (i in 0 until furthestParameterPosition) {
                stack.push(ConsumeMapping())
            }
        }
        mappings.forEach { mapping ->
            when (mapping.instruction) {
                is DUPLICATE -> stack.duplicate(mapping.instruction.topOffset)
                is SWAP -> stack.swap(mapping.instruction.topOffsetLeft, mapping.instruction.topOffsetRight)
                else -> {
                    mapping.instruction.stackParameters.forEach { _ ->
                        val parameterMapping = stack.pop()
                        mapping.parameters.add(parameterMapping)
                        parameterMapping.consumed = true
                    }
                    mapping.instruction.stackAdds.forEach { _ ->
                        stack.push(mapping)
                    }
                }
            }

        }
        return mappings.asSequence()
                .filterNot { it.consumed }
                .map { format(it) }
                .toList()
    }
}
