package com.hileco.cortex.vm.layer.test

import com.hileco.cortex.vm.layer.TreeLayered

class BasicTreeLayered(private val empty: Boolean, parent: BasicTreeLayered? = null) : TreeLayered<BasicTreeLayered>(parent) {
    override fun extractParentLayer(parent: BasicTreeLayered?): BasicTreeLayered {
        return BasicTreeLayered(empty, parent)
    }

    override fun isLayerEmpty(): Boolean {
        return empty
    }

    override fun createSibling(): BasicTreeLayered {
        return BasicTreeLayered(empty, parent())
    }

    override fun mergeParent() {
    }
}
