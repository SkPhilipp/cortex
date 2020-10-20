package com.hileco.cortex.processing.web3rpc.parallelism

class ResumeCounter(initialValue: Int) {
    private val monitor = Object()
    private val idsCompleted = mutableListOf<Int>()
    private var resumeFrom = initialValue

    fun value(): Int {
        return resumeFrom
    }

    fun complete(index: Int) {
        synchronized(monitor) {
            idsCompleted.add(index)
            idsCompleted.removeIf { it < resumeFrom }
            val highestValue = idsCompleted.max() ?: index
            val nextResumeFrom = IntRange(resumeFrom, highestValue).firstOrNull { !idsCompleted.contains(it) } ?: highestValue + 1
            resumeFrom = nextResumeFrom
        }
    }
}
