== Pathing Rules

Instead of iterating over paths as in PathStream, explore starting from a given state.

Such exploration should have the following aspects:
- Allow multiple states to exist at the same time.
- Allow branching using Layered* classes, for minimal overhead.
- Keep track of which paths were dead ends.
- Keep track of constraints on paths.
