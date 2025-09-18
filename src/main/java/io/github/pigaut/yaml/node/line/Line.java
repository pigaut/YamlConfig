package io.github.pigaut.yaml.node.line;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.optional.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Line implements ConfigLine {

    private final ConfigScalar scalar;
    private final List<ConfigScalar> values = new ArrayList<>();
    private final Map<String, ConfigScalar> valuesByKey = new LinkedHashMap<>();

    public Line(ConfigScalar scalar) throws InvalidConfigurationException {
        this.scalar = scalar;
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
    public @Nullable String getPath() {
        return scalar.getPath();
    }

    @Override
    public @Nullable String getSimplePath() {
        return scalar.getSimplePath();
    }

    @Override
    public void clear() {
        scalar.clear();
    }

    @Override
    public <T> ConfigOptional<T> load(Class<T> classType) {
        return scalar.load(classType);
    }

    @Override
    public <T> void map(T value) {
        scalar.map(value);
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
    public @NotNull ConfigScalar asScalar() {
        return scalar;
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
    public <T> T getRequired(int index, Class<T> classType) throws InvalidConfigurationException {
        return get(index, classType).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> get(int index, Class<T> classType) {
        return getScalar(index).flatMap(scalar -> scalar.load(classType));
    }

    @Override
    public <T> T getRequired(String key, Class<T> classType) throws InvalidConfigurationException {
        return get(key, classType).orThrow();
    }

    @Override
    public <T> List<T> getAll(Class<T> classType) throws InvalidConfigurationException {
        return getAll(0, classType);
    }

    @Override
    public <T> List<T> getAllOrSkip(Class<T> classType) {
        return getAllOrSkip(0, classType);
    }

    @Override
    public <T> List<T> getAll(int startIndex, Class<T> classType) throws InvalidConfigurationException {
        final List<T> elements = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            final ConfigScalar scalar = values.get(i);
            elements.add(scalar.load(classType).orThrow());
        }
        return elements;
    }

    @Override
    public <T> List<T> getAllOrSkip(int startIndex, Class<T> classType) {
        final List<T> elements = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            final ConfigScalar scalar = values.get(i);
            scalar.load(classType).ifPresent(elements::add);
        }
        return elements;
    }

    @Override
    public <T> ConfigOptional<T> get(String key, Class<T> classType) {
        return getScalar(key).flatMap(scalar -> scalar.load(classType));
    }

    private ConfigOptional<ConfigScalar> getScalar(String flag) {
        String[] aliases = flag.split("\\|");
        for (String alias : aliases) {
            if (valuesByKey.containsKey(alias)) {
                return ConfigOptional.of(valuesByKey.get(alias));
            }
        }
        return ConfigOptional.notSet(new InvalidConfigurationException(this, "Missing a value with flag: " + aliases[0]));
    }

    private ConfigOptional<ConfigScalar> getScalar(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(index);
        }

        if (index < values.size()) {
            return ConfigOptional.of(this, values.get(index));
        }

        return ConfigOptional.notSet(new InvalidConfigurationException(this, "Missing a value at position: " + (index + 1)));
    }

    @Override
    public <T> T loadRequired(Class<T> classType) {
        return scalar.loadRequired(classType);
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(@NotNull String modifier) throws InvalidConfigurationException {
        return getBoolean(modifier).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(@NotNull String modifier) throws InvalidConfigurationException {
        return getCharacter(modifier).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String modifier) throws InvalidConfigurationException {
        return getString(modifier).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String modifier, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        return getString(modifier, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(@NotNull String modifier) throws InvalidConfigurationException {
        return getInteger(modifier).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(@NotNull String modifier) throws InvalidConfigurationException {
        return getLong(modifier).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(@NotNull String modifier) throws InvalidConfigurationException {
        return getFloat(modifier).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(@NotNull String modifier) throws InvalidConfigurationException {
        return getDouble(modifier).orThrow();
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigurationException {
        return getBoolean(index).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(int index) throws InvalidConfigurationException {
        return getCharacter(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index) throws InvalidConfigurationException {
        return getString(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        return getString(index, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(int index) throws InvalidConfigurationException {
        return getInteger(index).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(int index) throws InvalidConfigurationException {
        return getLong(index).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(int index) throws InvalidConfigurationException {
        return getFloat(index).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(int index) throws InvalidConfigurationException {
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

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inBrackets = false;

        for (char c : line.toCharArray()) {
            if (c == '<') inBrackets = true;
            if (c == '>') inBrackets = false;

            if ((c == ' ' || c == ',') && !inBrackets) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }
            else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    public void updateLine(String line) {
        values.clear();
        valuesByKey.clear();

        for (String token : tokenize(line)) {
            if (token.startsWith("<") && token.endsWith(">")) {
                Object value = ParseUtil.parseAsScalar(token.substring(1, token.length() - 1));
                values.add(new KeylessLineScalar(this, values.size(), value));
                continue;
            }

            if (token.contains("=")) {
                String[] keyValuePair = token.split("=", 2);
                String key = keyValuePair[0];
                Object value = ParseUtil.parseAsScalar(keyValuePair[1]);
                valuesByKey.put(key, new KeyedLineScalar(this, key, value));
                continue;
            }

            Object value = ParseUtil.parseAsScalar(token);
            values.add(new KeylessLineScalar(this, values.size(), value));
        }
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(" ");

        for (Object value : values) {
            final String string = value.toString();
            if (string.contains(" ")) {
                joiner.add("<" + string + ">");
            }
            else {
                joiner.add(string);
            }
        }

        for (Map.Entry<String, ConfigScalar> parameter : valuesByKey.entrySet()) {
            joiner.add(parameter.getKey() + "=" + parameter.getValue().toString());
        }

        return joiner.toString();
    }

    @NotNull
    @Override
    public Iterator<ConfigScalar> iterator() {
        return values.iterator();
    }

}
