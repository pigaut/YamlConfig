package io.github.pigaut.yamlib.config.node;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.config.configurator.*;
import io.github.pigaut.yamlib.config.parser.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public abstract class SectionNode implements ConfigSection {

    public static final List<Class<?>> SCALAR_TYPES = List.of(Boolean.class, Character.class, String.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, BigInteger.class, BigDecimal.class);

    protected final Map<String, @NotNull Object> children = new LinkedHashMap<>();
    private boolean keyless = false;
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
        return new HashSet<>(children.keySet());
    }

    @Override
    public boolean contains(@NotNull String path) {
        return getOptionalField(path).isPresent();
    }

    @Override
    public void set(@NotNull String path, Object value) {
        Preconditions.checkNotNull(path, "Path cannot be null.");
        Preconditions.checkArgument(!path.isBlank(), "Path cannot be empty.");

        PathIterator pathIterator = new PathIterator(this, path);

        if (value instanceof Optional<?> optional) {
            value = optional.orElse(null);
        }

        if (value == null || SCALAR_TYPES.contains(value.getClass())) {
            pathIterator.setScalar(value);
            return;
        }

        Class<?> classType = value.getClass();
        Serializer serializer = getRoot().getParser().getSerializer(classType);
        if (serializer != null) {
            pathIterator.setScalar(serializer.serialize(value));
            return;
        }

        ConfigMapper mapper = getRoot().getConfigurator().getMapper(classType);
        if (mapper != null) {
            mapper.map(getSectionOrCreate(path), value);
            return;
        }

        throw new IllegalArgumentException("No serializer or mapper found for value of type " + classType.getSimpleName());
    }

    @Override
    public void add(Object value) {
        set("[" + children.size() + "]", value);
    }

    @Override
    public void map(@NotNull Object value) throws IllegalArgumentException {
        getRoot().getConfigurator().map(this, value);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public ConfigSection addSection() {
        return getSectionOrCreate("[" + size() + "]");
    }

    @Override
    public boolean isKeyless() {
        return keyless;
    }

    @Override
    public void setKeyless(boolean keyless) {
        if (!isRoot()) {
            ConfigSection parent = getParent();
            FlowStyle defaultFlowStyle = parent.getDefaultFlowStyle();
            this.flowStyle = defaultFlowStyle != null ? defaultFlowStyle : parent.isKeyless() && keyless ? FlowStyle.FLOW : FlowStyle.BLOCK;
        }

        this.keyless = keyless;
    }

    @Override
    public void isKeylessOrThrow() throws InvalidConfigurationException {
        if (!keyless) {
            throw new InvalidConfigurationException(this, "is not a list");
        }
    }

    @Override
    public void isKeyedOrThrow() throws InvalidConfigurationException {
        if (keyless) {
            throw new InvalidConfigurationException(this, "is not a section");
        }
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
        return getRoot().getConfigurator().loadOrThrow(type, this);
    }

    @Override
    public <T> @NotNull T load(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        return getSection(path).load(type);
    }

    @Override
    public <T> @NotNull T get(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        try {
            return getRoot().getParser().deserializeOrThrow(type, getString(path));
        } catch (DeserializationException e) {
            throw new InvalidConfigurationException(this, path, e.getMessage());
        }
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
        return getRoot().getConfigurator().load(type, this);
    }

    @Override
    public <T> Optional<T> loadOptional(@NotNull String path, @NotNull Class<T> type) {
        Optional<ConfigSection> sectionField = getOptionalSection(path);
        return sectionField.flatMap(section -> section.loadOptional(type));
    }

    @Override
    public <T> Optional<T> getOptional(@NotNull String path, @NotNull Class<T> type) {
        return getOptionalScalar(path).map(scalar -> getRoot().getParser().deserialize(type, scalar.toString()));
    }

    @Override
    public <T> Optional<T> getOrLoadOptional(@NotNull String path, @NotNull Class<T> type) throws IllegalArgumentException {
        if (getField(path) instanceof ConfigSection sectionToLoad) {
            return sectionToLoad.loadOptional(type);
        }
        return getOptional(path, type);
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
    public @NotNull ConfigSection getSection(@NotNull String path) throws InvalidConfigurationException {
        Object field = getField(path);

        if (!(field instanceof ConfigSection section)) {
            throw new InvalidConfigurationException(this, path, "is not a section");
        }

        return section;
    }

    @Override
    public @NotNull SectionNode getSectionOrCreate(@NotNull String path) {
        PathIterator pathIterator = new PathIterator(this, path);

        SectionNode section = null;
        while (pathIterator.hasNext()) {
            section = pathIterator.nextSectionOrCreate();
        }

        return section;
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(@NotNull String path) {
        return getOptionalField(path)
                .filter(ConfigSection.class::isInstance)
                .map(ConfigSection.class::cast);
    }

    @Override
    public Map<String, Object> getNestedFields() {
        return new LinkedHashMap<>(children);
    }

    @Override
    public Map<String, Object> getNestedScalars() {
        Map<String, Object> scalars = new HashMap<>();

        children.forEach((key, value) -> {
            if (!(value instanceof ConfigSection)) {
                scalars.put(key, value);
            }
        });

        return scalars;
    }

    @Override
    public Set<ConfigSection> getNestedSections() {
        Set<ConfigSection> sections = new HashSet<>();

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
            section.isKeylessOrThrow();
            return section.getNestedFields().values().stream().toList();
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

    @Override
    public List<BigInteger> getBigIntegerList(@NotNull String path) {
        List<BigInteger> bigIntegers = new ArrayList<>();

        for (Number number : getListOrThrow(path, Number.class, "is not a list of big integers").toList()) {
            if (number instanceof BigInteger bigInteger) {
                bigIntegers.add(bigInteger);
                continue;
            }
            bigIntegers.add(BigInteger.valueOf(number.longValue()));
        }

        return bigIntegers;
    }

    @Override
    public List<BigDecimal> getBigDecimalList(@NotNull String path) {
        List<BigDecimal> bigDecimals = new ArrayList<>();

        for (Number number : getListOrThrow(path, Number.class, "is not a list of big decimals").toList()) {
            if (number instanceof BigDecimal bigDecimal) {
                bigDecimals.add(bigDecimal);
                continue;
            }
            bigDecimals.add(BigDecimal.valueOf(number.doubleValue()));
        }

        return bigDecimals;
    }

    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> type) {
        List<Object> fields = getFieldList(path);

        ConfigLoader<T> loader = getRoot().getConfigurator().getExactLoader(type);
        Deserializer<T> deserializer = getRoot().getParser().getExactDeserializer(type);

        if (loader == null && deserializer == null) {
            throw new IllegalArgumentException("No deserializer or loader exists for class: " + type.getSimpleName());
        }

        List<T> list = new ArrayList<>();
        for (Object field : fields) {
            if (field instanceof ConfigSection section) {
                if (loader == null) {
                    throw new InvalidConfigurationException(this, path, "is not a list of " + type.getSimpleName());
                }
                list.add(loader.load(section));
            } else {
                if (deserializer == null) {
                    throw new InvalidConfigurationException(this, path, "is not a list of " + type.getSimpleName());
                }
                try {
                    list.add(deserializer.deserialize(field.toString()));
                } catch (DeserializationException e) {
                    throw new InvalidConfigurationException(this, path, e.getMessage());
                }
            }
        }

        return list;
    }

    public @NotNull Object getField(@NotNull String path) throws InvalidConfigurationException {
        PathIterator pathIterator = new PathIterator(this, path);

        Object node = null;
        while (pathIterator.hasNext()) {
            node = pathIterator.next();
        }

        if (node == null) {
            throw new InvalidConfigurationException(this, path, "is not set");
        }

        return node;
    }

    @Override
    public Optional<Object> getOptionalField(@NotNull String path) {
        PathIterator pathIterator = new PathIterator(this, path);

        Object node = null;
        while (pathIterator.hasNext()) {
            node = pathIterator.next();
        }

        return Optional.ofNullable(node);
    }

    @Override
    public @NotNull Object getScalar(@NotNull String path) throws InvalidConfigurationException {
        Object field = getField(path);
        if (field instanceof ConfigSection) {
            throw new InvalidConfigurationException(this, path, "is not a scalar value");
        }
        return field;
    }

    @Override
    public Optional<Object> getOptionalScalar(@NotNull String path) {
        return getOptionalField(path).filter(field -> !(field instanceof ConfigSection));
    }

    @Override
    public boolean getBoolean(@NotNull String path) throws InvalidConfigurationException {
        return getScalarOrThrow(path, Boolean.class, "is not a boolean");
    }

    @Override
    public Optional<Boolean> getOptionalBoolean(@NotNull String path) {
        return getOptionalScalar(path, Boolean.class);
    }

    @Override
    public char getCharacter(@NotNull String path) throws InvalidConfigurationException {
        return getScalarOrThrow(path, Character.class, "is not a character");
    }

    @Override
    public Optional<Character> getOptionalCharacter(@NotNull String path) {
        return getOptionalScalar(path, Character.class);
    }

    @Override
    public @NotNull String getString(@NotNull String path) throws InvalidConfigurationException {
        return getScalar(path).toString();
    }

    @Override
    public Optional<String> getOptionalString(@NotNull String path) {
        return getOptionalScalar(path).map(Object::toString);
    }

    @Override
    public Object[] getSplitString(String path, String regex, Class<?>... types) {
        final int length = types.length;
        final String[] elements = getString(path).split(regex);

        if (elements.length != length) {
            throw new InvalidConfigurationException(this, path,
                    "(Invalid split string) expected " + types.length + " elements, but got " + elements.length);
        }

        Object[] splitString = new Object[types.length];

        final Parser parser = getRoot().getParser();
        for (int i = 0; i < types.length; i++) {
            try {
                splitString[i] = parser.deserializeOrThrow(types[i], elements[i]);
            } catch (DeserializationException e) {
                throw new InvalidConfigurationException(this, path,
                        "(Invalid split string) " + e.getMessage());
            }
        }

        return splitString;
    }

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
        return getScalarOrThrow(path, Integer.class, "is not an integer");
    }

    @Override
    public Optional<Integer> getOptionalInteger(@NotNull String path) {
        return getOptionalScalar(path, Integer.class);
    }

    @Override
    public long getLong(@NotNull String path) throws InvalidConfigurationException {
        return getScalarOrThrow(path, Number.class, "is not a long").longValue();
    }

    @Override
    public Optional<Long> getOptionalLong(@NotNull String path) {
        return getOptionalScalar(path, Number.class).map(Number::longValue);
    }

    @Override
    public float getFloat(@NotNull String path) throws InvalidConfigurationException {
        return getScalarOrThrow(path, Number.class, "is not a float").floatValue();
    }

    @Override
    public Optional<Float> getOptionalFloat(@NotNull String path) {
        return getOptionalScalar(path, Number.class).map(Number::floatValue);
    }

    @Override
    public double getDouble(@NotNull String path) throws InvalidConfigurationException {
        return getScalarOrThrow(path, Number.class, "is not a double").doubleValue();
    }

    @Override
    public Optional<Double> getOptionalDouble(@NotNull String path) {
        return getOptionalScalar(path, Number.class).map(Number::doubleValue);
    }

    @Override
    public @NotNull BigInteger getBigInteger(@NotNull String path) throws InvalidConfigurationException {
        Number number = getScalarOrThrow(path, Number.class, "is not a big integer");
        return number instanceof BigInteger bigInteger ? bigInteger : BigInteger.valueOf(number.longValue());
    }

    @Override
    public Optional<BigInteger> getOptionalBigInteger(@NotNull String path) {
        try {
            return Optional.of(getBigInteger(path));
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull BigDecimal getBigDecimal(@NotNull String path) throws InvalidConfigurationException {
        Number number = getScalarOrThrow(path, Number.class, "is not a big decimal");
        return number instanceof BigDecimal bigDecimal ? bigDecimal : BigDecimal.valueOf(number.doubleValue());
    }

    @Override
    public Optional<BigDecimal> getOptionalBigDecimal(@NotNull String path) {
        try {
            return Optional.of(getBigDecimal(path));
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
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
    public char[][] getCharMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        char[][] charMatrix = new char[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Character character)) {
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix: Element at row %d and column %d is not a character", row + 1, column + 1));
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
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d is not an integer", row + 1, column + 1));
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
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d is not a boolean", row + 1, column + 1));
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
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d is not a double", row + 1, column + 1));
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
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d is not a long", row + 1, column + 1));
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
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d is not a float", row + 1, column + 1));
                }

                floatMatrix[row][column] = floatNum;
            }
        }

        return floatMatrix;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[][] getMatrix(String path, Class<T> type, int rowCount, int columnCount) {
        String[][] stringMatrix = getStringMatrix(path, rowCount, columnCount);
        T[][] matrix = (T[][]) Array.newInstance(type, rowCount, columnCount);

        Deserializer<T> deserializer = getRoot().getParser().getExactDeserializer(type);
        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer found for class " + type.getSimpleName());
        }

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                String value = stringMatrix[row][column];

                try {
                    matrix[row][column] = deserializer.deserialize(value);
                } catch (DeserializationException e) {
                    throw new InvalidConfigurationException(this, path, String.format("Invalid matrix. Element at row %d and column %d %s", row + 1, column + 1, e.getMessage()));
                }
            }
        }

        return matrix;
    }

    @Override
    public List<Object> toList() {
        return new ArrayList<>(children.values());
    }

    private void reindexFields() {
        Map<String, Object> indexedMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, Object> entry : children.entrySet()) {
            indexedMap.put(Integer.toString(index++), entry.getValue());
        }
        children.clear();
        children.putAll(indexedMap);
    }

    private <T> T getScalarOrThrow(@NotNull String path, Class<T> type, String error) {
        Object node = getScalar(path);
        if (!type.isInstance(node)) {
            throw new InvalidConfigurationException(this, path, error);
        }
        return type.cast(node);
    }

    private <T> Optional<T> getOptionalScalar(@NotNull String path, Class<T> type) {
        return getOptionalScalar(path).filter(type::isInstance).map(type::cast);
    }

    private void setScalar(String path, Object value) {
        String formattedPath = path.replace("][", ".").replace("[", ".").replace("]", "");

        SectionNode section = this;
        String key = formattedPath;

        int lastKeyIndex = formattedPath.lastIndexOf(".");
        if (lastKeyIndex != -1) {
            section = getSectionOrCreate(formattedPath.substring(0, lastKeyIndex));
            key = formattedPath.substring(lastKeyIndex + 1);
        }

        section.children.put(key, value != null ? value : "");
    }

    @Override
    public String toString() {
        return getNestedFields().toString();
    }

    // Navigates nodes, creates section nodes, and scalar nodes
    protected class PathIterator implements Iterator<Object> {
        private static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+)]");

        private final SectionNode root;
        private final List<NodeKey> keys = new ArrayList<>();
        private SectionNode currentSection;
        private int pointer = 0;

        public PathIterator(SectionNode root, String path) {
            this.root = root;
            this.currentSection = root;
            loadKeys(path.toLowerCase());
        }

        public boolean hasNext() {
            return pointer < keys.size();
        }

        @Nullable
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more keys in the path.");
            }

            if (currentSection == null) {
                return null;
            }

            Object node = keys.get(pointer++).getNode(currentSection);
            if (!hasNext()) {
                return node;
            }

            currentSection = (node instanceof SectionNode section) ? section : null;
            return node;
        }

        public boolean isLast() {
            return pointer + 1 >= keys.size();
        }

        @NotNull
        public SectionNode nextSectionOrCreate() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more keys in the path.");
            }

            if (currentSection == null) {
                throw new IllegalStateException("Current section is null.");
            }

            NodeKey key = keys.get(pointer++);
            Object node = key.getNode(currentSection);

            currentSection = (node instanceof SectionNode section) ? section : key.createSection(currentSection);

            return currentSection;
        }

        public void setScalar(Object scalar) {
            while (hasNext()) {
                if (isLast()) {
                    setNext(scalar);
                    break;
                }
                nextSectionOrCreate();
            }
        }

        private void setNext(Object scalar) {
            if (!hasNext()) {
                throw new NoSuchElementException("No more keys in the path.");
            }

            if (currentSection == null) {
                throw new IllegalStateException("Current section is null.");
            }

            NodeKey key = keys.get(pointer++);
            key.setScalar(currentSection, scalar);
        }

        public void reset() {
            pointer = 0;
            currentSection = root;
        }

        private void loadKeys(String path) {
            final String[] keys = path.split("\\.");
            for (String key : keys) {
                Matcher indicesMatcher = INDEX_PATTERN.matcher(key);
                if (indicesMatcher.matches()) {
                    int index;
                    try {
                        index = Integer.parseInt(indicesMatcher.group(1));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                    this.keys.add(new IndexKey(index));
                    continue;
                }

                String keyWithoutIndices = indicesMatcher.replaceAll("");

                String[] aliases = keyWithoutIndices.split("\\|");
                this.keys.add(aliases.length > 1 ? new MultiKey(aliases) : new SimpleKey(keyWithoutIndices));

                indicesMatcher.reset();
                while (indicesMatcher.find()) {
                    int index;
                    try {
                        index = Integer.parseInt(indicesMatcher.group(1));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                    this.keys.add(new IndexKey(index));
                }
            }
        }

        private interface NodeKey {
            Object getNode(SectionNode parent);

            SectionNode createSection(SectionNode parent);

            void setScalar(SectionNode parent, Object scalar);
        }

        private class SimpleKey implements NodeKey {
            private final String key;

            private SimpleKey(String key) {
                this.key = key;
            }

            @Override
            public Object getNode(SectionNode parent) {
                return parent.children.get(key);
            }

            @Override
            public SectionNode createSection(SectionNode parent) {
                parent.setKeyless(false);

                Object node = getNode(parent);
                if (node instanceof SectionNode foundSection) {
                    return foundSection;
                }

                return new ChildSection(parent, key, defaultFlowStyle);
            }

            @Override
            public void setScalar(SectionNode parent, Object scalar) {
                parent.setKeyless(false);

                if (scalar == null) {
                    parent.children.remove(key);
                    return;
                }

                parent.children.put(key, scalar);
            }
        }

        private class IndexKey implements NodeKey {
            private final int index;

            private IndexKey(int index) {
                this.index = index;
            }

            @Override
            public Object getNode(SectionNode parent) {
                int count = 0;
                for (Object field : parent.children.values()) {
                    if (index == count++) {
                        return field;
                    }
                }
                return null;
            }

            @Override
            public SectionNode createSection(SectionNode parent) {
                parent.setKeyless(true);

                Object node = getNode(parent);
                if (node instanceof SectionNode foundSection) {
                    return foundSection;
                }

                SectionNode currentSection = null;
                int size;
                while ((size = parent.size()) <= index) {
                    currentSection = new ChildSection(parent, Integer.toString(size), defaultFlowStyle);
                }

                return currentSection;
            }

            @Override
            public void setScalar(SectionNode parent, Object scalar) {
                parent.setKeyless(true);

                Map<String, Object> fields = parent.children;
                String key = Integer.toString(index);

                if (scalar == null) {
                    fields.remove(key);
                    return;
                }

                int size;
                while ((size = parent.size()) < index) {
                    fields.put(Integer.toString(size), "");
                }

                fields.put(key, scalar);
            }

        }

        private class MultiKey implements NodeKey {
            private final String[] keys;

            private MultiKey(String[] keys) {
                Preconditions.checkArgument(keys.length > 0, "Keys cannot be empty");
                this.keys = keys;
            }

            @Override
            public Object getNode(SectionNode parent) {
                for (String key : keys) {
                    Object node = parent.children.get(key);
                    if (node != null) {
                        return node;
                    }
                }
                return null;
            }

            @Override
            public SectionNode createSection(SectionNode parent) {
                parent.setKeyless(false);

                Object node = getNode(parent);
                if (node instanceof SectionNode foundSection) {
                    return foundSection;
                }

                return new ChildSection(parent, keys[0], defaultFlowStyle);
            }

            @Override
            public void setScalar(SectionNode parent, Object scalar) {
                parent.setKeyless(false);

                Map<String, Object> fields = parent.children;
                for (String key : keys) {
                    if (fields.containsKey(key)) {
                        parent.children.put(key, scalar);
                        return;
                    }
                }
                fields.put(keys[0], scalar);
            }
        }

    }

}
