package com.hileco.cortex.instructions.output;

import lombok.Builder;
import lombok.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Value
public class Table {

    @Builder
    @Value
    public static class Column {
        private final String header;
        @Builder.Default
        private final Color.Palette foreground = Color.Palette.DEFAULT;
        @Builder.Default
        private final Color.Palette background = Color.Palette.DEFAULT;
        private final int width;
    }

    private final List<Column> columns;
    @Builder.Default
    private List<List<String>> content = new ArrayList<>();

    public void row(Object... strings) {
        content.add(Arrays
                .stream(strings)
                .map(Objects::toString)
                .collect(Collectors.toList()));
    }

    private String toLength(String string, int length) {
        char[] chars = new char[length];
        int stringLength = string.length();
        for (int i = 0; i < chars.length; i++) {
            if (i < stringLength) {
                chars[i] = string.charAt(i);
            } else {
                chars[i] = ' ';
            }
        }
        return new String(chars);
    }


    private String buildSeperator(char left, char middle, char right) {
        StringBuilder entry = new StringBuilder();
        entry.append(Color.bg(Color.Palette.DEFAULT));
        entry.append(Color.fg(Color.Palette.DEFAULT));
        entry.append(left);
        for (int i = 0; i < this.columns.size(); i++) {
            if (i > 0) {
                entry.append(middle);
            }
            Column column = this.columns.get(i);
            char[] chars = new char[column.width];
            Arrays.fill(chars, '─');
            entry.append(chars);
        }
        entry.append(right);
        return entry.toString();
    }

    private String buildHeader() {
        StringBuilder entry = new StringBuilder();
        entry.append(Color.bg(Color.Palette.DEFAULT));
        entry.append(Color.fg(Color.Palette.DEFAULT));
        entry.append("│");
        for (Column column : columns) {
            entry.append(Color.bg(column.background));
            entry.append(Color.fg(column.foreground));
            entry.append(toLength(column.header, column.width));
            entry.append(Color.bg(Color.Palette.DEFAULT));
            entry.append(Color.fg(Color.Palette.DEFAULT));
            entry.append("│");
        }
        return entry.toString();
    }

    private String buildRow(List<String> row) {
        StringBuilder entry = new StringBuilder();
        entry.append(Color.bg(Color.Palette.DEFAULT));
        entry.append(Color.fg(Color.Palette.DEFAULT));
        entry.append("│");
        for (int i = 0; i < columns.size(); i++) {
            String cell = i >= row.size() ? "" : row.get(i);
            Column column = columns.get(i);
            entry.append(Color.bg(column.background));
            entry.append(Color.fg(column.foreground));
            entry.append(toLength(cell, column.width));
            entry.append(Color.bg(Color.Palette.DEFAULT));
            entry.append(Color.fg(Color.Palette.DEFAULT));
            entry.append("│");
        }
        return entry.toString();
    }

    private List<String> toStrings() {
        final List<String> output = new ArrayList<>();
        output.add(buildSeperator('┌', '┬', '┐'));
        output.add(buildHeader());
        output.add(buildSeperator('├', '┼', '┤'));
        for (List<String> row : this.content) {
            output.add(buildRow(row));
        }
        output.add(buildSeperator('└', '┴', '┘'));
        return output;
    }

    public void write(PrintStream printStream) {
        for (String string : toStrings()) {
            printStream.println(string);
        }
        printStream.flush();
    }
}
