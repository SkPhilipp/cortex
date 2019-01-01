package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction
import java.util.concurrent.atomic.AtomicReference

class GraphNode(val instruction: AtomicReference<Instruction>,
                val line: Int) {

    fun isInstruction(classes: Collection<Class<*>>): Boolean {
        return instruction.get()::class.java in classes
    }

    fun isInstruction(vararg classes: Class<*>): Boolean {
        return instruction.get()::class.java in classes
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format("[%03d]", line))
        stringBuilder.append(instruction)
        return "$stringBuilder"
    }
}
