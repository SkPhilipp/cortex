package com.hileco.cortex.fuzzing.blueprint

import com.hileco.cortex.fuzzing.blueprint.Blueprint.*

class BlueprintGenerator(private val seed: Long, private val addressCeiling: Int = Int.MAX_VALUE) {

    private val randomContext = RandomContext(seed)

    fun generateAtlas(programCount: Int = randomContext.randomIntBetween(1, 10)): AtlasBlueprint {
        return AtlasBlueprint(
                programs = List(programCount) { generateProgram() }
        )
    }

    private val programTraitGenerator = randomContext.pick(mapOf(
            ProgramTrait.LIBRARY to 1,
            ProgramTrait.VULNERABLE to 1,
            ProgramTrait.OVERFLOW_SAFE to 1,
            ProgramTrait.WALLET to 1,
            ProgramTrait.TOKEN to 1
    ))

    private val programScopeGenerator = randomContext.pick(mapOf(
            ProgramScope.EMBEDDED to 1,
            ProgramScope.PERMANENT to 16
    ))

    private val programAccessibilityGenerator = randomContext.pick(mapOf(
            ProgramAccessibility.OWNER to 1,
            ProgramAccessibility.PUBLIC to 4
    ))

    fun generateProgram(functionCount: Int = randomContext.randomIntBetween(1, 10),
                        variableCount: Int = randomContext.randomIntBetween(1, 10),
                        traitCount: Int = randomContext.randomIntBetween(1, 10)): ProgramBlueprint {
        return ProgramBlueprint(
                functions = List(functionCount) { generateFunction() },
                variables = List(variableCount) { generateProgramVariable() },
                traits = List(traitCount) { programTraitGenerator() },
                scope = programScopeGenerator(),
                accessibility = programAccessibilityGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling),
                owner = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val functionAccessibilityGenerator = randomContext.pick(mapOf(
            FunctionAccessibility.OWNER to 1,
            FunctionAccessibility.PRIVATE to 4,
            FunctionAccessibility.PUBLIC to 8
    ))

    private val functionTraitGenerator = randomContext.pick(mapOf(
            FunctionTrait.CALLABLE to 1,
            FunctionTrait.UTILITY to 1,
            FunctionTrait.TRANSFER to 2,
            FunctionTrait.READ to 4,
            FunctionTrait.WRITE to 4,
            FunctionTrait.BACKDOOR to 1,
            FunctionTrait.RECURSIVE to 1,
            FunctionTrait.NUMBER_GENERATOR to 1
    ))

    fun generateFunction(statementsCount: Int = randomContext.randomIntBetween(1, 10),
                         variablesCount: Int = randomContext.randomIntBetween(1, 10),
                         traitsCount: Int = randomContext.randomIntBetween(1, 10)): FunctionBlueprint {
        return FunctionBlueprint(
                statements = List(statementsCount) { generateStatement() },
                variables = List(variablesCount) { generateFunctionVariable() },
                traits = List(traitsCount) { functionTraitGenerator() },
                accessibility = functionAccessibilityGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val programVariableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 16,
            VariableKind.ADDRESS_MAPPING to 8,
            VariableKind.NUMBER to 16,
            VariableKind.NUMBER_MAPPING to 8
    ))

    private val programVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 32,
            VariableResidence.DISK to 64,
            VariableResidence.STACK to 2,
            VariableResidence.NONE to 16
    ))

    fun generateProgramVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = programVariableKindGenerator(),
                residence = programVariableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val functionVariableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 16,
            VariableKind.ADDRESS_MAPPING to 1,
            VariableKind.NUMBER to 32,
            VariableKind.NUMBER_MAPPING to 1
    ))

    private val functionVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 32,
            VariableResidence.DISK to 1,
            VariableResidence.STACK to 4,
            VariableResidence.NONE to 8
    ))

    fun generateFunctionVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = functionVariableKindGenerator(),
                residence = functionVariableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val statementVariableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 16,
            VariableKind.ADDRESS_MAPPING to 1,
            VariableKind.NUMBER to 64,
            VariableKind.NUMBER_MAPPING to 1
    ))

    private val statementVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 2,
            VariableResidence.STACK to 8,
            VariableResidence.NONE to 4
    ))

    fun generateStatementVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = statementVariableKindGenerator(),
                residence = statementVariableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val statementKindGenerator = randomContext.pick(mapOf(
            StatementKind.LOOP to 1600,
            StatementKind.CONDITIONAL to 3200,
            StatementKind.VARIABLE_UPDATE to 12800,
            StatementKind.INVOKE_FUNCTION to 6400,
            StatementKind.INVOKE_PROGRAM to 100,
            StatementKind.NATIVE_INSTRUCTION to 400,
            StatementKind.CREATE_EMBEDDED_PROGRAM to 1
    ))

    fun generateStatement(statementsCount: Int = randomContext.randomIntBetween(0, 3),
                          variableCount: Int = randomContext.randomIntBetween(1, 10)): StatementBlueprint {
        return StatementBlueprint(
                statements = List(statementsCount) { generateStatement() },
                variables = List(variableCount) { generateStatementVariable() },
                kind = statementKindGenerator()
        )
    }
}