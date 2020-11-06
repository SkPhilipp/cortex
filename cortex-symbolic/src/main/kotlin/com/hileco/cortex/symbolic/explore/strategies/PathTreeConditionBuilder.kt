package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.collections.BranchedStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry

typealias Path = BranchedStack<SymbolicPathEntry>

class PathTreeConditionBuilder {

    private fun groupTree(paths: List<Path>): MutableMap<Path?, MutableSet<Path>> {
        val mapping = mutableMapOf<Path?, MutableSet<Path>>()
        var current = paths
        while (current.isNotEmpty()) {
            val pathsByParent = current.groupBy { it.parent() }.mapValues { it.value.toSet() }
            pathsByParent.forEach { (parent, pathsOfParent) ->
                mapping.compute(parent) { _, existingPaths ->
                    val set = existingPaths ?: HashSet()
                    set.addAll(pathsOfParent)
                    set
                }
            }
            current = pathsByParent.keys.filterNotNull()
        }
        return mapping
    }

    /**
     * Constructs a subcondition out of the difference in new entries between the parent and the child path.
     */
    private fun buildSubcondition(parent: Path?, child: Path): Expression {
        val start = parent?.size ?: 0
        val end = child.size
        val subconditions = arrayListOf<Expression>()
        for (i in start until end) {
            val pathEntry = child[i]
            subconditions.add(if (pathEntry.taken) pathEntry.condition else Expression.Not(pathEntry.condition))
        }
        return Expression.constructAnd(subconditions)
    }

    private fun buildCondition(mappedPaths: MutableMap<Path?, MutableSet<Path>>, path: Path?): Expression {
        val pathChildren = mappedPaths[path] ?: return Expression.True
        val pathConditions = pathChildren.map { childPath ->
            Expression.constructAnd(listOf(buildSubcondition(path, childPath), buildCondition(mappedPaths, childPath)))
        }.toList()
        return Expression.constructOr(pathConditions)
    }

    fun build(paths: List<Path>): Expression {
        val mappedPaths = groupTree(paths)
        return buildCondition(mappedPaths, null)
    }
}