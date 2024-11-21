package io.github.pigaut.yamlib.config.node;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.config.path.*;
import io.github.pigaut.yamlib.configurator.*;
import io.github.pigaut.yamlib.configurator.field.*;
import io.github.pigaut.yamlib.configurator.section.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public abstract class SectionNode implements ConfigSection {

    protected final Map<String, @NotNull Object> children = new LinkedHashMap<>();
    private FlowStyle flowStyle = FlowStyle.BLOCK;
    private FlowStyle defaultFlowStyle = null;

    private <T> Stream<T> getListOrThrow(String path, Class<T> type, String error) {
        List<Object> scalars = getFieldList(path);

        for (int i = 0; i < scalars.size(); i++) {
            if (!type.isInstance(scalars.get(i))) {
                throw new InvalidConfigurationException(this, path, error);
            }
        }

        return scalars.stream().map(type::cast);
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public Set<String> getKeys() {
        return new LinkedHashSet<>(children.keySet());
    }

    @Override
    public Set<Object> getValues() {
        return new LinkedHashSet<>(children.values());
    }

    @Override
    public boolean contains(@NotNull String path) {
        return getOptionalField(path).isPresent();
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return getOptionalScalar(path).isPresent();
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return getOptionalSection(path).isPresent();
    }

    @Override
    public <T> void set(@NotNull String path, @Nullable T value) {
        if (value == null) {
            ConfigUtil.setScalar(this, path, null);
            return;
        }

        final Class<?> classType = value.getClass();
        if (YAMLib.SCALARS.contains(classType)) {
            ConfigUtil.setScalar(this, path, value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        final ConfigSetter<? super T> setter = (ConfigSetter<? super T>) configurator.getConfigSetter(classType);
        if (setter != null) {
            set(path, setter.generateValue(value));
            return;
        }

        @SuppressWarnings("unchecked")
        final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(classType);
        if (mapper != null) {
            final ConfigSection sectionToMap = getSectionOrCreate(path);
            configurator.map(sectionToMap, value);
            return;
        }

        throw new IllegalArgumentException("No setter or mapper found for value of class type: " + classType.getSimpleName());
    }

    @Override
    public <T> void add(@NotNull T value) {
        Preconditions.checkNotNull(value, "Value cannot be null");

        final Class<?> classType = value.getClass();
        if (YAMLib.SCALARS.contains(classType)) {
            ConfigUtil.addScalar(this, value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        final ConfigSetter<? super T> setter = (ConfigSetter<? super T>) configurator.getConfigSetter(classType);
        if (setter != null) {
            final Object generatedValue = setter.generateValue(value);
            String generatedKey = setter.generateKey(value);
            if (generatedKey == null) {
                generatedKey = addKey();
            }

            set(generatedKey, generatedValue);
            return;
        }

        @SuppressWarnings("unchecked")
        final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(classType);
        if (mapper != null) {
            final String generatedKey = mapper.generateKey(value);
            final ConfigSection sectionToMap = generatedKey != null ? getSectionOrCreate(generatedKey) : addSection();

            mapper.map(sectionToMap, value);
            return;
        }

        throw new IllegalArgumentException("No config setter or mapper found for class: " + classType.getSimpleName());
    }

    @Override
    public void map(@NotNull Object value) {
        getRoot().getConfigurator().map(this, value);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public void formatKeys(StringFormatter formatter) {
        Map<String, Object> children = getNestedFields();
        this.children.clear();
        children.forEach((key, value) -> this.children.put(formatter.format(key), value));
    }

    @Override
    public @NotNull FlowStyle getFlowStyle() {
        return flowStyle;
    }

    @Override
    public void setFlowStyle(@NotNull FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }

    @Override
    public @Nullable FlowStyle getDefaultFlowStyle() {
        return defaultFlowStyle;
    }

    @Override
    public void setDefaultFlowStyle(@NotNull FlowStyle flowStyle) {
        this.defaultFlowStyle = flowStyle;
        for (ConfigSection section : getNestedSections()) {
            section.setFlowStyle(flowStyle);
        }
    }

    @Override
    public <T> @NotNull T load(@NotNull Class<T> type) throws InvalidConfigurationException {
        final Configurator configurator = getRoot().getConfigurator();
        return configurator.load(type, this);
    }

    @Override
    public <T> @NotNull T load(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        final ConfigSection section = getSection(path);
        return section.load(type);
    }

    @Override
    public <T> @NotNull T get(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        return ConfigUtil.get(this, path, type);
    }

    @Override
    public <T> @NotNull T getOrLoad(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        if (getField(path) instanceof ConfigSection sectionToLoad) {
            return sectionToLoad.load(type);
        }
        return get(path, type);
    }

    @Override
    public <T> @NotNull Optional<T> loadOptional(@NotNull Class<T> type) {
        final Configurator configurator = getRoot().getConfigurator();
        return ConfigUtil.getOptional(() -> configurator.load(type, this));
    }

    @Override
    public <T> Optional<T> loadOptional(@NotNull String path, @NotNull Class<T> type) {
        return ConfigUtil.getOptional(() -> load(path, type));
    }

    @Override
    public <T> Optional<T> getOptional(@NotNull String path, @NotNull Class<T> type) {
        return ConfigUtil.getOptional(() -> get(path, type));
    }

    @Override
    public <T> Optional<T> getOrLoadOptional(@NotNull String path, @NotNull Class<T> type) throws IllegalArgumentException {
        return ConfigUtil.getOptional(() -> getOrLoad(path, type));
    }

    @Override
    public <T> Stream<T> loadAll(@NotNull Class<T> type) {
        return getNestedSections().stream().flatMap(section -> section.loadOptional(type).stream());
    }

    @Override
    public <T> Stream<T> loadAllOrThrow(@NotNull Class<T> type) throws InvalidConfigurationException {
        return getNestedSections().stream().map(section -> section.load(type));
    }

    @Override
    public <T> Stream<T> loadAll(@NotNull String path, @NotNull Class<T> type) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.loadAll(type)).orElse(Stream.empty());
    }

    @Override
    public <T> Stream<T> loadAllOrThrow(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.loadAllOrThrow(type)).orElse(Stream.empty());
    }

    @Override
    public <T> Stream<@NotNull T> getAll(@NotNull String path, @NotNull Class<T> type) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.getKeys().stream().flatMap(key -> section.getOptional(key, type).stream()))
                .orElse(Stream.empty());
    }

    @Override
    public <T> Stream<@NotNull T> getAllOrThrow(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.getKeys().stream().map(key -> section.get(key, type))).orElse(Stream.empty());
    }

    @Override
    public <T> Stream<@NotNull T> getOrLoadAll(@NotNull String path, @NotNull Class<T> type) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.getKeys().stream().flatMap(key -> section.getOrLoadOptional(key, type).stream()))
                .orElse(Stream.empty());
    }

    @Override
    public <T> Stream<@NotNull T> getOrLoadAllOrThrow(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.getKeys().stream().map(key -> section.getOrLoad(key, type))).orElse(Stream.empty());
    }

    @Override
    public @NotNull ConfigSection getSection(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getSection(this, path);
    }

    @Override
    public @NotNull SectionNode getSectionOrCreate(@NotNull String path) {
        return ConfigUtil.getSectionOrCreate(this, path, false);
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getSection(path));
    }

    @Override
    public @NotNull ConfigSection getSection(@NotNull String path, boolean keyless) throws InvalidConfigurationException {
        final ConfigSection section = getSection(path);

        if (section.isKeyless() && !keyless) {
            throw new InvalidConfigurationException(this, path, "is not a section");
        }
        else if (!section.isKeyless() && keyless) {
            throw new InvalidConfigurationException(this, path, "is not a list");
        }

        return section;
    }

    @Override
    public @NotNull ConfigSection getSectionOrCreate(@NotNull String path, boolean keyless) {
        return ConfigUtil.getSectionOrCreate(this, path, keyless);
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(@NotNull String path, boolean keyless) {
        return ConfigUtil.getOptional(() -> getSection(path, keyless));
    }

    @Override
    public Map<String, Object> getNestedFields() {
        return new LinkedHashMap<>(children);
    }

    @Override
    public Map<String, Object> getNestedScalars() {
        Map<String, Object> scalars = new LinkedHashMap<>();

        children.forEach((key, value) -> {
            if (!(value instanceof ConfigSection)) {
                scalars.put(key, value);
            }
        });

        return scalars;
    }

    @Override
    public Set<ConfigSection> getNestedSections() {
        Set<ConfigSection> sections = new LinkedHashSet<>();

        children.forEach((key, value) -> {
            if (value instanceof ConfigSection section) {
                sections.add(section);
            }
        });

        return sections;
    }


    @Override
    public Map<String, Object> getNestedFields(@NotNull String path) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(ConfigSection::getNestedFields).orElse(Map.of());
    }

    @Override
    public Map<String, Object> getNestedScalars(@NotNull String path) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(ConfigSection::getNestedScalars).orElse(Map.of());
    }

    @Override
    public Set<ConfigSection> getNestedSections(@NotNull String path) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(ConfigSection::getNestedSections).orElse(Set.of());
    }

    @Override
    public List<Object> getFieldList(@NotNull String path) {
        ConfigSection section = getOptionalSection(path).orElse(null);

        if (section != null) {
            if (!section.isKeyless()) {
                throw new InvalidConfigurationException(section, "is not a list");
            }
            return new ArrayList<>(section.getValues());
        }

        return List.of();
    }

    @Override
    public List<Object> getScalarList(@NotNull String path) throws InvalidConfigurationException {
        List<Object> fields = getFieldList(path);

        for (Object field : fields) {
            if (field instanceof ConfigSection) {
                throw new InvalidConfigurationException(this, path, "is not a list of scalar values");
            }
        }

        return fields;
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) throws InvalidConfigurationException {
        return getListOrThrow(path, ConfigSection.class, "is not a list of sections").toList();
    }

    @Override
    public List<Boolean> getBooleanList(@NotNull String path) {
        return getListOrThrow(path, Boolean.class, "is not a list of booleans").toList();
    }

    @Override
    public List<Character> getCharacterList(@NotNull String path) {
        return getListOrThrow(path, Character.class, "is not a list of characters").toList();
    }

    @Override
    public List<String> getStringList(@NotNull String path) {
        List<Object> fields = getFieldList(path);

        for (Object field : fields) {
            if (field instanceof ConfigSection) {
                throw new InvalidConfigurationException(this, path, "is not a list of strings");
            }
        }

        return fields.stream().map(Object::toString).toList();
    }

    @Override
    public List<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) {
        return getStringList(path).stream()
                .map(formatter::format)
                .toList();
    }

    @Override
    public List<Integer> getIntegerList(@NotNull String path) {
        return getListOrThrow(path, Integer.class, "is not a list of integers").toList();
    }

    @Override
    public List<Long> getLongList(@NotNull String path) {
        return getListOrThrow(path, Number.class, "is not a list of longs").map(Number::longValue).toList();
    }

    @Override
    public List<Float> getFloatList(@NotNull String path) {
        return getListOrThrow(path, Number.class, "is not a list of floats").map(Number::floatValue).toList();
    }

    @Override
    public List<Double> getDoubleList(@NotNull String path) {
        return getListOrThrow(path, Number.class, "is not a list of doubles").map(Number::doubleValue).toList();
    }

    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> type) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.map(section -> section.getList(type)).orElse(List.of());
    }

    @Override
    public <T> List<@NotNull T> getList(@NotNull Class<T> type) throws InvalidConfigurationException {
        if (!isKeyless()) {
            throw new InvalidConfigurationException(this, "is not a list");
        }

        if (size() == 0) {
            return List.of();
        }

        List<T> list = new ArrayList<>();
        for (String key : getKeys()) {
            list.add(getOrLoad(key, type));
        }

        return list;
    }

    public @NotNull Object getField(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getField(this, path);
    }

    @Override
    public Optional<Object> getOptionalField(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getField(path));
    }

    @Override
    public @NotNull Object getScalar(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalar(this, path);
    }

    @Override
    public Optional<Object> getOptionalScalar(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getScalar(path));
    }

    @Override
    public boolean getBoolean(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Boolean.class, "is not a boolean");
    }

    @Override
    public Optional<Boolean> getOptionalBoolean(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getBoolean(path));
    }

    @Override
    public char getCharacter(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Character.class, "is not a character");
    }

    @Override
    public Optional<Character> getOptionalCharacter(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getCharacter(path));
    }

    @Override
    public @NotNull String getString(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalar(this, path).toString();
    }

    @Override
    public Optional<String> getOptionalString(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getString(path));
    }

