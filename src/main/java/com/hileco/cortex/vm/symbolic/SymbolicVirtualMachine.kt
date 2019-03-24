package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.ExecutionVariable
import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger
import java.util.*

class SymbolicVirtualMachine : DelegateLayered<SymbolicVirtualMachine> {
    val programs: MutableList<SymbolicProgramContext>
    val atlas: MutableMap<BigInteger, SymbolicProgram>
    val variables: LayeredMap<ExecutionVariable, Expression>
    val path: LayeredStack<SymbolicPathEntry>
    var instructionsExecuted: Int
    var exited: Boolean = false
    var exitedReason: ProgramException.Reason? = null

    constructor(vararg programContexts: SymbolicProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = Stack()
        atlas = HashMap()
        path = LayeredStack()
        variables = LayeredMap()
        variables[ExecutionVariable.START_TIME] = Expression.Value(startTime)
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
    }

    private constructor(programs: MutableList<SymbolicProgramContext>,
                        atlas: MutableMap<BigInteger, SymbolicProgram>,
                        path: LayeredStack<SymbolicPathEntry>,
                        variables: LayeredMap<ExecutionVariable, Expression>,
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
                .map { if (it.taken) it.condition else Expression.Not(it.condition) }
                .forEach { expression -> expressions.add(expression) }
        return Expression.constructAnd(expressions)
    }

    override fun recreateParent(): SymbolicVirtualMachine {
        val branchPrograms = programs.map { program -> program.parent() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.parent() }.toMutableMap()
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, path.parent(), variables.parent(), instructionsExecuted)
    }

    override fun branchDelegates(): SymbolicVirtualMachine {
        val branchPrograms = programs.map { program -> program.branch() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.branch() }.toMutableMap()
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, path.branch(), variables.branch(), instructionsExecuted)
    }

    override fun closeDelegates() {
        programs.forEach { program -> program.close() }
        atlas.values.forEach { symbolicProgram -> symbolicProgram.close() }
        variables.close()
        path.close()
    }
}
