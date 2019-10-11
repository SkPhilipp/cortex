package com.hileco.cortex.data.layer

class StackLayer<T>(parent: StackLayer<T>?) : Layer<StackLayer<T>>(parent) {
    val entries: MutableMap<Int, T> = HashMap()
    var length: Int = 0

    override val isEmpty: Boolean
        get() {
            return entries.isEmpty() && parent?.length == this.length
        }

    init {
        this.length = parent?.length ?: 0
    }
}
