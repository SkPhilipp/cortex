package com.hileco.cortex.blueprint

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
    fun generateVariable() {
        val blueprintGenerator = BlueprintGenerator(1)
        blueprintGenerator.generateVariable()
    }

    @Test(timeout = 1000)
    fun generateStatement() {
        val blueprintGenerator = BlueprintGenerator(2)
        blueprintGenerator.generateStatement()
    }
}