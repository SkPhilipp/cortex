package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.Expression
import com.hileco.cortex.symbolic.Not
import com.hileco.cortex.symbolic.Value
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable
import java.math.BigInteger
import java.util.*

class SymbolicVirtualMachine : VmComponent<SymbolicVirtualMachine> {
    val programs: MutableList<SymbolicProgramContext>
    val atlas: MutableMap<BigInteger, SymbolicProgram>
    val variables: VmMap<ExecutionVariable, Expression>
    val path: VmStack<SymbolicPathEntry>
    var instructionsExecuted: Int
    var exited: Boolean = false
    var exitedReason: ProgramException.Reason? = null

    constructor(vararg programContexts: SymbolicProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = Stack()
        atlas = HashMap()
        path = LayeredVmStack()
        variables = LayeredVmMap()
        variables[ExecutionVariable.START_TIME] = Value(startTime)
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
    }

    private constructor(programs: MutableList<SymbolicProgramContext>,
                        atlas: MutableMap<BigInteger, SymbolicProgram>,
                        path: VmStack<SymbolicPathEntry>,
                        variables: VmMap<ExecutionVariable, Expression>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.path = path
        this.variables = variables
        this.instructionsExecuted = instructionsExecuted
    }

    fun condition(): Expression {
        val expressions: MutableList<Expression> = arrayListOf()
        path.asSequence()
                .map { if (it.taken) it.condition else Not(it.condition) }
                .forEach { expression -> expressions.add(expression) }
        return Expression.constructAnd(expressions)
    }

    override fun close() {
        programs.forEach { it.close() }
        atlas.values.forEach { it.close() }
        variables.close()
        path.close()
    }

    override fun copy(): SymbolicVirtualMachine {
        val branchPrograms = programs.map { program -> program.copy() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.copy() }.toMutableMap()
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, path.copy(), variables.copy(), instructionsExecuted)
    }
}
