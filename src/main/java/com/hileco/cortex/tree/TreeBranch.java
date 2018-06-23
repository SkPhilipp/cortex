package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TreeBranch {
    private List<TreeBranch> branches;
    private Instruction instruction;
    private Map<Class<?>, Object> metadata;

    public TreeBranch() {
        branches = new ArrayList<>();
        instruction = null;
        metadata = new HashMap<>();
    }

    public List<TreeBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<TreeBranch> branches) {
        this.branches = branches;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public boolean hasBranches() {
        return !this.branches.isEmpty();
    }

    public boolean isInstruction() {
        return instruction != null;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> key) {
        T value = (T) metadata.get(key);
        return Optional.ofNullable(value);
    }

    public <T> void put(Class<T> key, T value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "TreeBranch{" +
                "branches=" + branches +
                ", instruction=" + instruction +
                ", metadata=" + metadata +
                '}';
    }
}
