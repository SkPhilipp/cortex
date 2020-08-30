package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class SymbolicVirtualMachine : VmComponent<SymbolicVirtualMachine> {
    val programs: MutableList<SymbolicProgramContext>
    val atlas: MutableMap<BackedInteger, SymbolicProgram>
    val variables: VmMap<ExecutionVariable, Expression>
    val path: LayeredVmStack<SymbolicPathEntry>
    val transfers: VmStack<SymbolicTransfer>
    var instructionsExecuted: Int
    var exited: Boolean = false
    var exitedReason: ProgramException.Reason? = null
    var id: Int

    constructor(vararg programContexts: SymbolicProgramContext, startTime: Long = System.currentTimeMillis()) {
        id = nextId.getAndIncrement()
        programs = Stack()
        atlas = HashMap()
        path = LayeredVmStack()
        variables = LayeredVmMap()
        variables[ExecutionVariable.START_TIME] = Expression.Value(startTime.toBackedInteger())
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
        this.transfers = LayeredVmStack()
    }

    private constructor(programs: MutableList<SymbolicProgramContext>,
                        atlas: MutableMap<BackedInteger, SymbolicProgram>,
                        path: LayeredVmStack<SymbolicPathEntry>,
                        variables: VmMap<ExecutionVariable, Expression>,
                        transfers: VmStack<SymbolicTransfer>,
                        instructionsExecuted: Int) {
        this.id = nextId.getAndIncrement()
        this.programs = programs
        this.atlas = atlas
        this.path = path
        this.variables = variables
        this.transfers = transfers
        this.instructionsExecuted = instructionsExecuted
    }

    fun conditions(): MutableList<Expression> {
        val expressions: MutableList<Expression> = arrayListOf()
        path.asSequence()
                .map { if (it.taken) it.condition else Expression.Not(it.condition) }
                .forEach { expression -> expressions.add(expression) }
        return expressions
    }

    fun condition(): Expression {
        return Expression.constructAnd(conditions())
    }

    override fun close() {
        programs.forEach { it.close() }
        atlas.values.forEach { it.close() }
        variables.close()
        path.close()
        transfers.close()
    }

    override fun copy(): SymbolicVirtualMachine {
        val branchPrograms = programs.map { program -> program.copy() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.copy() }.toMutableMap()
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, path.copy(), variables.copy(), transfers.copy(), instructionsExecuted)
    }

    companion object {
        private val nextId = AtomicInteger(0)
    }
}
