package com.hileco.cortex.analysis.edges

class Flow(val type: FlowType,
           val source: Int,
           val target: Int? = null) : Edge {
    override fun toString(): String {
        return "$type FROM $source TO ${target ?: "END"}"
    }
}
