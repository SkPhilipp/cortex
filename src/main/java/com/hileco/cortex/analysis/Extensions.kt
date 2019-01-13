package com.hileco.cortex.analysis

inline fun <T> Iterable<T>.forEachTwo(action: (T, T) -> Unit): Unit {
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

inline fun <T> Iterable<T>.firstTwo(predicate: (T, T) -> Boolean): Pair<T, T> {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        var first = iterator.next()
        while (iterator.hasNext()) {
            val second = iterator.next()
            if (predicate(first, second)) {
                return first to second
            }
            first = second
        }
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

inline fun <T> Iterable<T>.firstTwoOrNull(predicate: (T, T) -> Boolean): Pair<T, T>? {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        var first = iterator.next()
        while (iterator.hasNext()) {
            val second = iterator.next()
            if (predicate(first, second)) {
                return first to second
            }
            first = second
        }
    }
    return null
}