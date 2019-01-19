package com.hileco.cortex.database

import com.hileco.cortex.io.serialization.InstructionParser
import com.hileco.cortex.vm.Program
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import java.math.BigInteger


class ProgramRepository(private val collection: MongoCollection<Document>) {
    fun save(program: Program) {
        collection.updateOne(Filters.eq("_id", "${program.address}"), Document("\$set", map(program)), UpdateOptions().upsert(true))
    }

    fun delete(program: Program) {
        collection.deleteOne(Document(mapOf("_id" to "${program.address}")))
    }

    fun findOne(address: BigInteger): Program? {
        return collection.find(Filters.eq("_id", "$address")).asSequence().map { map(it) }.firstOrNull()
    }

    fun findAll(): Sequence<Program> {
        return collection.find().asSequence().map { map(it) }
    }

    private fun map(program: Program): Document {
        return Document(mapOf(
                "instructions" to program.instructions.map { "$it" },
                "address" to "${program.address}",
                "_id" to "${program.address}"
        ))
    }

    private fun map(document: Document): Program {
        val instructionParser = InstructionParser()
        val instructions = document["instructions"] as List<*>
        val address = document["address"] as String
        return Program(instructions.map { instructionParser.parse(it as String) }, BigInteger(address))
    }
}