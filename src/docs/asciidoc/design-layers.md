Additions:
- Layered structures must implement `children():List<WeakReference<>>`
- LayeredNavigator(Layered<T>) is introduced, supporting:
  - `.root()` to navigate to the root of a Layered structure
  - `.filter((Layered<T>) -> boolean)` to navigate over only matching Layered structures
  - `.tree()` to retrieve the full tree structure, with predicates applied
  - `.edges()` to retrieve a list of all edges

These components will allow for the construction of optimal composite conditions, for example given a tree representation
of SymbolicVirtualMachines(Layered)'s path condition additions, with "z" representing a local root:

           z C==100
             /    \
           /        \
      y B==10      x B==20
       /     \         \
     /         \         \
  w A==1      v A==2     u A==3

(Note that a single node could have multiple conditions, in this example each node has exactly one.)

The optimal expression would be built recursively as such;

    // note that the optimization per size is likely already implemented in single-AND and single/empty-OR construction methods
    build(node) := node.children.size == 0 -> node.condition
                   node.children.size == 1 -> AND(node.condition, build(node.children.single()))
                   node.children.size >= 2 -> AND(node.condition, OR(node.children.map(build).toList()))

`build(z)` would yield `AND(C==100, OR(AND(B==10, OR(A==1, A==2)) OR(AND(B==20, A==3))))`, representing
the optimal expression `z && ( ( y && ( w || v ) ) || ( x && ( u ) ) )`.

Currently the expression is built by OR-ing the AND-ed paths to each edge node, resulting
in the suboptimal expression ( z && y && w ) || ( z && y && v ) || ( z && x && u ).

The current expression's size is depth-dependent, with its size being `depth * edges`, growing exponentially.
The optimal expression's size is equal to the amount of nodes, for the common 2-child-only trees `edges * 2 - 1`, growing linearly.

Note that SymbolicVirtualMachine's path condition should become an append-only Layered stack.

