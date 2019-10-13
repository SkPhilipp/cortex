package com.hileco.cortex.fuzzing.blueprint

import com.hileco.cortex.fuzzing.blueprint.Blueprint.*
import com.hileco.cortex.fuzzing.blueprint.Blueprint.StatementKind.*
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionsBuilder

@Suppress("unused")
class BlueprintWriter {
    fun writeAtlas(atlas: AtlasBlueprint) {
        // TODO: Return mapOf(addresses to com.hileco.cortex.vm.concrete.Program instances)
        //val programs: List<ProgramBlueprint>
    }

    fun writeProgram(atlas: AtlasBlueprint,
                     program: ProgramBlueprint) {
        val programBuilder = InstructionsBuilder()
        // TODO: Initialize all of the program's variables
        // TODO: Apply accessibility checks
        // TODO: Go through all of its functions and construct a function table mapping as List<Instruction>
        // TODO: Go through all of its functions with writeFunction
        // TODO: Construct a com.hileco.cortex.vm.concrete.Program instance including address and owner
    }

    fun writeFunction(atlas: AtlasBlueprint,
                      program: ProgramBlueprint,
                      function: FunctionBlueprint): List<Instruction> {
        // TODO: Construct out of the function a List<Instruction>
        // TODO: Apply accessibility checks
        // TODO: Initialize all of the function's variables
        // TODO: Go through all of its statements with writeStatement
        return listOf()
    }

    fun writeStatement(atlas: AtlasBlueprint,
                       program: ProgramBlueprint,
                       function: FunctionBlueprint,
                       statement: StatementBlueprint): List<Instruction> {
        // TODO: Construct out of the statement a List<Instruction>
        // TODO: Initialize all of the statement's variables
        when (statement.kind) {
            LOOP -> TODO()
            CONDITIONAL -> TODO()
            VARIABLE_UPDATE -> TODO()
            INVOKE_FUNCTION -> TODO()
            INVOKE_PROGRAM -> TODO()
            NATIVE_INSTRUCTION -> TODO()
            CREATE_EMBEDDED_PROGRAM -> TODO()
        }
    }

    fun writeEmbeddedProgram(atlas: AtlasBlueprint,
                             parentProgram: ProgramBlueprint,
                             function: FunctionBlueprint,
                             statement: StatementBlueprint,
                             embeddedProgram: ProgramBlueprint): List<Instruction> {
        // TODO: Initialize all of the program's variables
        // TODO: Apply accessibility checks
        // TODO: Go through all of its functions and construct a function table mapping as List<Instruction>
        // TODO: Go through all of its functions with writeFunction
        // TODO: Construct a com.hileco.cortex.vm.concrete.Program instance including address and owner
        return listOf()
    }

}