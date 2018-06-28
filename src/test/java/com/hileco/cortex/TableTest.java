package com.hileco.cortex;

import com.hileco.cortex.output.Color;
import com.hileco.cortex.output.Table;
import org.junit.Test;

import java.util.Arrays;

public class TableTest {

    @Test
    public void test() {
        Table table = Table.builder()
                .columns(Arrays.asList(
                        Table.Column.builder().header("Blah 1").foreground(Color.Palette.RED).width(8).build(),
                        Table.Column.builder().header("Blah 2").foreground(Color.Palette.GREEN).width(8).build(),
                        Table.Column.builder().header("Total Blahs").foreground(Color.Palette.BLUE).width(10).build()
                ))
                .content(Arrays.asList(
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla"),
                        Arrays.asList("blah", "bla", "blahbla")
                ))
                .build();
        table.write(System.out);
    }
}
