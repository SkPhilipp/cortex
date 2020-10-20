package com.hileco.cortex.processing.web3rpc.parallelism

class ResumeCounter(initialValue: Long) {
    private val monitor = Object()
    private val idsCompleted = mutableListOf<Long>()
    private var resumeFrom = initialValue

    fun value(): Long {
        return resumeFrom
    }

    fun complete(index: Long) {
        synchronized(monitor) {
            idsCompleted.add(index)
            idsCompleted.removeIf { it < resumeFrom }
            val highestValue = idsCompleted.max() ?: index
            val nextResumeFrom = LongRange(resumeFrom, highestValue).firstOrNull { !idsCompleted.contains(it) } ?: highestValue + 1
            resumeFrom = nextResumeFrom
        }
    }
}
