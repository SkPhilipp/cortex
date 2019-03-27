package com.hileco.cortex.analysis.explore

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.layer.LayeredStack
import com.hileco.cortex.vm.symbolic.SymbolicPathEntry

typealias Path = LayeredStack<SymbolicPathEntry>

class ExploreConditionBuilder {

    /**
     * Maps out given paths by mapping each [paths]' relation to its parent, to its respective parent, and so on.
     */
    private fun map(paths: List<Path>): MutableMap<StrongReference<Path>, List<Path>> {
        val mapping: MutableMap<StrongReference<Path>, List<Path>> = hashMapOf()

        /**
         * Merges new parent-child path relations into the mapping.
         */
        fun merge(parent: StrongReference<Path>, children: List<Path>) {
            mapping.compute(parent) { _, existingPaths ->
                val combinedpaths = arrayListOf<Path>()
                combinedpaths.addAll(children)
                if (existingPaths != null) {
                    combinedpaths.addAll(existingPaths)
                }
                combinedpaths
            }
        }

        var pathsBySource: Map<StrongReference<Path>, List<Path>> = paths.groupBy { StrongReference(it.parent()) }
        while (pathsBySource.isNotEmpty()) {
            pathsBySource.forEach { parent, child -> merge(parent, child) }
            pathsBySource = pathsBySource.keys.asSequence()
                    .map { it.referent }
                    .filterNotNull()
                    .groupBy { StrongReference(it.parent()) }
        }
        return mapping
    }

    /**
     * Constructs a subcondition out of the difference in new entries between the parent and the child path.
     */
    private fun buildSubcondition(parent: Path?, child: Path): Expression {
        val start = parent?.size() ?: 0
        val end = child.size()
        val subconditions = arrayListOf<Expression>()
        for (i in start until end) {
            val pathEntry = child[i]
            subconditions.add(if (pathEntry.taken) pathEntry.condition else Expression.Not(pathEntry.condition))
        }
        return Expression.constructAnd(subconditions)
    }

    // TODO:
    // what if there is no difference between the child and the parent node...?
    //       o
    //     /    \
    // WIN        x == 1
    //              WIN
    // make sure to test this scenario...
    // for now, assume it doesnt matter

    private fun buildCondition(mappedPaths: MutableMap<StrongReference<Path>, List<Path>>, path: Path?): Expression {
        val pathChildren = mappedPaths[StrongReference(path)] ?: return Expression.True
        val pathConditions = pathChildren.map { childPath ->
            Expression.And(listOf(buildSubcondition(path, childPath), buildCondition(mappedPaths, childPath)))
        }.toList()
        return Expression.Or(pathConditions)
    }

    fun build(paths: List<Path>): Expression {
        val mappedPaths = map(paths)
        return buildCondition(mappedPaths, null)
    }
}