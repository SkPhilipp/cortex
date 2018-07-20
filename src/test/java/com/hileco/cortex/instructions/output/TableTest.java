package com.hileco.cortex.instructions.output;

import org.junit.Test;

import java.util.Arrays;

public class TableTest {

    @Test
    public void test() {
        Table table = Table.builder()
                .columns(Arrays.asList(
                        Table.Column.builder().header("Column 1").foreground(Color.Palette.RED).width(8).build(),
                        Table.Column.builder().header("Column 2").foreground(Color.Palette.GREEN).width(8).build(),
                        Table.Column.builder().header("Column 3").foreground(Color.Palette.BLUE).width(10).build()
                ))
                .content(Arrays.asList(
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell"),
                        Arrays.asList("cell", "cell", "cell")
                ))
                .build();
        table.write(System.out);
    }
}
