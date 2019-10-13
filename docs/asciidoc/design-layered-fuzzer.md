# Layered

## Merge-Equivalence Flatten

    0
      \
        1
      /   \
    ..     2.N
0   | {x:2 _original_ , y: 2 _original_, z: 3 _original_}
1   | {x:1 _overwrite_}
2.N | {x: _deleted_, z: _deleted_}

    0
  /   \
1       2.N
  \
   ..
 2.N | merge(1, 2.N) == {x: _deleted_, z: _deleted_}

2.N is unlinked from parent 1, as merge(1.layer, 2.N.layer) == view(2.N)
As parent 1 has a parent of its own, 0, 2.N is linked to parent 0.

As a new child-parent link is established, another flatten may be performed on 2.N, this should be evaluated until no flatten is possible.
As a child-parent link was removed, 1 should be checked for only children.

## One-Child Flatten

    0
  /   \
 ..     1
          \
           2.N
1   | any (effective)
2.N | any

        0
      /   \
     ..    2.N
2.N | merge (1, 2.N)

2.N and 1 merge as 2.N is the only remaining child of 1, 2.N now references 0 as its parent.
1 can be cleaned up.

## Sibling-Equivalence Flatten

    0
  /   \
1       2
  \       \ 
   3&4     5&6

When 1 == 2, and 1 & 2 are locked, either could be chosen as preferred parent (2), and the structure can be reorganized as such:

0
  \
    2
      \
     3&4&5&6

This would allow further optimizations upward. In this example 2 is the only child of 0, meaning that they can be flattened as such;

0
  \
 3&4&5&6

## Entry Hoisting

    0
  /   \
1       2
  \       \
   3&4     5&6

0 | {...}
1 | {..., x: 1, y: 2}
2 | {..., x: 3, y: 2}

To save space at the cost of time, when 1 and 2 are not fully equal but contain entries where 1[key] == 2[key], they can be merged upwards.

0 | {..., y: 2}
1 | {..., x: 1}
2 | {..., x: 3}

## Minimal Layer Size

    0
  /   \
 ..     1
      /   \
    2       3

0 | {...}
1 | {x: 1}
2 | {...}
3 | {...}

To save time at the cost of space, layers could be merged into their children when they are not of a given minimal layer size.

    0
  /   \
 ..     2&3

0 | {...}
1 | {...}
2 | merge(2, 1)
3 | merge(3, 1)

## Unoptimized Copy-Structures

Alternate copy-based implementation of the Layered\* for fuzzing & benchmarking;

    Variation.equals(100, variation -> {
        ...
        Assertions.assertEquals(copyBasedImpl.view(), realImpl.view());
    });

## Optimizations

- Implementation:
    - Keep optimizations separate for minimum branching
    - Potentially apply bloom filters
    - Determine proper List size for LayerData
    - Lock-free children list
    - Lock-free layer modifications
- Merge modes:
    - Layer "Upwards": A layer merges with its parent as such that it could reference it's grandparent as its parent instead
    - Layer "Downwards": A layer merges with all its children as such that they could reference it's parent as their parent instead
    - Entry "Upwards": A layer merges a single entry with its parent as such that the entry does not need to remain in the current layer
    - Entry "Downwards": A layer merges a single entry with all its children as such that the entry does not need to remain in the current layer
- Merges:
    - When a (non-edge) child overwrites all Entries in its parent, that child Layer can be merged Upwards
    - When all children (at any depth) of a parent contain the same Entry, the Entry can be merged Upwards
    - When two (non-edge) children of a parent are equivalent, children of the first can reference the second as their new parent, and the first child can be closed
    - When a parent has only one child, the child Layer can be merged Upwards
    - When a (non-edge) child contains no entries, it can be merged Upwards
    - When an Entry is accessed often, it can be merged Downards
- Configurations:
    - Minimum layer size (hard) which would merge layers on creation unless the parent is of a certain size
    - Maximum depth (soft) which when reached could increase the minimum layer size
