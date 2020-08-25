package com.hileco.cortex.collections.layer.optimize

import com.hileco.cortex.collections.layer.MapLayer

/**
 * Optimizers are not thread-safe.
 *
 * Layered values given to an optimizer must be fully exclusive to the optimizer - this goes for the complete layered structure.
 */
class LayeredVmMapOptimizer {
    /**
     * When a child overwrites all the keys in a parent, the child can instead reference its grandparent as its parent.
     */
    fun <K, V> effectiveEmptyParentFlatten(layer: MapLayer<K, V>) {
        val layerParent = layer.parent
        if (layerParent != null) {
            val edgeOverwrites = layer.deletions + layer.entries.keys
            val edgeParentOverwrites = layerParent.deletions + layerParent.entries.keys
            if (edgeOverwrites.containsAll(edgeParentOverwrites)) {
                layer.changeParent(layerParent.parent)
            }
        }
    }

    /**
     * When a parent has only one child, the child can be merged upwards.
     */
    fun <K, V> oneChildFlatten(layer: MapLayer<K, V>) {
        val layerParent = layer.parent
        if (layerParent != null && layerParent.children.size == 1) {
            layer.deletions.addAll(layerParent.deletions - layer.entries.keys)
            layer.entries.putAll(layerParent.entries - layer.deletions)
            layer.changeParent(layerParent.parent)
        }
    }

    /**
     * When a parent is equivalent to a sibling which also happens to be a parent, their children can be merged.
     */
    fun <K, V> equivalentParentMerge(layer: MapLayer<K, V>) {
        val layerParent = layer.parent ?: return
        layerParent.children.asSequence()
                .filter { it.children.isNotEmpty() }
                .filter { it !== layer }
                .filter { it.contentEquals(layer) }
                .flatMap { it.children.asSequence() }
                .forEach { it.changeParent(layer) }
    }

    /**
     * When a key of a parent is overwritten by all children, that key can be removed.
     */
    fun <K, V> overwrittenKeyRemove(layer: MapLayer<K, V>) {
        if (layer.children.isNotEmpty()) {
            val keys = layer.deletions + layer.entries.keys
            keys.asSequence()
                    .filter { key -> layer.children.all { child -> child.overwrites(key) } }
                    .forEach { key ->
                        layer.deletions.remove(key)
                        layer.entries.remove(key)
                    }
        }
    }

    /**
     * When a key of a parent is the same as its child, that key can be removed from the child.
     */
    fun <K, V> unneededChangeRemove(layer: MapLayer<K, V>) {
        val layerParent = layer.parent
        if (layerParent != null) {
            layer.entries.filter { layerParent.entries[it.key] == it.value }
                    .forEach { layer.entries.remove(it.key) }
            layer.deletions.filter { layerParent.deletions.contains(it) }
                    .forEach { layer.deletions.remove(it) }
        }
    }

    /**
     * When all children of one parent contain the same change, that change can be moved into the parent.
     */
    fun <K, V> sameChangeMerge(layer: MapLayer<K, V>) {
        layer.children.forEach { child ->
            child.deletions.filter { deletion -> layer.children.all { it.deletions.contains(deletion) } }
                    .forEach { deletion ->
                        layer.children.forEach { it.deletions.remove(deletion) }
                        layer.entries.remove(deletion)
                        layer.deletions.add(deletion)
                    }
            child.entries.filter { entry -> layer.children.all { it.entries[entry.key] == entry.value } }
                    .forEach { entry ->
                        layer.children.forEach { it.entries.remove(entry.key) }
                        layer.deletions.remove(entry.key)
                        layer.entries[entry.key] = entry.value
                    }
        }
    }
}
