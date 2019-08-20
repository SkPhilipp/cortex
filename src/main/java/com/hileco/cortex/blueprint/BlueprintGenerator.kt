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
                variables = List(variableCount) { generateProgramVariable() },
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
            VariableKind.ADDRESS to 3.toDouble(),
            VariableKind.ADDRESS_MAPPING to 1.toDouble(),
            VariableKind.NUMBER to 10.toDouble(),
            VariableKind.NUMBER_MAPPING to 1.toDouble()
    ))

    private val programVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 10.toDouble(),
            VariableResidence.DISK to 4.toDouble(),
            VariableResidence.STACK to 20.toDouble(),
            VariableResidence.NONE to 1.toDouble()
    ))

    fun generateProgramVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = programVariableKindGenerator(),
                residence = programVariableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val functionVariableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 3.toDouble(),
            VariableKind.ADDRESS_MAPPING to 1.toDouble(),
            VariableKind.NUMBER to 10.toDouble(),
            VariableKind.NUMBER_MAPPING to 1.toDouble()
    ))

    private val functionVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 10.toDouble(),
            VariableResidence.DISK to 4.toDouble(),
            VariableResidence.STACK to 20.toDouble(),
            VariableResidence.NONE to 1.toDouble()
    ))

    fun generateFunctionVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = functionVariableKindGenerator(),
                residence = functionVariableResidenceGenerator(),
                address = randomContext.randomIntBetween(0, addressCeiling)
        )
    }

    private val statementVariableKindGenerator = randomContext.pick(mapOf(
            VariableKind.ADDRESS to 3.toDouble(),
            VariableKind.ADDRESS_MAPPING to 1.toDouble(),
            VariableKind.NUMBER to 10.toDouble(),
            VariableKind.NUMBER_MAPPING to 1.toDouble()
    ))

    private val statementVariableResidenceGenerator = randomContext.pick(mapOf(
            VariableResidence.MEMORY to 10.toDouble(),
            VariableResidence.DISK to 4.toDouble(),
            VariableResidence.STACK to 20.toDouble(),
            VariableResidence.NONE to 1.toDouble()
    ))

    fun generateStatementVariable(): VariableBlueprint {
        return VariableBlueprint(
                kind = statementVariableKindGenerator(),
                residence = statementVariableResidenceGenerator(),
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
                          variableCount: Int = randomContext.randomIntBetween(1, 10)): StatementBlueprint {
        return StatementBlueprint(
                statements = List(statementsCount) { generateStatement() },
                variables = List(variableCount) { generateStatementVariable() },
                kind = statementKindGenerator()
        )
    }
}