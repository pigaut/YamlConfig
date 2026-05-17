package io.github.pigaut.yaml.node.line;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.scalar.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;

import java.util.*;

public class Line implements ConfigLine {

    private final ConfigScalar scalar;
    private final List<ConfigScalar> values = new ArrayList<>();
    private final Map<String, ConfigScalar> valuesByKey = new LinkedHashMap<>();
    private final LineStyle lineStyle;

    public Line(@NotNull ConfigScalar scalar, LineStyle lineStyle) {
        this.scalar = scalar;
        this.lineStyle = lineStyle;
        updateLine(scalar.toString());
    }

    @Override
    public @NotNull String getKey() {
        return scalar.getKey();
    }

    @Override
    public boolean isRoot() {
        return scalar.isRoot();
    }

    @Override
    public @NotNull ConfigBranch getParent() throws UnsupportedOperationException {
        return scalar.getParent();
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return scalar.getFieldType();
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return scalar.getRoot();
    }

    @Override
    public @NotNull String getPath() {
        return scalar.getPath();
    }

    @Override
    public @NotNull String getSimplePath() {
        return scalar.getSimplePath();
    }

    @Override
    public void clear() {
        scalar.clear();
    }

    @Override
    public <T> ConfigOptional<T> get(@NotNull Class<T> classType) {
        return scalar.get(classType);
    }

    @Override
    public <T> void map(T value) {
        scalar.map(value);
    }

    @Override
    public List<CommentLine> getBlockComments() {
        return scalar.getBlockComments();
    }

    @Override
    public void setBlockComments(@NotNull List<CommentLine> blockComments) {
        scalar.setBlockComments(blockComments);
    }

    @Override
    public void clearBlockComments() {
        scalar.clearBlockComments();
    }

    @Override
    public void addBlockComment(@NotNull String value) {
        scalar.addBlockComment(value);
    }

    @Override
    public void addBlockBlankLine() {
        scalar.addBlockBlankLine();
    }

    @Override
    public List<CommentLine> getInLineComments() {
        return scalar.getInLineComments();
    }

    @Override
    public void setInLineComments(@NotNull List<CommentLine> inLineComments) {
        scalar.setInLineComments(inLineComments);
    }

    @Override
    public void clearInlineComments() {
        scalar.clearInlineComments();
    }

    @Override
    public void addInlineComment(@NotNull String value) {
        scalar.addInlineComment(value);
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.of(scalar);
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return scalar.toSection();
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return scalar.toSequence();
    }

    @Override
    public boolean equals(@NotNull String value) {
        return scalar.equals(value);
    }

    @Override
    public boolean equalsIgnoreCase(@NotNull String value) {
        return scalar.equalsIgnoreCase(value);
    }

    @Override
    public boolean contains(@NotNull String value) {
        return scalar.contains(value);
    }

    @Override
    public boolean hasFlag(@NotNull String key) {
        String[] aliases = key.split("\\|");
        for (String alias : aliases) {
            if (valuesByKey.containsKey(alias)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public int flagCount() {
        return valuesByKey.size();
    }

    @Override
    public @NotNull ConfigScalar asScalar() {
        return scalar;
    }

    @Override
    public @NotNull String toString(@NotNull StringFormatter formatter) {
        String string = this.toString();
        return formatter.format(string);
    }

    @Override
    public @NotNull String getValue() {
        return scalar.toString();
    }

    @Override
    public void setValue(@NotNull String line) {
        scalar.setValue(line);
    }

    @Override
    public void set(int index, Object value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        Preconditions.checkArgument(YamlConfig.isScalarType(value.getClass()), "Value is not a scalar");
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException(index);
        }
        values.set(index, new KeylessLineScalar(this, index, value));
    }

    @Override
    public void setFlag(@NotNull String key, @NotNull Object value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        Preconditions.checkArgument(YamlConfig.isScalarType(value.getClass()), "Value is not a scalar");

        String[] aliases = key.split("\\|");
        for (String alias : aliases) {
            if (valuesByKey.containsKey(alias)) {
                valuesByKey.put(alias, new KeyedLineScalar(this, alias, value));
                return;
            }
        }

        String primary = aliases[0];
        valuesByKey.put(primary, new KeyedLineScalar(this, primary, value));
    }

    @Override
    public <T> T getRequired(int index, @NotNull Class<T> classType) throws InvalidConfigException {
        return get(index, classType).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> get(int index, @NotNull Class<T> classType) {
        return getScalar(index).flatMap(scalar -> scalar.get(classType));
    }

    @Override
    public <T> T getRequired(@NotNull String key, @NotNull Class<T> classType) throws InvalidConfigException {
        return get(key, classType).orThrow();
    }

    @Override
    public <T> ConfigList<T> getAll(@NotNull Class<T> classType) {
        return getAll(0, classType);
    }

    @Override
    public <T> ConfigList<T> getAll(int startIndex, @NotNull Class<T> classType) {
        List<T> elements = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            ConfigScalar scalar = values.get(i);
            try {
                elements.add(scalar.getRequired(classType));
            } catch (InvalidConfigException e) {
                return ConfigList.invalid(e);
            }
        }
        return ConfigList.of(this, elements);
    }

    @Override
    public <T> List<T> getAllRequired(@NotNull Class<T> classType) throws InvalidConfigException {
        return getAllRequired(0, classType);
    }

    @Override
    public <T> List<T> getAllRequired(int startIndex, @NotNull Class<T> classType) throws InvalidConfigException {
        List<T> elements = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            ConfigScalar scalar = values.get(i);
            elements.add(scalar.getRequired(classType));
        }
        return elements;
    }

    @Override
    public <T> ConfigOptional<T> get(@NotNull String key, @NotNull Class<T> classType) {
        return getScalar(key).flatMap(scalar -> scalar.get(classType));
    }

    private ConfigOptional<ConfigScalar> getScalar(String flag) {
        String[] aliases = flag.split("\\|");
        for (String alias : aliases) {
            if (valuesByKey.containsKey(alias)) {
                return ConfigOptional.of(valuesByKey.get(alias));
            }
        }
        return ConfigOptional.notSet(this, "Missing a value with flag: " + aliases[0]);
    }

    private ConfigOptional<ConfigScalar> getScalar(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(index);
        }

        if (index < values.size()) {
            return ConfigOptional.of(this, values.get(index));
        }

        return ConfigOptional.notSet(this, "Missing a value at position: " + (index + 1));
    }

