package com.hileco.cortex.fuzzing.blueprint

import org.junit.Test

class BlueprintGeneratorTest {

    @Test(timeout = 1000)
    fun generateAtlas() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateAtlas()
    }

    @Test(timeout = 1000)
    fun generateProgram() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateProgram()
    }

    @Test(timeout = 1000)
    fun generateFunction() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateFunction()
    }

    @Test(timeout = 1000)
    fun generateProgramVariable() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateProgramVariable()
    }

    @Test(timeout = 1000)
    fun generateFunctionVariable() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateFunctionVariable()
    }

    @Test(timeout = 1000)
    fun generateStatementVariable() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateStatementVariable()
    }

    @Test(timeout = 1000)
    fun generateStatement() {
        val blueprintGenerator = BlueprintGenerator(2)
        blueprintGenerator.generateStatement()
    }
}