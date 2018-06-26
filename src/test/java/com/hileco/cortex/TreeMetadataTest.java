package com.hileco.cortex;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.TreeMetadata;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TreeMetadataTest {

    @Test
    public void test() {
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .NOOP()
                .POP()
                .build();
        boolean selfContained = TreeMetadata.isSelfContained(instructions);
        Assert.assertTrue(selfContained);
    }
}
