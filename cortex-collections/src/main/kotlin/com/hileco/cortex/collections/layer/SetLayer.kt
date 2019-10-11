package com.hileco.cortex.collections.layer

import java.util.*

class SetLayer<T>(parent: SetLayer<T>?) : Layer<SetLayer<T>>(parent) {
    val entries: MutableSet<T> = HashSet()
    val deletions: MutableSet<T> = HashSet()

    override val isEmpty: Boolean
        get() = entries.isEmpty() && deletions.isEmpty()
}
