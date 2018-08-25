package com.hileco.cortex.output;

public class Color {

    private static final char PREFIX = 27;

    public static String fg(Palette palette, String... outputs) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append("[")
                .append(palette.foreground)
                .append("m");
        for (var output : outputs) {
            stringBuilder.append(PREFIX);
            stringBuilder.append("[")
                    .append(palette.foreground)
                    .append("m")
                    .append(output);
        }
        return stringBuilder.toString();
    }

    public static String bg(Palette palette, String... outputs) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append("[")
                .append(palette.background)
                .append("m");
        for (var output : outputs) {
            stringBuilder.append(PREFIX);
            stringBuilder.append("[")
                    .append(palette.background)
                    .append("m")
                    .append(output);
        }
        return stringBuilder.toString();
    }

    public enum Palette {

        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        MAGENTA(35),
        CYAN(36),
        WHITE(37),
        DEFAULT(39);

        private final int foreground;
        private final int background;
        Palette(int code) {
            this.foreground = code;
            this.background = code + 10;
        }
    }

}
