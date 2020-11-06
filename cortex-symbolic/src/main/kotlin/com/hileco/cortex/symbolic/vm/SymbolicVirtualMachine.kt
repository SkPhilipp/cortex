package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.*
import com.hileco.cortex.symbolic.ProgramException
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.instructions.stack.ExecutionVariable
import java.util.*

class SymbolicVirtualMachine : BranchedComposite<SymbolicVirtualMachine> {
    val programs: MutableList<SymbolicProgramContext>
    val atlas: MutableMap<BackedInteger, SymbolicProgram>
    val variables: BranchedMap<ExecutionVariable, Expression>
    val path: BranchedStack<SymbolicPathEntry>
    val transfers: BranchedStack<SymbolicTransfer>
    var instructionsExecuted: Int
    var exited: Boolean = false
    var exitedReason: ProgramException.Reason? = null

    constructor(vararg programContexts: SymbolicProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = Stack()
        atlas = HashMap()
        path = BranchedStack()
        variables = BranchedMap()
        variables[ExecutionVariable.START_TIME] = Expression.Value(startTime.toBackedInteger())
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
        this.transfers = BranchedStack()
    }

    private constructor(programs: MutableList<SymbolicProgramContext>,
                        atlas: MutableMap<BackedInteger, SymbolicProgram>,
                        path: BranchedStack<SymbolicPathEntry>,
                        variables: BranchedMap<ExecutionVariable, Expression>,
                        transfers: BranchedStack<SymbolicTransfer>,
                        instructionsExecuted: Int) {
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
}
