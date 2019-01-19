package com.hileco.cortex.io.serialization

import com.hileco.cortex.database.Database
import com.hileco.cortex.vm.Program
import java.math.BigInteger
import java.nio.file.Path

class ProgramReferenceLoader {
    fun load(reference: String): Program {
        return if (Regex("^@\\d+").matchEntire(reference) != null) {
            val address = BigInteger(reference.substring(1))
            Database.programRepository.findOne(address) ?: throw IllegalArgumentException("No program at address $address")
        } else {
            val instructionParser = InstructionParser()
            val instructions = Path.of(reference).toFile()
                    .inputStream()
                    .reader()
                    .readLines()
                    .map { instructionParser.parse(it) }
            Program(instructions)
        }
    }
}
