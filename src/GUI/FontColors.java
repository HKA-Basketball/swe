package GUI;

enum FontTypes {
    NONE(0),
    BOLD(1),
    UNDERLINED(4),
    BACKGROUND(0);

    private final int code;

    FontTypes(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}

public enum FontColors {

    // Reset
    RESET("Reset", ""),  // Text Reset

    // Colors
    BLACK("Black", "0"),   // BLACK
    RED("Red", "1"),    // RED
    GREEN("Green", "2"), // GREEN
    YELLOW("Yellow", "3"),// YELLOW
    BLUE("Blue", "4"),  // BLUE
    PURPLE("Purple", "5"),// PURPLE
    CYAN("Cyan", "6"),  // CYAN
    WHITE("White", "7"); // WHITE

    private static final String FORMAT = "\033[%sm";
    private static final String COLOR_CODE_BASE = "%s;%s";

    private final String name;
    private final String code;

    FontColors(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return getCode(FontTypes.NONE, false);
    }

    public String getCode(FontTypes type, boolean bright) {
        if (this.equals(RESET)) {
            return FORMAT.formatted("0");
        }

        int baseCode = bright ? 9 : 3;
        baseCode += FontTypes.BACKGROUND.equals(type) ? 1 : 0;
        String fullCode = baseCode + code;
        return FORMAT.formatted(COLOR_CODE_BASE.formatted(type, fullCode));
    }
}
