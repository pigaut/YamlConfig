package io.github.pigaut.yaml.node.line;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.scalar.*;
import org.jetbrains.annotations.*;

import java.util.*;

public final class LineMatcher {

    private LineMatcher() {}

    public static boolean matchesFormat(@NotNull ConfigLine line, @NotNull String format) {
        List<String> tokens = tokenize(format);
        return matchSequence(line, tokens, 0, true) != -1;
    }

    /**
     * Splits a format string on whitespace, except whitespace inside a bracketed
     * group ( () / <> / [] ), which is kept as part of that single token.
     * Brackets may be nested (e.g. "[+ <ingredient>]" is one token).
     */
    private static List<String> tokenize(@NotNull String format) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        int n = format.length();

        while (i < n) {
            while (i < n && Character.isWhitespace(format.charAt(i))) {
                i++;
            }
            if (i >= n) {
                break;
            }

            int start = i;
            char c = format.charAt(i);

            if (c == '<' || c == '(' || c == '[') {
                int depth = 0;
                while (i < n) {
                    char ch = format.charAt(i);
                    if (ch == '<' || ch == '(' || ch == '[') {
                        depth++;
                    } else if (ch == '>' || ch == ')' || ch == ']') {
                        depth--;
                    }
                    i++;
                    if (depth == 0) {
                        break;
                    }
                }
            } else {
                while (i < n && !Character.isWhitespace(format.charAt(i))) {
                    i++;
                }
            }

            tokens.add(format.substring(start, i));
        }

        return tokens;
    }

    /**
     * Matches a sequence of format tokens starting at valueIdx.
     * Returns the resulting value index if every token matched, or -1 if any required token failed.
     * Flags (name=value inside <>/()) are only recognized when allowFlags is true, i.e. outside any [] group.
     */
    private static int matchSequence(@NotNull ConfigLine line, @NotNull List<String> tokens, int valueIdx, boolean allowFlags) {
        for (String token : tokens) {
            if (token.startsWith("[") && token.endsWith("]")) {
                List<String> innerTokens = tokenize(token.substring(1, token.length() - 1));
                while (true) {
                    int result = matchSequence(line, innerTokens, valueIdx, false);
                    if (result == -1) {
                        break;
                    }
                    valueIdx = result;
                }
                continue;
            }

            int result = matchToken(line, token, valueIdx, allowFlags);
            if (result == -1) {
                return -1;
            }
            valueIdx = result;
        }

        return valueIdx;
    }

    /**
     * Matches a single non-repeating token (literal, aliased literal, required <param>, optional (flag), or flag <name=value>/(name=value)).
     * Returns the next value index if matched/consumed, or -1 if it failed to match.
     */
    private static int matchToken(@NotNull ConfigLine line, @NotNull String token, int valueIdx, boolean allowFlags) {
        // Required parameter (anything) — consumes one value, no content check
        if (token.startsWith("<") && token.endsWith(">")) {
            String inner = token.substring(1, token.length() - 1);

            if (allowFlags && inner.contains("=")) {
                String flagName = inner.substring(0, inner.indexOf('='));
                return line.hasFlag(flagName) ? valueIdx : -1;
            }

            return valueIdx < line.size() ? valueIdx + 1 : -1;
        }

        // Optional value/flag description — parsed separately (by key), doesn't consume a positional value
        if (token.startsWith("(") && token.endsWith(")")) {
            return valueIdx;
        }

        if (valueIdx >= line.size()) {
            return -1;
        }
        ConfigOptional<String> foundString = line.getString(valueIdx);

        // Literal with aliases: id|alias
        if (token.contains("|")) {
            String[] aliases = token.split("\\|");
            for (String alias : aliases) {
                if (foundString.require(s -> s.equalsIgnoreCase(alias)).isValid()) {
                    return valueIdx + 1;
                }
            }
            return -1;
        }

        // Plain literal
        return foundString.require(s -> s.equalsIgnoreCase(token)).isValid() ? valueIdx + 1 : -1;
    }

}
