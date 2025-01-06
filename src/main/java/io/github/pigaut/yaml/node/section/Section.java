package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.parser.*;
import io.github.pigaut.yaml.path.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public abstract class Section extends Branch implements ConfigSection {

    private final Map<@NotNull String, @NotNull ConfigField> fieldsByKey = new LinkedHashMap<>();

    protected Section(FlowStyle flowStyle) {
        super(flowStyle);
    }

    public void putNode(String key, Field value) {
        fieldsByKey.put(key, value);
    }

    public ConfigField getNode(String key) {
        return fieldsByKey.get(key);
    }

    public void removeNode(String key) {
        fieldsByKey.remove(key);
    }

    @Override
    public int size() {
        return fieldsByKey.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Stream<@NotNull ConfigField> stream() {
        return fieldsByKey.values().stream();
    }

    @Override
    public <T> void map(@NotNull T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.mapSection(this, value);
    }

    @Override
    public <T> void add(@NotNull T value) {
        Preconditions.checkNotNull(value, "Cannot add null value to a section");

        final Class<?> classType = value.getClass();

        if (YamlConfig.isScalarType(classType)) {
            setScalar(YamlConfig.generateRandomKey(), value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(classType);

        if (mapper == null) {
            throw new IllegalArgumentException("No config setter or mapper found for class type: " + classType.getSimpleName());
        }

        final String key = mapper.createKey(value);
        switch (mapper.getDefaultMappingType()) {
            case SCALAR -> {
                final Object scalarToSet = mapper.createScalar(value);
                setScalar(key, scalarToSet);
            }
            case SECTION -> {
                final ConfigSection sectionToMap = getSectionOrCreate(key);
                if (!mapper.keepExistingFields()) {
                    sectionToMap.clear();
                }
                mapper.mapSection(sectionToMap, value);
            }
            case SEQUENCE -> {
                final ConfigSequence sequenceToMap = getSequenceOrCreate(key);
                if (!mapper.keepExistingFields()) {
                    sequenceToMap.clear();
                }
                mapper.mapSequence(sequenceToMap, value);
            }
        }
    }

    @Override
    public <T> void addAll(Collection<T> elements) {
        for (T value : elements) {
            add(value);
        }
    }

    @Override
    public Set<String> getKeys() {
        return new LinkedHashSet<>(fieldsByKey.keySet());
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
    public boolean isSequence(@NotNull String path) {
        return getOptionalSequence(path).isPresent();
    }

    @Override
    public <T> void set(@NotNull String path, @NotNull T value) {
        Preconditions.checkNotNull(value, "Value cannot be null");

        final Class<?> valueClass = value.getClass();
        if (YamlConfig.isScalarType(valueClass)) {
            setScalar(path, value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(valueClass);

        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + valueClass.getSimpleName());
        }

        final ConfigField existingField = getOptionalField(path).orElse(null);
        final MappingType defaultMappingType = mapper.getDefaultMappingType();

        if (existingField != null) {
            switch (existingField.getFieldType()) {
                case SCALAR -> {
                    try {
                        final Object scalarToSet = mapper.createScalar(value);
                        setScalar(path, scalarToSet);
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SCALAR) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
                case SECTION -> {
                    try {
                        final ConfigSection sectionToMap = getSectionOrCreate(path);
                        if (!mapper.keepExistingFields()) {
                            sectionToMap.clear();
                        }
                        mapper.mapSection(sectionToMap, value);
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SECTION) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
                case SEQUENCE -> {
                    try {
                        final ConfigSequence sequenceToMap = getSequenceOrCreate(path);
                        if (!mapper.keepExistingFields()) {
                            sequenceToMap.clear();
                        }
                        mapper.mapSequence(sequenceToMap, value);
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SEQUENCE) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
            }
        } else {
            switch (defaultMappingType) {
                case SCALAR -> {
                    final Object scalarToSet = mapper.createScalar(value);
                    setScalar(path, scalarToSet);
                }
                case SECTION -> {
                    final ConfigSection sectionToMap = getSectionOrCreate(path);
                    if (!mapper.keepExistingFields()) {
                        sectionToMap.clear();
                    }
                    mapper.mapSection(sectionToMap, value);
                }
                case SEQUENCE -> {
                    final ConfigSequence sequenceToMap = getSequenceOrCreate(path);
                    if (!mapper.keepExistingFields()) {
                        sequenceToMap.clear();
                    }
                    mapper.mapSequence(sequenceToMap, value);
                }
            }
        }

    }

    @Override
    public void remove(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);
        while (iterator.hasNext()) {
            if (iterator.isLast()) {
                final Branch currentBranch = iterator.getCurrentBranch();
                final FieldKey lastKey = iterator.getLastKey();
                lastKey.remove(currentBranch);
                break;
            }
            iterator.branch();
        }
    }

    @Override
    public void formatKeys(StringFormatter formatter) {
        final Set<ConfigField> fields = getNestedFields();
        clear();
        for (ConfigField field : fields) {
            fieldsByKey.put(formatter.format(field.getKey()), field);
        }
    }

    @Override
    public <T> @NotNull T get(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        final ConfigField field = getField(path);
        return field.load(type);
    }

    @Override
    public <T> Optional<T> getOptional(@NotNull String path, @NotNull Class<T> type) {
        return ConfigOptional.of(() -> get(path, type));
    }

    @Override
    public <T> List<@NotNull T> getAll(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException {
        final ConfigField optionalField = getOptionalField(path).orElse(null);
        if (optionalField instanceof ConfigBranch branch) {
            return branch.getAll(type);
        }
        return List.of();
    }

    @Override
    public @NotNull ConfigSection getSection(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getOptionalField(path).orElseThrow(() ->
                new InvalidConfigurationException(this, path, "Section not found"));
        return field.toSection();
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(@NotNull String path) {
        Optional<ConfigField> optionalField = getOptionalField(path);
        return optionalField.flatMap(ConfigField::asSection);
    }

    @Override
    public @NotNull Section getSectionOrCreate(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.branch();
        }
        return currentBranch.convertToSection();
    }

    @Override
    public @NotNull ConfigSequence getSequence(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getOptionalField(path).orElseThrow(() ->
                new InvalidConfigurationException(this, path, "List not found"));
        return field.toSequence();
    }

    @Override
    public Optional<ConfigSequence> getOptionalSequence(@NotNull String path) {
        Optional<ConfigField> optionalField = getOptionalField(path);
        return optionalField.flatMap(ConfigField::asSequence);
    }

    @Override
    public @NotNull ConfigSequence getSequenceOrCreate(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.branch();
        }
        return currentBranch.convertToSequence();
    }

    @Override
    public @NotNull Set<ConfigField> getNestedFields(@NotNull String path) {
        final ConfigField foundField = getOptionalField(path).orElse(null);
        if (foundField instanceof ConfigBranch branch) {
            return branch.getNestedFields();
        }
        return Set.of();
    }

    @Override
    public @NotNull Set<ConfigSection> getNestedSections(@NotNull String path) {
        final ConfigField foundField = getOptionalField(path).orElse(null);
        if (foundField instanceof ConfigBranch branch) {
            return branch.getNestedSections();
        }
        return Set.of();
    }

    @Override
    public List<ConfigField> getFieldList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toFieldList).orElse(List.of());
    }

    @Override
    public List<ConfigScalar> getScalarList(@NotNull String path) throws InvalidConfigurationException {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toScalarList).orElse(List.of());
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) throws InvalidConfigurationException {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toSectionList).orElse(List.of());
    }

    @Override
    public List<Boolean> getBooleanList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toBooleanList).orElse(List.of());
    }

    @Override
    public List<Character> getCharacterList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toCharacterList).orElse(List.of());
    }

    @Override
    public List<String> getStringList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toStringList).orElse(List.of());
    }

    @Override
    public List<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(sequence -> sequence.toStringList(formatter)).orElse(List.of());
    }

    @Override
    public List<Integer> getIntegerList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toIntegerList).orElse(List.of());
    }

    @Override
    public List<Long> getLongList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toLongList).orElse(List.of());
    }

    @Override
    public List<Float> getFloatList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toFloatList).orElse(List.of());
    }

    @Override
    public List<Double> getDoubleList(@NotNull String path) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(ConfigSequence::toDoubleList).orElse(List.of());
    }

    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> type) {
        final Optional<ConfigSequence> optionalSequence = getOptionalSequence(path);
        return optionalSequence.map(sequence -> sequence.toList(type)).orElse(List.of());
    }

    public @NotNull ConfigField getField(@NotNull String path) throws InvalidConfigurationException {
        final PathIterator iterator = PathIterator.of(this, path);

        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }

        if (field == null) {
            throw new InvalidConfigurationException(this, path, "Field is not set");
        }

        return field;
    }

    @Override
    public Optional<ConfigField> getOptionalField(@NotNull String path) {
        return ConfigOptional.of(() -> getField(path));
    }

    @Override
    public @NotNull ConfigScalar getScalar(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getOptionalField(path).orElseThrow(() ->
                new InvalidConfigurationException(this, path, "Value is not set"));
        return field.toScalar();
    }

    @Override
    public Optional<ConfigScalar> getOptionalScalar(@NotNull String path) {
        Optional<ConfigField> optionalField = getOptionalField(path);
        return optionalField.flatMap(ConfigField::asScalar);
    }

    @Override
    public boolean getBoolean(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toBoolean();
    }

    @Override
    public Optional<Boolean> getOptionalBoolean(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asBoolean);
    }

    @Override
    public char getCharacter(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toCharacter();
    }

    @Override
    public Optional<Character> getOptionalCharacter(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asCharacter);
    }

    @Override
    public @NotNull String getString(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toString();
    }

    @Override
    public Optional<String> getOptionalString(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.map(ConfigScalar::toString);
    }

    @Override
    public @NotNull String getString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        String string = getString(path);
        return formatter.format(string);
    }

    @Override
    public Optional<String> getOptionalString(@NotNull String path, @NotNull StringFormatter formatter) {
        Optional<String> optionalString = getOptionalString(path);
        return optionalString.map(formatter::format);
    }

    @Override
    public int getInteger(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toInteger();
    }

    @Override
    public Optional<Integer> getOptionalInteger(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asInteger);
    }

    @Override
    public long getLong(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toLong();
    }

    @Override
    public Optional<Long> getOptionalLong(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asLong);
    }

    @Override
    public float getFloat(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toFloat();
    }

    @Override
    public Optional<Float> getOptionalFloat(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asFloat);
    }

    @Override
    public double getDouble(@NotNull String path) throws InvalidConfigurationException {
        ConfigField field = getField(path);
        return field.toScalar().toDouble();
    }

    @Override
    public Optional<Double> getOptionalDouble(@NotNull String path) {
        Optional<ConfigScalar> optionalScalar = getOptionalScalar(path);
        return optionalScalar.flatMap(ConfigScalar::asDouble);
    }

    public ConfigScalar[][] getMatrix(@NotNull String path, int rowSize, int columnSize) throws InvalidConfigurationException {
        Preconditions.checkArgument(rowSize > 0 && columnSize > 0, "Matrix must have at least one row and one column.");

        final ConfigField rowsField = getField(path);
        if (!(rowsField instanceof ConfigSequence rowsSequence)) {
            throw new InvalidConfigurationException(this, path, "(Invalid matrix) is not a list");
        }

        final List<ConfigField> rows = rowsSequence.toFieldList();

        final int rowCount = rows.size();
        if (rowCount != rowSize) {
            throw new InvalidConfigurationException(this, path,
                    "(Invalid matrix) expected " + rowSize + " rows, but got " + rowCount);
        }

        final ConfigScalar[][] matrix = new ConfigScalar[rowSize][columnSize];
        for (int row = 0; row < rowSize; row++) {
            final ConfigField columnsField = rows.get(row);

            if (!(columnsField instanceof ConfigSequence columnsSequence)) {
                throw new InvalidConfigurationException(columnsField, "(Invalid matrix) is not a list");
            }

            final List<ConfigField> columns = columnsSequence.toFieldList();

            final int columnCount = columns.size();
            if (columnCount != columnSize) {
                throw new InvalidConfigurationException(this, path,
                        "(Invalid matrix) at row " + (row + 1) + ", expected " + columnSize + " columns, but got " + columnCount);
            }

            for (int column = 0; column < columnSize; column++) {
                final ConfigField field = columns.get(column);

                if (!(field instanceof ConfigScalar scalar)) {
                    throw new InvalidConfigurationException(this, path,
                            "(Invalid matrix) row " + (row + 1) + " and column " + (column + 1) + " is not a value");
                }

                matrix[row][column] = scalar;
            }
        }

        return matrix;
    }

    @Override
    public boolean[][] getBooleanMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        boolean[][] booleanMatrix = new boolean[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                booleanMatrix[row][column] = scalar.asBoolean().orElseThrow(() -> new InvalidConfigurationException(this, path,
                        "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not a boolean"));
            }
        }

        return booleanMatrix;
    }

    @Override
    public char[][] getCharacterMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        char[][] charMatrix = new char[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                charMatrix[row][column] = scalar.asCharacter().orElseThrow(() ->
                        new InvalidConfigurationException(this, path,
                                "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not a character"));
            }
        }

        return charMatrix;
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
    public String[][] getStringMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        String[][] stringMatrix = new String[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                stringMatrix[row][column] = matrix[row][column].toString();
            }
        }

        return stringMatrix;
    }

    @Override
    public String[][] getStringMatrix(@NotNull String path, int rowCount, int columnCount, StringFormatter formatter) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        String[][] stringMatrix = new String[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final String string = matrix[row][column].toString();
                stringMatrix[row][column] = formatter.format(string);
            }
        }

        return stringMatrix;
    }

    @Override
    public int[][] getIntegerMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        int[][] intMatrix = new int[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                intMatrix[row][column] = scalar.asInteger().orElseThrow(() ->
                        new InvalidConfigurationException(this, path,
                                "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not an integer"));
            }
        }

        return intMatrix;
    }

    @Override
    public long[][] getLongMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        long[][] longMatrix = new long[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                longMatrix[row][column] = scalar.asLong().orElseThrow(() ->
                        new InvalidConfigurationException(this, path,
                                "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not a long"));
            }
        }

        return longMatrix;
    }

    @Override
    public float[][] getFloatMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        float[][] floatMatrix = new float[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                floatMatrix[row][column] = scalar.asFloat().orElseThrow(() ->
                        new InvalidConfigurationException(this, path,
                                "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not a float"));
            }
        }

        return floatMatrix;
    }

    @Override
    public double[][] getDoubleMatrix(@NotNull String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        ConfigScalar[][] matrix = getMatrix(path, rowCount, columnCount);
        double[][] doubleMatrix = new double[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final ConfigScalar scalar = matrix[row][column];

                final int effectiveRow = row + 1;
                final int effectiveColumn = column + 1;

                doubleMatrix[row][column] = scalar.asDouble().orElseThrow(() ->
                        new InvalidConfigurationException(this, path,
                                "(Invalid matrix) element at row " + effectiveRow + " and column " + effectiveColumn + " is not a double"));
            }
        }

        return doubleMatrix;
    }

    private void setScalar(@NotNull String path, @NotNull Object value) {
        final PathIterator iterator = PathIterator.of(this, path);
        while (iterator.hasNext()) {
            if (iterator.isLast()) {
                final Branch currentBranch = iterator.getCurrentBranch();
                final FieldKey lastKey = iterator.getLastKey();

                lastKey.set(currentBranch, value);
                break;
            }
            iterator.branch();
        }
    }

    @Override
    public Iterator<@NotNull ConfigField> iterator() {
        return fieldsByKey.values().iterator();
    }

    @Override
    public @NotNull Object getValue() {
        return toMap();
    }

    @Override
    public @NotNull MappingType getFieldType() {
        return MappingType.SECTION;
    }

    @Override
    public void clear() {
        fieldsByKey.clear();
    }

    @Override
    public <T> @NotNull T load(@NotNull Class<T> type) throws InvalidConfigurationException {
        final Configurator configurator = getRoot().getConfigurator();
        ConfigLoader<? extends T> loader = configurator.getLoader(type, this);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + type.getSimpleName());
        }

        this.setProblemDescription(loader.getProblemDescription());
        final T value = loader.loadFromSection(this);
        this.setProblemDescription(null);
        return value;
    }

    @Override
    public @NotNull ConfigScalar toScalar() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a value but found a section");
    }

    @Override
    public @NotNull ConfigSection toSection() throws InvalidConfigurationException {
        return this;
    }

    @Override
    public @NotNull ConfigSequence toSequence() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a list but found a section");
    }

    @Override
    public @NotNull Section convertToSection() {
        return this;
    }

    @Override
    public @NotNull List<Object> toList() {
        return fieldsByKey.values().stream()
                .map(ConfigField::getValue)
                .toList();
    }

    @Override
    public @NotNull Map<String, Object> toMap() {
        return new LinkedHashMap<>(fieldsByKey);
    }

    @Override
    public String toString() {
        return fieldsByKey.toString();
    }

}
