package io.github.pigaut.yaml.node.line;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.scalar.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;

import java.util.*;
import java.util.regex.*;

public class Line implements ConfigLine {

    private final ConfigScalar scalar;
    private final List<ConfigScalar> values = new ArrayList<>();
    private final Map<String, ConfigScalar> valuesByKey = new LinkedHashMap<>();
    private final LineStyle lineStyle;
    private final @Nullable String lineFormat;

    public Line(@NotNull ConfigScalar scalar, LineStyle lineStyle, @Nullable String lineFormat) {
        this.scalar = scalar;
        this.lineStyle = lineStyle;
        this.lineFormat = lineFormat;
        updateLine(scalar.toString());
    }

    @Override
    public @NotNull String getKey() {
        return scalar.getKey();
    }

    @Override
    public @NotNull String getKey(@NotNull CaseStyle style) {
        return scalar.getKey(style);
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
    public void replaceAll(@NotNull CharSequence target, @NotNull CharSequence replacement) {
        scalar.replaceAll(target, replacement);
    }

    @Override
    public void replaceAll(@NotNull Pattern pattern, @NotNull Map<String, String> replacements) {
        scalar.replaceAll(pattern, replacements);
    }

    @Override
    public List<CommentLine> getBlockComments() {
        return scalar.getBlockComments();
    }

    @Override
    public void setBlockComments(@Nullable List<CommentLine> blockComments) {
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
    public ConfigOptional<ConfigScalar> asScalar() {
        return ConfigOptional.of(scalar);
    }

    @Override
    public ConfigOptional<ConfigSection> asSection() {
        return scalar.asSection();
    }

    @Override
    public ConfigOptional<ConfigSequence> asSequence() {
        return scalar.asSequence();
    }

    @Override
    public boolean hasErrors() {
        return scalar.hasErrors();
    }

    @Override
    public boolean hasWarnings() {
        return scalar.hasWarnings();
    }

    @Override
    public int getErrorCount() {
        return scalar.getErrorCount();
    }

    @Override
    public int getWarningCount() {
        return scalar.getWarningCount();
    }

    @Override
    public @NotNull List<ConfigException> getErrors() {
        return scalar.getErrors();
    }

    @Override
    public @NotNull List<ConfigException> getWarnings() {
        return scalar.getWarnings();
    }

    @Override
    public void collectError(@NotNull ConfigException error) {
        scalar.collectError(error);
    }

    @Override
    public void collectWarning(@NotNull ConfigException warning) {
        scalar.collectWarning(warning);
    }

    @Override
    public void collectAll(@NotNull ErrorCollector other) {
        scalar.collectAll(other);
    }

    @Override
    public void clearErrors() {
        scalar.clearErrors();
    }

    @Override
    public void clearWarnings() {
        scalar.clearWarnings();
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
    public @Nullable String getFormat() {
        return lineFormat;
    }

    @Override
    public boolean matchesFormat(@NotNull String line) {
        String[] parts = line.split("\\s");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("<") && part.endsWith(">")) {
                // Required parameter (anything)
                continue;
            }

            if (part.startsWith("(") && part.endsWith(")")) {
                // Optional flag
                continue;
            }

            ConfigOptional<String> foundString = getString(i);

            if (part.contains("|")) {
                String[] aliases = part.split("\\|");
                boolean anyMatched = false;
                for (String alias : aliases) {
                    if (foundString.require(s -> s.equalsIgnoreCase(alias)).isValid()) {
                        anyMatched = true;
                        break;
                    }
                }
                if (!anyMatched) {
                    return false;
                }
                continue;
            }

            if (foundString.require(s -> s.equalsIgnoreCase(part)).isInvalid()) {
                return false;
            }
        }

        return true;
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
    public Map<String, ConfigScalar> getAllFlags() {
        return new HashMap<>(valuesByKey);
    }

    @Override
    public @NotNull ConfigScalar toScalar() {
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
        return getScalar(index).flatMapIfValid(scalar -> scalar.get(classType));
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
        return getScalar(key).flatMapIfValid(scalar -> scalar.get(classType));
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
        return getScalar(index).flatMapIfValid(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toCharacter);
    }

    @Override
    public ConfigOptional<String> getString(int index) {
        return getScalar(index).mapIfValid(ConfigScalar::toString);
    }

    @Override
    public ConfigOptional<String> getString(int index, @NotNull StringFormatter formatter) {
        return getScalar(index).mapIfValid(scalar -> scalar.toString(formatter));
    }

    @Override
    public ConfigOptional<Integer> getInteger(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toDouble);
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toCharacter);
    }

    @Override
    public ConfigOptional<String> getString(@NotNull String key) {
        return getScalar(key).mapIfValid(ConfigScalar::toString);
    }

    @Override
    public ConfigOptional<String> getString(@NotNull String key, @NotNull StringFormatter formatter) {
        return getScalar(key).mapIfValid(scalar -> scalar.toString(formatter));
    }

    @Override
    public ConfigOptional<Integer> getInteger(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(@NotNull String key) {
        return getScalar(key).flatMapIfValid(ConfigScalar::toDouble);
    }

    private static final String SPLIT_LINE = "\u001F";

    public void updateLine(@NotNull String line) {
        values.clear();
        valuesByKey.clear();

        for (LineTokenizer.Token token : LineTokenizer.tokenize(line, lineStyle)) {
            switch (token.type()) {
                case KEY_VALUE -> {
                    String raw = token.raw();

                    int separatorIndex = raw.indexOf('=');
                    String key = raw.substring(0, separatorIndex);
                    String value = raw.substring(separatorIndex + 1);

                    Object parsedValue = ScalarUtil.parseAsScalar(value);
                    ConfigScalar existingScalar = getScalar(key).orElse(null);
                    if (existingScalar != null) {
                        parsedValue = existingScalar.getValue() + SPLIT_LINE + parsedValue;
                    }

                    valuesByKey.put(key, new KeyedLineScalar(this, key, parsedValue));
                }

                case VALUE -> {
                    Object parsedValue = ScalarUtil.parseAsScalar(token.raw());
                    values.add(new KeylessLineScalar(this, values.size(), parsedValue));
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
