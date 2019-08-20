package com.hileco.cortex.blueprint

import com.hileco.cortex.blueprint.Blueprints.*

class BlueprintGenerator(private val seed: Long, private val addressCeiling: Int = Int.MAX_VALUE) {

    private val randomContext = RandomContext(seed)

    fun generateAtlas(programCount: Int = randomContext.randomIntBetween(1, 10)): AtlasBlueprint {
        return AtlasBlueprint(
                programs = List(programCount) { generateProgram() }
        )
    }

    private val programTraitGenerator = randomContext.pick(mapOf(
            ProgramTrait.LIBRARY to 1.toDouble(),
            ProgramTrait.VULNERABLE to 1.toDouble(),
            ProgramTrait.OVERFLOW_SAFE to 1.toDouble(),
            ProgramTrait.WALLET to 1.toDouble(),
            ProgramTrait.TOKEN to 1.toDouble()
    ))

    private val programScopeGenerator = randomContext.pick(mapOf(
            ProgramScope.EMBEDDED to 1.toDouble(),
            ProgramScope.PERMANENT to 1.toDouble()
    ))

    private val programAccessibilityGenerator = randomContext.pick(mapOf(
            ProgramAccessibility.OWNER to 1.toDouble(),
            ProgramAccessibility.PUBLIC to 1.toDouble()
    ))

    fun generateProgram(functionCount: Int = randomContext.randomIntBetween(1, 10),
                        variableCount: Int = randomContext.randomIntBetween(1, 10),
                        traitCount: Int = randomContext.randomIntBetween(1, 10)): ProgramBlueprint {
        return ProgramBlueprint(
                functions = List(functionCount) { generateFunction() },
                variables = List(variableCount) { generateVariable() },
                traits = List(traitCount) { programTraitGenerator() },
                scope = programScopeGenerator(),
                accessibility = programAccessibilityGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling),
                owner = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val functionAccessibilityGenerator = randomContext.pick(mapOf(
            FunctionAccessibility.OWNER to 1.toDouble(),
            FunctionAccessibility.PRIVATE to 1.toDouble(),
            FunctionAccessibility.PUBLIC to 1.toDouble()
    ))

    private val functionTraitGenerator = randomContext.pick(mapOf(
            FunctionTrait.CALLABLE to 1.toDouble(),
            FunctionTrait.UTILITY to 1.toDouble(),
            FunctionTrait.TRANSFER to 1.toDouble(),
            FunctionTrait.READ to 1.toDouble(),
            FunctionTrait.WRITE to 1.toDouble(),
            FunctionTrait.BACKDOOR to 1.toDouble(),
            FunctionTrait.RECURSIVE to 1.toDouble(),
            FunctionTrait.NUMBER_GENERATOR to 1.toDouble()
    ))

    fun generateFunction(statementsCount: Int = randomContext.randomIntBetween(1, 10),
            // TODO: Function-level variable generator with its own allocation / chance table
                         variablesCount: Int = randomContext.randomIntBetween(1, 10),
                         traitsCount: Int = randomContext.randomIntBetween(1, 10)): FunctionBlueprint {
        return FunctionBlueprint(
                statements = List(statementsCount) { generateStatement() },
                variables = List(variablesCount) { generateVariable() },
                traits = List(traitsCount) { functionTraitGenerator() },
                accessibility = functionAccessibilityGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val variableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 3.toDouble(),
            VariableKind.ADDRESS_MAPPING to 1.toDouble(),
            VariableKind.NUMBER to 10.toDouble(),
            VariableKind.NUMBER_MAPPING to 1.toDouble()
    ))

    private val variableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 10.toDouble(),
            VariableResidence.DISK to 4.toDouble(),
            VariableResidence.STACK to 20.toDouble(),
            VariableResidence.NONE to 1.toDouble()
    ))

    fun generateVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = variableKindGenerator(),
                residence = variableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val statementKindGenerator = randomContext.pick(mapOf(
            StatementKind.LOOP to 10.toDouble(),
            StatementKind.CONDITIONAL to 10.toDouble(),
            StatementKind.VARIABLE_UPDATE to 10.toDouble(),
            StatementKind.INVOKE_FUNCTION to 10.toDouble(),
            StatementKind.INVOKE_PROGRAM to 10.toDouble(),
            StatementKind.NATIVE_INSTRUCTION to 10.toDouble()
    ))

    fun generateStatement(statementsCount: Int = randomContext.randomIntBetween(0, 3),
            // TODO: Statement-level variable generator with its own allocation / chance table
                          variableCount: Int = randomContext.randomIntBetween(1, 10)): StatementBlueprint {
        return StatementBlueprint(
                statements = List(statementsCount) { generateStatement() },
                variables = List(variableCount) { generateVariable() },
                kind = statementKindGenerator()
        )
    }
}