package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.symbolic.explore.StrongReference
import com.hileco.cortex.collections.StackLayer
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry

typealias Path = StackLayer<SymbolicPathEntry>

class PathTreeConditionBuilder {
    /**
     * Maps out given paths by mapping each [paths]' relation to its parent, to its respective parent, and so on.
     */
    private fun map(paths: List<Path>): MutableMap<StrongReference<Path>, MutableSet<StrongReference<Path>>> {
        val mapping: MutableMap<StrongReference<Path>, MutableSet<StrongReference<Path>>> = hashMapOf()

        /**
         * Merges new parent-child path relations into the mapping.
         */
        fun merge(parent: StrongReference<Path>, children: List<Path>) {
            mapping.compute(parent) { _, existingPaths ->
                val set = existingPaths ?: HashSet()
                set.addAll(children.map { StrongReference(it) })
                set
            }
        }

        var pathsBySource: Map<StrongReference<Path>, List<Path>> = paths.groupBy { StrongReference(it.parent) }
        while (pathsBySource.isNotEmpty()) {
            pathsBySource.forEach { (parent, child) -> merge(parent, child) }
            pathsBySource = pathsBySource.keys.asSequence()
                    .map { it.referent }
                    .filterNotNull()
                    .groupBy { StrongReference(it.parent) }
        }
        return mapping
    }

    /**
     * Constructs a subcondition out of the difference in new entries between the parent and the child path.
     */
    private fun buildSubcondition(parent: Path?, child: Path): Expression {
        val start = parent?.length ?: 0
        val end = child.length
        val subconditions = arrayListOf<Expression>()
        for (i in start until end) {
            val pathEntry = child.entries[i] ?: throw NullPointerException()
            subconditions.add(if (pathEntry.taken) pathEntry.condition else Expression.Not(pathEntry.condition))
        }
        return Expression.constructAnd(subconditions)
    }

    private fun buildCondition(mappedPaths: MutableMap<StrongReference<Path>, MutableSet<StrongReference<Path>>>, path: Path?): Expression {
        val pathChildren = mappedPaths[StrongReference(path)] ?: return Expression.True
        val pathConditions = pathChildren.map { childPath ->
            Expression.constructAnd(listOf(buildSubcondition(path, childPath.referent!!), buildCondition(mappedPaths, childPath.referent)))
        }.toList()
        return Expression.constructOr(pathConditions)
    }

    fun build(paths: List<Path>): Expression {
        val mappedPaths = map(paths)
        return buildCondition(mappedPaths, null)
    }
}