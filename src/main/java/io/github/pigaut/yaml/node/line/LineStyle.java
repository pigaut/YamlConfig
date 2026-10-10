package io.github.pigaut.yaml.node.line;

import org.jetbrains.annotations.*;

public enum LineStyle {

    /**
     * First word is the label. Subsequent comma-separated items form the values list.
     * Flags are parsed key-value pairs at the end.
     * <p>
     * <p><b>Format:</b> <code>label value1, value2, value3 key1=value1 key2=value2</code></p>
     *
     * <p><b>Example:</b> <code>sendMessage This is a message, with two lines delay=3s</code></p>
     * <ul>
     *   <li><b>label:</b> <code>"sendMessage"</code></li>
     *   <li><b>values:</b> <code>["This is a message", "with multiple lines"]</code></li>
     *   <li><b>flags:</b> <code>delay = 3s</code></li>
     * </ul>
     */
    LABELED(", "),

    /**
     * Comma-separated values with no leading label. One space following a comma is automatically trimmed.
     * Flags are parsed off the end.
     * <p>
     * <p><b>Format:</b> <code>value1, value2, value3 key1=val1 key2=val2</code></p>
     *
     * <p><b>Example:</b> <code>chat.permission, economy.permission, rank.permission requireAll=true</code></p>
     * <ul>
     *   <li><b>values:</b> <code>["chat.permission", "economy.permission", "rank.permission"]</code></li>
     *   <li><b>flags:</b> <code>requireAll = true</code></li>
     * </ul>
     */
    COMMA(", "),

    /**
     * Colon-separated values with no whitespace around the colon. Flags are parsed off the end.
     * <p>
     * <p><b>Format:</b> <code>value1:value2:value3 key1=val1 key2=val2</code></p>
     *
     * <p><b>Example:</b> <code>namespace:key caseStyle=CONSTANT</code></p>
     * <ul>
     *   <li><b>values:</b> <code>["namespace", "key"]</code></li>
     *   <li><b>flags:</b> <code>caseStyle = CONSTANT</code></li>
     * </ul>
     */
    COLON(":"),

    /**
     * Whitespace-separated values. A comma groups consecutive words into a single value entry
     * until the first word without a trailing comma. Flags are parsed off the end.
     * <p>
     * <p><b>Format:</b> <code>value1 value2 value3 key1=val1 key2=val2</code></p>
     *
     * <p><b>Examples:</b></p>
     * <ul>
     *   <li>
     *     <code>value1a, value1b, value1c value2 value3 key1=val1 key2=val2</code>
     *     <ul>
     *       <li><b>values:</b> <code>["value1a value1b value1c", "value2", "value3"]</code></li>
     *       <li><b>flags:</b> <code>key1 = val1</code>, <code>key2 = val2</code></li>
     *     </ul>
     *   </li>
     *   <li>
     *     <code>3 eggs every 5s enabled=true</code>
     *     <ul>
     *       <li><b>values:</b> <code>["3", "eggs", "every", "5s"]</code></li>
     *       <li><b>flags:</b> <code>enabled = true</code></li>
     *     </ul>
     *   </li>
     *   <li>
     *     <code>coal, iron, copper -> scrap_metal smeltTime=5s exp=5.0</code>
     *     <ul>
     *       <li><b>values:</b> <code>["coal iron copper", "->", "scrap_metal"]</code></li>
     *       <li><b>flags:</b> <code>smeltTime = 5s</code>, <code>exp = 5.0</code></li>
     *     </ul>
     *   </li>
     * </ul>
     */
    SPACED(" "),

    // 2 iron + 2 coal -> 1 steel smeltTime=10s exp=5.0
    MATH(" ");

    private final String delimiter;

    LineStyle(@NotNull String delimiter) {
        this.delimiter = delimiter;
    }

    public @NotNull String getDelimiter() {
        return delimiter;
    }

    public boolean isLabeled() {
        return this == LABELED;
    }

    public boolean isCommaSeparated() {
        return this == LABELED || this == COMMA;
    }

    public static boolean isMathOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '='
                || c == '>' || c == '<';
    }

}
