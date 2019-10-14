# Layered

## Optimizations

- Implementation:
    - Potentially apply bloom filters in front of Layered Sets and Maps
    - Determine optimal initial List size for LayerData per use-case
- Merge modes:
    - Layer "Upwards": A layer merges with its parent as such that it could reference it's grandparent as its parent instead
    - Layer "Downwards": A layer merges with all its children as such that they could reference it's parent as their parent instead
    - Entry "Upwards": A layer merges a single entry with its parent as such that the entry does not need to remain in the current layer
    - Entry "Downwards": A layer merges a single entry with all its children as such that the entry does not need to remain in the current layer
- Merges:
    - When an Entry is accessed often, it can be merged Downards
- Configurations:
    - Minimum layer size (hard) which would merge layers on creation unless the parent is of a certain size
    - Maximum depth (soft) which when reached could increase the minimum layer size
