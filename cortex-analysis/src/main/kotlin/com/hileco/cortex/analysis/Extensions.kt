package com.hileco.cortex.analysis

inline fun <T> Iterable<T>.forEachTwo(action: (T, T) -> Unit) {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        var first = iterator.next()
        while (iterator.hasNext()) {
            val second = iterator.next()
            action(first, second)
            first = second
        }
    }
}
