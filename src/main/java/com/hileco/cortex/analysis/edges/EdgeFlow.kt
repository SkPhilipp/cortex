package com.hileco.cortex.analysis.edges

class EdgeFlow(val type: EdgeFlowType,
               val source: Int? = null,
               val target: Int? = null) : Edge {

    override fun toString(): String {
        val sourceString = source ?: "START"
        val targetString = target ?: "END"
        return String.format("%s %s --> %s", type, sourceString, targetString)
    }

    companion object {
        val UTIL = EdgeUtility(EdgeFlow::class.java)
    }
}
