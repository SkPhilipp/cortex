package com.hileco.cortex.database

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document


abstract class Repository<T, K>(private val collection: MongoCollection<Document>) {
    protected abstract fun idOf(entry: T): K

    open fun toId(key: K): String {
        return "$key"
    }

    protected abstract fun map(entry: T): Document

    protected abstract fun map(document: Document): T

    fun save(entry: T) {
        collection.updateOne(Filters.eq("_id", toId(idOf(entry))), Document("\$set", map(entry)), UpdateOptions().upsert(true))
    }

    fun delete(entry: T) {
        collection.deleteOne(Document(mapOf("_id" to toId(idOf(entry)))))
    }

    fun findOne(id: K): T? {
        return collection.find(Filters.eq("_id", toId(id))).asSequence().map { map(it) }.firstOrNull()
    }

    fun findAll(): Sequence<T> {
        return collection.find().asSequence().map { map(it) }
    }
}