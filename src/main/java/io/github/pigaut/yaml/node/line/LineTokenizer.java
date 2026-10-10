package io.github.pigaut.yaml.node.line;

import org.jetbrains.annotations.*;

import java.util.*;

public class LineTokenizer {

    public record Token(String raw, TokenType type) {}

    enum TokenType {
        VALUE,
        KEY_VALUE
    }

    public static List<Token> tokenize(@NotNull String line, @NotNull LineStyle lineStyle) {
        if (line.isEmpty()) {
            return List.of();
        }

        List<Token> parts = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        char[] chars = line.toCharArray();

        boolean useCommaSeparator = lineStyle.isCommaSeparated();
        boolean foundLabel = !lineStyle.isLabeled();
        boolean foundFlag = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // Ignore escaped comma ",,"
            if (c == ',' && i + 1 < chars.length && chars[i + 1] == ',') {
                current.append(',');
                i++;
                continue;
            }

            // End current token at comma
            if (c == ',' && useCommaSeparator) {
                Token token = new Token(current.toString(), TokenType.VALUE);
                flush(token, parts, current);

                // Skip up to one trailing space after the comma. The rest is counted as next token.
                if (i + 1 < chars.length && chars[i + 1] == ' ') {
                    i++;
                }
                continue;
            }

            // End current token (label) at first space unless first token is a flag
            if (c == ' ' && !foundLabel) {
                Token token = new Token(current.toString(), TokenType.VALUE);
                flush(token, parts, current);
                foundLabel = true;
                continue;
            }

            boolean nextTokenFlag = false;
            if (c == ' ' || i == 0) {
                nextTokenFlag = isNextTokenAFlag(chars, i + 1);
                if (nextTokenFlag) {
                    foundFlag = true;
                }
            }

            // End current token at space followed by flag
            if (c == ' ' && nextTokenFlag) {
                Token token = toToken(current.toString());
                flush(token, parts, current);
                foundFlag = true;
                continue;
            }

            // End current token at empty space (Spaced line style) ignores trailing space after commas
            boolean previousTokenUnescapedComma = i >= 2 ? (chars[i - 1] == ',' && chars[i - 2] != ',') : i == 1 && (chars[i - 1] == ',');
            if (c == ' ' && !previousTokenUnescapedComma && !foundFlag && lineStyle == LineStyle.SPACED) {
                if (!current.isEmpty()) {
                    Token token = toToken(current.toString());
                    flush(token, parts, current);
                }
                continue;
            }

            // End current token at colon char (Colon line style)
            if (c == ':' && !foundFlag && lineStyle == LineStyle.COLON) {
                if (!current.isEmpty()) {
                    Token token = toToken(current.toString());
                    flush(token, parts, current);
                }
                continue;
            }

            // End current token at a math operator char (Math line style)
            if (!foundFlag && lineStyle == LineStyle.MATH) {
                int operatorLength = findMathOperatorLength(chars, i);
                if (operatorLength > 0) {
                    // Ignore a single leading space before the operator
                    if (!current.isEmpty() && current.charAt(current.length() - 1) == ' ') {
                        current.deleteCharAt(current.length() - 1);
                    }

                    if (!current.isEmpty()) {
                        Token token = toToken(current.toString());
                        flush(token, parts, current);
                    }

                    // The operator itself becomes a token
                    String operator = new String(chars, i, operatorLength);
                    parts.add(new Token(operator, TokenType.VALUE));

                    int next = i + operatorLength;
                    if (next < chars.length && chars[next] == ' ') {
                        operatorLength++;
                    }

                    // -1 because the for-loop's i++ accounts for one of them
                    i += operatorLength - 1;
                    continue;
                }
            }


            current.append(c);
        }

        // Add last remaining token
        if (!current.isEmpty()) {
            Token token = toToken(current.toString());
            flush(token, parts, current);
        }

        return parts;
    }

    /**
     * Returns the length of the operator starting at index i (2 for "->", ">=", "<=", 1 for
     * everything else), or 0 if there's no operator at this position.
     */
    private static int findMathOperatorLength(char[] chars, int i) {
        char c = chars[i];
        if (i + 1 < chars.length) {
            char next = chars[i + 1];
            if ((c == '-' && next == '>') || (c == '>' && next == '=') || (c == '<' && next == '=')) {
                return 2;
            }
        }
        return LineStyle.isMathOperator(c) ? 1 : 0;
    }

    private static void flush(@NotNull Token token, @NotNull List<Token> parts, @NotNull StringBuilder current) {
        parts.add(token);
        current.setLength(0);
    }

    private static Token toToken(String raw) {
        int splitIndex = raw.indexOf("=");
        if (splitIndex == -1) {
            return new Token(raw, TokenType.VALUE);
        }

        boolean escaped = splitIndex < raw.length() - 1 && raw.charAt(splitIndex + 1) == '=';
        if (escaped) {
            // Collapse only the "==" at splitIndex into a literal "="
            String unescaped = raw.substring(0, splitIndex) + "=" + raw.substring(splitIndex + 2);
            return new Token(unescaped, TokenType.VALUE);
        }

        return new Token(raw, TokenType.KEY_VALUE);
    }

    private static boolean isNextTokenAFlag(char[] chars, int start) {
        for (int j = start; j < chars.length; j++) {
            if (chars[j] == ' ') return false; // Found another space before an '='
            if (chars[j] == '=') {
                // Ensure it's '=' and not '=='
                boolean notEscaped = (j + 1 >= chars.length || chars[j + 1] != '=');
                return notEscaped;
            }
        }
        return false;
    }

}