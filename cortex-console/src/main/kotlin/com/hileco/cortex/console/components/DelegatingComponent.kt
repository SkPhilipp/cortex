package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition


abstract class DelegatingComponent<T : Component>(var delegate: T) : Component {
    override val position: TerminalPosition
        get() {
            return delegate.position
        }

    override val bottom: Int
        get() {
            return delegate.bottom
        }

    override val right: Int
        get() {
            return delegate.right
        }
}