//    @Override
//    public Object[] getSplitString(String path, String regex, Class<?>... types) {
//        Preconditions.checkArgument(types.length > 1, "Types must have at least two elements.");
//        final int length = types.length;
//        final String[] elements = getString(path).split(regex);
//
//        if (elements.length != length) {
//            throw new InvalidConfigurationException(this, path,
//                    "(Invalid split string) expected " + types.length + " elements, but got " + elements.length);
//        }
//
//        Object[] splitString = new Object[types.length];
//
//        final Parser parser = getRoot().getParser();
//        for (int i = 0; i < types.length; i++) {
//            try {
//                splitString[i] = parser.deserialize(types[i], elements[i]);
//            } catch (DeserializationException e) {
//                throw new InvalidConfigurationException(this, path,
//                        "(Invalid split string) " + e.getMessage());
//            }
//        }
//
//        return splitString;
//    }

    @Override
    public @NotNull String getString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        return formatter.format(getString(path));
    }

    @Override
    public Optional<String> getOptionalString(@NotNull String path, @NotNull StringFormatter formatter) {
        return getOptionalString(path).map(formatter::format);
    }

    @Override
    public int getInteger(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Integer.class, "is not an integer");
    }

    @Override
    public Optional<Integer> getOptionalInteger(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getInteger(path));
    }

    @Override
    public long getLong(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Number.class, "is not a long").longValue();
    }

    @Override
    public Optional<Long> getOptionalLong(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getLong(path));
    }

    @Override
    public float getFloat(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Number.class, "is not a float").floatValue();
    }

    @Override
    public Optional<Float> getOptionalFloat(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getFloat(path));
    }

    @Override
    public double getDouble(@NotNull String path) throws InvalidConfigurationException {
        return ConfigUtil.getScalarOrThrow(this, path, Number.class, "is not a double").doubleValue();
    }

    @Override
    public Optional<Double> getOptionalDouble(@NotNull String path) {
        return ConfigUtil.getOptional(() -> getDouble(path));
    }

    @Override
    public @NotNull LocalDate getDate(@NotNull String path) throws InvalidConfigurationException {
        return get(path, LocalDate.class);
    }

    @Override
    public Optional<LocalDate> getOptionalDate(@NotNull String path) {
        return getOptional(path, LocalDate.class);
    }

    @Override
    public @NotNull LocalTime getTime(@NotNull String path) throws InvalidConfigurationException {
        return get(path, LocalTime.class);
    }

    @Override
    public Optional<LocalTime> getOptionalTime(@NotNull String path) {
        return getOptional(path, LocalTime.class);
    }

    @Override
    public @NotNull LocalDateTime getDateTime(@NotNull String path) throws InvalidConfigurationException {
        return get(path, LocalDateTime.class);
    }

    @Override
    public Optional<LocalDateTime> getOptionalDateTime(@NotNull String path) {
        return getOptional(path, LocalDateTime.class);
    }

    @Override
    public @NotNull File getFile(@NotNull String path) throws InvalidConfigurationException {
        return get(path, File.class);
    }

    @Override
    public @NotNull Locale getLocale(@NotNull String path) throws InvalidConfigurationException {
        return get(path, Locale.class);
    }

    @Override
    public Optional<Locale> getOptionalLocale(@NotNull String path) {
        return getOptional(path, Locale.class);
    }

    @Override
    public @NotNull UUID getUUID(@NotNull String path) throws InvalidConfigurationException {
        return get(path, UUID.class);
    }

    @Override
    public Optional<UUID> getOptionalUUID(@NotNull String path) {
        return getOptional(path, UUID.class);
    }

    @Override
    public @NotNull URL getURL(@NotNull String path) throws InvalidConfigurationException {
        return get(path, URL.class);
    }

    @Override
    public Optional<URL> getOptionalURL(@NotNull String path) {
        return getOptional(path, URL.class);
    }

    public Object[][] getMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Preconditions.checkArgument(rowCount > 0 && columnCount > 0, "Matrix must have at least one row and one column.");

        Object[][] matrix = new Object[rowCount][columnCount];
        ConfigSection rowSection = getSection(path);

        if (!rowSection.isKeyless()) {
            throw new InvalidConfigurationException(this, path, "(Invalid matrix) is not a list of lists");
        }

        List<ConfigSection> rowSections = new ArrayList<>(rowSection.getNestedSections());

        if (rowSections.size() != rowCount) {
            throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) expected %d rows, but got %d", rowCount, rowSections.size()));
        }

        for (int row = 0; row < rowCount; row++) {
            ConfigSection columnSection = rowSections.get(row);

            if (!columnSection.isKeyless()) {
                throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) row %d is not a list", row + 1));
            }

            List<Object> columnFields = new ArrayList<>(columnSection.getNestedScalars().values());

            if (columnFields.size() != columnCount) {
                throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) row %d must have %d columns, but got %d", row + 1, columnCount, columnFields.size()));
            }

            for (int column = 0; column < columnCount; column++) {
                matrix[row][column] = columnFields.get(column);
            }

        }

        return matrix;
    }

    @Override
    public String[][] getStringMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        String[][] stringMatrix = new String[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];
                stringMatrix[row][column] = element.toString();
            }
        }

        return stringMatrix;
    }

    @Override
    public char[][] getCharacterMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        char[][] charMatrix = new char[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Character character)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not a character", row + 1, column + 1));
                }

                charMatrix[row][column] = character;
            }
        }

        return charMatrix;
    }

    @Override
    public int[][] getIntegerMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        int[][] intMatrix = new int[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Integer intNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not an integer", row + 1, column + 1));
                }

                intMatrix[row][column] = intNum;
            }
        }

        return intMatrix;
    }

    @Override
    public boolean[][] getBooleanMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        boolean[][] booleanMatrix = new boolean[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Boolean bool)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not a boolean", row + 1, column + 1));
                }

                booleanMatrix[row][column] = bool;
            }
        }

        return booleanMatrix;
    }

    @Override
    public double[][] getDoubleMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        double[][] doubleMatrix = new double[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Double doubleNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not a double", row + 1, column + 1));
                }

                doubleMatrix[row][column] = doubleNum;
            }
        }

        return doubleMatrix;
    }

    @Override
    public long[][] getLongMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        long[][] longMatrix = new long[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Long longNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not a long", row + 1, column + 1));
                }

                longMatrix[row][column] = longNum;
            }
        }

        return longMatrix;
    }

    @Override
    public float[][] getFloatMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        float[][] floatMatrix = new float[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Float floatNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("(Invalid matrix) element at row %d and column %d is not a float", row + 1, column + 1));
                }

                floatMatrix[row][column] = floatNum;
            }
        }

        return floatMatrix;
    }

    @Override
    public List<Object> toList() {
        return new ArrayList<>(children.values());
    }

    @Override
    public String toString() {
        return getNestedFields().toString();
    }

}
