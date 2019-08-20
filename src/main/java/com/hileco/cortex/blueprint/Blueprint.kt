package com.hileco.cortex.blueprint


class Blueprint {

    enum class VariableKind {
        NUMBER,
        ADDRESS,
        NUMBER_MAPPING,
        ADDRESS_MAPPING
    }

    enum class VariableResidence {
        MEMORY,
        DISK,
        STACK,
        CALL_DATA,
        NONE
    }

    data class VariableBlueprint(
            val kind: VariableKind,
            val residence: VariableResidence,
            val address: Int
    )

    enum class StatementKind {
        LOOP,
        CONDITIONAL,
        VARIABLE_UPDATE,
        INVOKE_FUNCTION,
        INVOKE_PROGRAM,
        NATIVE_INSTRUCTION,
        CREATE_EMBEDDED_PROGRAM
    }

    data class StatementBlueprint(
            val statements: List<StatementBlueprint>,
            val variables: List<VariableBlueprint>,
            val kind: StatementKind
    )

    enum class FunctionTrait {
        CALLABLE,
        UTILITY,
        TRANSFER,
        READ,
        WRITE,
        BACKDOOR,
        RECURSIVE,
        NUMBER_GENERATOR
    }

    enum class FunctionAccessibility {
        PRIVATE,
        PUBLIC,
        OWNER
    }

    data class FunctionBlueprint(
            val statements: List<StatementBlueprint>,
            val variables: List<VariableBlueprint>,
            val traits: List<FunctionTrait>,
            val accessibility: FunctionAccessibility,
            val address: Int
    )

    enum class ProgramTrait {
        LIBRARY,
        VULNERABLE,
        OVERFLOW_SAFE,
        WALLET,
        TOKEN
    }

    enum class ProgramScope {
        EMBEDDED,
        PERMANENT
    }

    enum class ProgramAccessibility {
        OWNER,
        PUBLIC
    }

    data class ProgramBlueprint(
            val functions: List<FunctionBlueprint>,
            val variables: List<VariableBlueprint>,
            val traits: List<ProgramTrait>,
            val scope: ProgramScope,
            val accessibility: ProgramAccessibility,
            val address: Int,
            val owner: Int
    )

    data class AtlasBlueprint(
            val programs: List<ProgramBlueprint>
    )
}
