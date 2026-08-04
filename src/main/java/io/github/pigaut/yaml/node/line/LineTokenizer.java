package io.github.pigaut.yaml.node.line;

import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.List;

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

        boolean foundLabel = lineStyle != LineStyle.LABELED;
        boolean foundFlag = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // Ignore escaped comma ",,"
            if (c == ',' && i + 1 < chars.length && chars[i + 1] == ',') {
                current.append(',');
                i++;
                continue;
            }

            boolean nextTokenFlag = isNextTokenAFlag(chars, i + 1);
            if (nextTokenFlag) {
                foundFlag = true;
            }

            // End current token at comma
            if (c == ',' && lineStyle != LineStyle.SPACED) {
                Token token = new Token(current.toString(), TokenType.VALUE);
                flush(token, parts, current);

                // Skip up to one trailing space after the comma. The rest is counted as next token.
                if (i + 1 < chars.length && chars[i + 1] == ' ') {
                    i++;
                }
                continue;
            }

            // End current token (label) at first space
            if (c == ' ' && !foundLabel) {
                Token token = new Token(current.toString(), TokenType.VALUE);
                flush(token, parts, current);
                foundLabel = true;
                continue;
            }

            // End current token at space followed by flag token
            if (c == ' ' && nextTokenFlag) {
                Token token = toToken(current.toString());
                flush(token, parts, current);
                foundFlag = true;
                continue;
            }

            // End current token at empty space
            if (c == ' ' && !foundFlag && lineStyle == LineStyle.SPACED) {
                if (!current.isEmpty()) {
                    Token token = toToken(current.toString());
                    flush(token, parts, current);
                }
                continue;
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