== Pathing Rules

Instead of iterating over paths as in PathStream, explore starting from a given state.

Such exploration should have the following aspects:
- Allow multiple states to exist at the same time.
- Allow branching using Layered* classes, for minimal overhead.
- Keep track of which paths were dead ends.
- Keep track of constraints on paths.

== Potential Layered* Optimizations

- Layered* must keep track of their immediate child layers, using WeakReference
- Layered* can be marked as deleted, removing the parent's child reference, merging when only one child remains
- Layered* on initialization with layer-empty parent should reference the first non-layer-empty parent of the given parent
- Layered* should for performance be marked as intentionally lock-free and even non-threadsafe, and internally reference only concrete classes
