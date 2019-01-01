package com.hileco.cortex.analysis.edges

class EdgeFlow(val type: EdgeFlowType,
               val source: Int? = null,
               val target: Int? = null) : Edge {
    override fun toString(): String {
        return "$type ${source ?: "START"} --> ${target ?: "END"}"
    }
}
