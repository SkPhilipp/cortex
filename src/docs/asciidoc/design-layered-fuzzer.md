# Function Inversion

Given an output and a function, create an inverse function which could result in multiple outputs.

These may be able to be solved using Layered Structures, if memory overhead was previously the limit.

# Layered Structures

Copyable structures with minimal memory usage.

These are implemented as a linked tree, where the end-nodes are inherently thread-unsafe and non-end-nodes are only writeable by optimization processes.

## Optimizer

Essentially a garbage-collector for Layered Structures.

### Merge-Equivalence Flatten

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

### One-Child Flatten

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

### Flatten Reporting & Tuning

The optimizer is to report on:
- Structure layer sizes (average, median)
- Amount of merges performed
- Amount of space saved per optimization operation
- Amount of layers (average, 50th percentile, 99th percentile, total)
- Amount of children per layer (average, 50th percentile, 99th percentile, total)

### Sibling-Equivalence Flatten

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

### Entry Hoisting

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

### Minimal Layer Size

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

### Preferred Depth Limit

A preferred depth limit may be configured, which when detected to be reached could be used to dynamically increment the minimum layer size.

### Bloom Filter Wrappers

It may be interesting to add a Bloom Filter in front of certain Layered Collection operations.
These could be recalculated for child nodes.

### Caches

Read values from parents could be stored in an end-node cache, this would allow for most hot variables
stored at the root to be available at the end-nodes.

## Fuzzing

### Unoptimized Copy-Structures

A minimal copy-based implementation of the Layered Structures interfaces could be useful for:
- Fuzzing single-thread Layered Structures
- Fuzzing thread safety of Layered Structures
- Fuzzing Layered Structures optimizers
- Constructing benchmarks

Fuzzing would be set up as such:
- Determine a PRNG seed
- Initialize the PRNG
- Fuzz by executing instructions on the real and the minimal implementation,
  as such that they would result in the same view of end-nodes
- Every X instructions, compare all end-nodes for equality
- When a mismatch is found:
  - Determine the failing instruction by re-running the simulation, validating end-nodes for equality from the last non-mismatch to the mismatch
  - Repeat this process using varying optimizer options as well as the exact instruction position on which a mismatch occurs
  - Log the PRNG seed, instructions executed, guilty optimizer option combinations, and mismatching nodes

