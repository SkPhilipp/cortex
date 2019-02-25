package com.hileco.cortex.database

import com.hileco.cortex.vm.concrete.Program
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.math.BigInteger


class ProgramRepository(collection: MongoCollection<Document>) : Repository<Program, BigInteger>(collection) {
    override fun idOf(entry: Program): BigInteger {
        return entry.address
    }

    override fun map(entry: Program): Document {
        return Document(mapOf(
                "instructions" to entry.instructions.map { "$it" },
                "address" to "${entry.address}",
                "_id" to "${entry.address}"
        ))
    }

    override fun map(document: Document): Program {
        val instructionParser = InstructionParser()
        val instructions = document["instructions"] as List<*>
        val address = document["address"] as String
        return Program(instructions.map { instructionParser.parse(it as String) }, BigInteger(address))
    }
}