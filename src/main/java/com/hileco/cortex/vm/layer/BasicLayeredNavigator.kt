package com.hileco.cortex.vm.layer

class BasicLayeredNavigator<T : BasicLayered<T>>(private var layer: T) {
    fun root(): BasicLayeredNavigator<T> {
        var currentParent: T? = layer.parent()
        while(currentParent != null) {
            layer = currentParent
            currentParent = layer.parent()
        }
        return this
    }
}