    @Override
    public <T> T getRequired(@NotNull Class<T> classType) throws InvalidConfigException {
        return scalar.getRequired(classType);
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(@NotNull String modifier) throws InvalidConfigException {
        return getBoolean(modifier).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(@NotNull String modifier) throws InvalidConfigException {
        return getCharacter(modifier).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String modifier) throws InvalidConfigException {
        return getString(modifier).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String modifier, @NotNull StringFormatter formatter) throws InvalidConfigException {
        return getString(modifier, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(@NotNull String modifier) throws InvalidConfigException {
        return getInteger(modifier).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(@NotNull String modifier) throws InvalidConfigException {
        return getLong(modifier).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(@NotNull String modifier) throws InvalidConfigException {
        return getFloat(modifier).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(@NotNull String modifier) throws InvalidConfigException {
        return getDouble(modifier).orThrow();
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigException {
        return getBoolean(index).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(int index) throws InvalidConfigException {
        return getCharacter(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index) throws InvalidConfigException {
        return getString(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigException {
        return getString(index, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(int index) throws InvalidConfigException {
        return getInteger(index).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(int index) throws InvalidConfigException {
        return getLong(index).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(int index) throws InvalidConfigException {
        return getFloat(index).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(int index) throws InvalidConfigException {
        return getDouble(index).orThrow();
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(int index) {
        return getScalar(index).flatMap(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(int index) {
        return getScalar(index).flatMap(ConfigScalar::toCharacter);
    }

    @Override
    public ConfigOptional<String> getString(int index) {
        return getScalar(index).map(ConfigScalar::toString);
    }

    @Override
    public ConfigOptional<String> getString(int index, @NotNull StringFormatter formatter) {
        return getScalar(index).map(scalar -> scalar.toString(formatter));
    }

    @Override
    public ConfigOptional<Integer> getInteger(int index) {
        return getScalar(index).flatMap(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(int index) {
        return getScalar(index).flatMap(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(int index) {
        return getScalar(index).flatMap(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(int index) {
        return getScalar(index).flatMap(ConfigScalar::toDouble);
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toCharacter);
    }

    @Override
    public ConfigOptional<String> getString(@NotNull String key) {
        return getScalar(key).map(ConfigScalar::toString);
    }

    @Override
    public ConfigOptional<String> getString(@NotNull String key, @NotNull StringFormatter formatter) {
        return getScalar(key).map(scalar -> scalar.toString(formatter));
    }

    @Override
    public ConfigOptional<Integer> getInteger(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(@NotNull String key) {
        return getScalar(key).flatMap(ConfigScalar::toDouble);
    }

    private record Token(String raw, TokenType type) {}

    enum TokenType {
        VALUE,
        KEY_VALUE
    }

    private List<Token> tokenize(String line) {
        if (line == null || line.isEmpty()) {
            return List.of();
        }

        List<Token> parts = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        char[] chars = line.toCharArray();

        boolean foundLabel = lineStyle != LineStyle.LABELED;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == ',' && i + 1 < chars.length && chars[i + 1] == ',') {
                current.append(',');
                i++;
                continue;
            }

            if (c == ',') {
                // Commas separate standard VALUEs
                parts.add(new Token(current.toString(), TokenType.VALUE));
                current.setLength(0);

                if (i + 1 < chars.length && chars[i + 1] == ' ') {
                    i++;
                }
                continue;
            }

            if (c == ' ' && !foundLabel) {
                String finalRaw = current.toString();
                parts.add(new Token(finalRaw, TokenType.VALUE));
                current.setLength(0);
                foundLabel = true;
                continue;
            }

            if (c == ' ' && isNextTokenAFlag(chars, i + 1)) {
                String finalRaw = current.toString();
                TokenType finalType = finalRaw.contains("=") && !finalRaw.contains("==")
                        ? TokenType.KEY_VALUE
                        : TokenType.VALUE;
                parts.add(new Token(finalRaw, finalType));
                current.setLength(0);
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            String finalRaw = current.toString();
            TokenType finalType = finalRaw.contains("=") && !finalRaw.contains("==")
                    ? TokenType.KEY_VALUE
                    : TokenType.VALUE;
            parts.add(new Token(finalRaw, finalType));
        }

        return parts;
    }

//    private static List<Token> tokenize(String line) {
//        if (line == null || line.isEmpty()) {
//            return List.of();
//        }
//
//        List<Token> parts = new ArrayList<>();
//
//        // Handle the id
//        int firstSpace = line.indexOf(' ');
//        if (firstSpace == -1) {
//            return List.of(new Token(line, TokenType.VALUE));
//        }
//        parts.add(new Token(line.substring(0, firstSpace), TokenType.VALUE));
//
//        StringBuilder current = new StringBuilder();
//        char[] chars = line.toCharArray();
//
//        for (int i = firstSpace + 1; i < chars.length; i++) {
//            char c = chars[i];
//
//            if (c == ',' && i + 1 < chars.length && chars[i + 1] == ',') {
//                current.append(',');
//                i++;
//                continue;
//            }
//
//            if (c == ',') {
//                // Commas separate standard VALUEs
//                parts.add(new Token(current.toString(), TokenType.VALUE));
//                current.setLength(0);
//
//                if (i + 1 < chars.length && chars[i + 1] == ' ') {
//                    i++;
//                }
//                continue;
//            }
//
//            if (c == ' ' && isNextTokenAFlag(chars, i + 1)) {
//                String finalRaw = current.toString();
//                TokenType finalType = finalRaw.contains("=") && !finalRaw.contains("==")
//                        ? TokenType.KEY_VALUE
//                        : TokenType.VALUE;
//                parts.add(new Token(finalRaw, finalType));
//                current.setLength(0);
//                continue;
//            }
//
//            current.append(c);
//        }
//
//        if (!current.isEmpty()) {
//            String finalRaw = current.toString();
//            TokenType finalType = finalRaw.contains("=") && !finalRaw.contains("==")
//                    ? TokenType.KEY_VALUE
//                    : TokenType.VALUE;
//            parts.add(new Token(finalRaw, finalType));
//        }
//
//        return parts;
//    }

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

    public void updateLine(String line) {
        values.clear();
        valuesByKey.clear();

        List<Token> tokens = tokenize(line);
        for (Token token : tokens) {
            switch (token.type()) {
                case KEY_VALUE -> {
                    String raw = token.raw();

                    int separatorIndex = raw.indexOf('=');
                    String key = raw.substring(0, separatorIndex);
                    String value = raw.substring(separatorIndex + 1);

                    Object parsedValue = ParseUtil.parseAsScalar(value);
                    valuesByKey.put(key, new KeyedLineScalar(this, key, parsedValue));
                }

                case VALUE -> {
                    Object value = ParseUtil.parseAsScalar(token.raw());
                    values.add(new KeylessLineScalar(this, values.size(), value));
                }
            }
        }
    }

    @Override
    public @NotNull String toString() {
        StringJoiner valueJoiner = new StringJoiner(", ");
        for (Object value : values) {
            valueJoiner.add(value.toString());
        }

        StringJoiner flagJoiner = new StringJoiner(" ");
        flagJoiner.add(valueJoiner.toString());

        for (Map.Entry<String, ConfigScalar> parameter : valuesByKey.entrySet()) {
            valueJoiner.add(parameter.getKey() + "=" + parameter.getValue().toString());
        }

        return flagJoiner.toString();
    }

    @NotNull
    @Override
    public Iterator<ConfigScalar> iterator() {
        return values.iterator();
    }

}
