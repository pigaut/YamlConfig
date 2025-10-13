package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.optional.*;
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
    public @NotNull Section convertToSection() {
        return this;
    }

    @Override
    public <T> void map(@NotNull T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        var mapper = (ConfigMapper<? super T>) configurator.getMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.mapToSection(this, value);
    }

    @Override
    public <T> void add(@NotNull T value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        final var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            createScalar(YamlConfig.generateRandomKey(), value);
            return;
        }

        @SuppressWarnings("unchecked") final var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        final String key = mapper.createKey(value);
        final ConfigField existingField = getField(key).orElse(null);
        if (existingField != null) {
            if (mapper.clearExistingFields()) {
                existingField.clear();
            }
            try {
                if (existingField instanceof ConfigScalar scalar) {
                    mapper.mapToScalar(scalar, value);
                } else if (existingField instanceof ConfigSection section) {
                    mapper.mapToSection(section, value);
                } else if (existingField instanceof ConfigSequence sequence) {
                    mapper.mapToSequence(sequence, value);
                }
            } catch (UnsupportedMappingException e) {
                // Current mapper does not support the existing field type
            }
        }

        // Fallback to the default field mapping type if the value could not be mapped
        try {
            switch (mapper.getDefaultMappingType()) {
                case SCALAR -> {
                    final ConfigScalar scalar = getScalarOrCreate(key);
                    if (mapper.clearExistingFields()) {
                        scalar.clear();
                    }
                    mapper.mapToScalar(scalar, value);
                }
                case SECTION -> {
                    final ConfigSection section = getSectionOrCreate(key);
                    if (mapper.clearExistingFields()) {
                        section.clear();
                    }
                    mapper.mapToSection(section, value);
                }
                case SEQUENCE -> {
                    final ConfigSequence sequence = getSequenceOrCreate(key);
                    if (mapper.clearExistingFields()) {
                        sequence.clear();
                    }
                    mapper.mapToSequence(sequence, value);
                }
            }
        } catch (UnsupportedMappingException e) {
            throw new IllegalStateException(mapper.getClass() + " does not override the default mapping method");
        }
    }

    @Override
    public <T> void addAll(Collection<T> elements) {
        for (T value : elements) {
            add(value);
        }
    }

    @Override
    public @NotNull List<Object> toList() {
        return fieldsByKey.values().stream()
                .map(ConfigField::getValue)
                .toList();
    }

    @Override
    public @NotNull Map<String, Object> toMap() {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, ConfigField> entry : fieldsByKey.entrySet()) {
            final ConfigField field = entry.getValue();
            map.put(entry.getKey(), field.getValue());
        }
        return map;
    }

    @Override
    public Set<String> getKeys() {
        return new LinkedHashSet<>(fieldsByKey.keySet());
    }

    @Override
    public boolean contains(@NotNull String path) {
        return getField(path).isSetInConfig();
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return getScalar(path).isSetInConfig();
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return getSection(path).isSetInConfig();
    }

    @Override
    public boolean isSequence(@NotNull String path) {
        return getSequence(path).isSetInConfig();
    }

    @Override
    public <T> void set(@NotNull String path, @Nullable T value) {
        if (value == null) {
            createScalar(path, "");
            return;
        }

        final var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            createScalar(path, value);
            return;
        }

        @SuppressWarnings("unchecked") final var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        final ConfigField existingField = getField(path).orElse(null);
        if (existingField != null) {
            if (mapper.clearExistingFields()) {
                existingField.clear();
            }
            try {
                if (existingField instanceof ConfigScalar scalar) {
                    mapper.mapToScalar(scalar, value);
                } else if (existingField instanceof ConfigSection section) {
                    mapper.mapToSection(section, value);
                } else if (existingField instanceof ConfigSequence sequence) {
                    mapper.mapToSequence(sequence, value);
                }
            } catch (UnsupportedMappingException e) {
                // Current mapper does not support the existing field type
            }
        }

        // Fallback to the default field mapping type if the value could not be mapped
        try {
            switch (mapper.getDefaultMappingType()) {
                case SCALAR -> {
                    final ConfigScalar scalar = getScalarOrCreate(path);
                    if (mapper.clearExistingFields()) {
                        scalar.clear();
                    }
                    mapper.mapToScalar(scalar, value);
                }
                case SECTION -> {
                    final ConfigSection section = getSectionOrCreate(path);
                    if (mapper.clearExistingFields()) {
                        section.clear();
                    }
                    mapper.mapToSection(section, value);
                }
                case SEQUENCE -> {
                    final ConfigSequence sequence = getSequenceOrCreate(path);
                    if (mapper.clearExistingFields()) {
                        sequence.clear();
                    }
                    mapper.mapToSequence(sequence, value);
                }
            }
        } catch (UnsupportedMappingException e) {
            throw new IllegalStateException(mapper.getClass() + " does not override the default mapping method");
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
            iterator.nextBranch();
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
    public @NotNull Section getSectionOrCreate(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch();
        }
        return currentBranch.convertToSection();
    }

    @Override
    public @NotNull ConfigSequence getSequenceOrCreate(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch();
        }
        return currentBranch.convertToSequence();
    }

    @Override
    public ConfigScalar getScalarOrCreate(@NotNull String path) {
        final ConfigScalar scalar = getScalar(path).orElse(null);
        return scalar != null ? scalar : createScalar(path, "");
    }

    @Override
    public @NotNull Set<ConfigField> getNestedFields(@NotNull String path) {
        return getBranch(path)
                .map(ConfigBranch::getNestedFields)
                .orElse(Set.of());
    }

    @Override
    public @NotNull Set<ConfigSection> getNestedSections(@NotNull String path) {
        return getBranch(path)
                .map(ConfigBranch::getNestedSections)
                .orElse(Set.of());
    }

    @Override
    public Set<ConfigSequence> getNestedSequences(@NotNull String path) {
        return getBranch(path)
                .map(ConfigBranch::getNestedSequences)
                .orElse(Set.of());
    }

    @Override
    public <T> List<T> getAll(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigurationException {
        return getBranch(path)
                .map(branch -> branch.getAll(classType))
                .orElse(List.of());
    }

    @Override
    public <T> List<T> getAllOrSkip(@NotNull String path, @NotNull Class<T> classType) {
        return getBranch(path)
                .map(branch -> branch.getAllOrSkip(classType))
                .orElse(List.of());
    }

    @Override
    public <T> @NotNull T getRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigurationException {
        return get(path, classType).orThrow();
    }

    @Override
    public ConfigField getRequiredField(@NotNull String path) throws InvalidConfigurationException {
        return getField(path).orThrow();
    }

    @Override
    public ConfigScalar getRequiredScalar(@NotNull String path) {
        return getScalar(path).orThrow();
    }

    @Override
    public ConfigSection getRequiredSection(@NotNull String path) throws InvalidConfigurationException {
        return getSection(path).orThrow();
    }

    @Override
    public ConfigSequence getRequiredSequence(@NotNull String path) throws InvalidConfigurationException {
        return getSequence(path).orThrow();
    }

    @Override
    public ConfigLine getRequiredLine(@NotNull String path) throws InvalidConfigurationException {
        return getLine(path).orThrow();
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(@NotNull String path) throws InvalidConfigurationException {
        return getBoolean(path).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(@NotNull String path) throws InvalidConfigurationException {
        return getCharacter(path).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String path) throws InvalidConfigurationException {
        return getString(path).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        return getString(path, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(@NotNull String path) throws InvalidConfigurationException {
        return getInteger(path).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(@NotNull String path) throws InvalidConfigurationException {
        return getLong(path).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(@NotNull String path) throws InvalidConfigurationException {
        return getFloat(path).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(@NotNull String path) throws InvalidConfigurationException {
        return getDouble(path).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> get(@NotNull String path, @NotNull Class<T> classType) {
        return getField(path).flatMap(field -> field.load(classType));
    }

    public ConfigOptional<ConfigField> getField(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);

        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }

        if (field == null) {
            return ConfigOptional.notSet(this, path, "Field is not set");
        }

        return ConfigOptional.of(field);
    }

    @Override
    public ConfigOptional<ConfigScalar> getScalar(@NotNull String path) {
        return getField(path).flatMap(ConfigField::toScalar);
    }

    @Override
    public ConfigOptional<ConfigSection> getSection(@NotNull String path) {
        return getField(path).flatMap(ConfigField::toSection);
    }

    @Override
    public ConfigOptional<ConfigSequence> getSequence(@NotNull String path) {
        return getField(path).flatMap(ConfigField::toSequence);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(@NotNull String path) {
        return getScalar(path).map(ConfigScalar::toLine);
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toCharacter);
    }

    @Override
    public @NotNull ConfigOptional<String> getString(@NotNull String path) {
        return getScalar(path).map(ConfigScalar::toString);
    }

    @Override
    public @NotNull ConfigOptional<String> getString(@NotNull String path, @NotNull StringFormatter formatter) {
        return getScalar(path).map(scalar -> formatter.format(scalar.toString()));
    }

    @Override
    public ConfigOptional<Integer> getInteger(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(@NotNull String path) {
        return getScalar(path).flatMap(ConfigScalar::toDouble);
    }

    @Override
    public <T> List<T> getList(@NotNull String path, Class<T> classType) throws InvalidConfigurationException {
        return getElements(path, classType).withDefault(List.of());
    }

    @Override
    public <T> ConfigOptional<List<T>> getElements(@NotNull String path, Class<T> classType) {
        return getSequence(path).flatMap(sequence -> sequence.toList(classType));
    }

    @Override
    public List<ConfigField> getFieldList(@NotNull String path) throws InvalidConfigurationException {
        return getFields(path).withDefault(List.of());
    }

    @Override
    public List<ConfigScalar> getScalarList(@NotNull String path) throws InvalidConfigurationException {
        return getScalars(path).withDefault(List.of());
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) throws InvalidConfigurationException {
        return getSections(path).withDefault(List.of());
    }

    @Override
    public List<Boolean> getBooleanList(@NotNull String path) throws InvalidConfigurationException {
        return getBooleans(path).withDefault(List.of());
    }

    @Override
    public List<Character> getCharacterList(@NotNull String path) throws InvalidConfigurationException {
        return getCharacters(path).withDefault(List.of());
    }

    @Override
    public List<String> getStringList(@NotNull String path) throws InvalidConfigurationException {
        return getStrings(path).withDefault(List.of());
    }

    @Override
    public List<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException {
        return getStrings(path, formatter).withDefault(List.of());
    }

    @Override
    public List<Integer> getIntegerList(@NotNull String path) throws InvalidConfigurationException {
        return getIntegers(path).withDefault(List.of());
    }

    @Override
    public List<Long> getLongList(@NotNull String path) throws InvalidConfigurationException {
        return getLongs(path).withDefault(List.of());
    }

    @Override
    public List<Float> getFloatList(@NotNull String path) throws InvalidConfigurationException {
        return getFloats(path).withDefault(List.of());
    }

    @Override
    public List<Double> getDoubleList(@NotNull String path) throws InvalidConfigurationException {
        return getDoubles(path).withDefault(List.of());
    }

    @Override
    public ConfigOptional<List<ConfigField>> getFields(@NotNull String path) {
        return getSequence(path).map(ConfigSequence::toFieldList);
    }

    @Override
    public ConfigOptional<List<ConfigScalar>> getScalars(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toScalarList);
    }

    @Override
    public ConfigOptional<List<ConfigSection>> getSections(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toSectionList);
    }

    @Override
    public ConfigOptional<List<Boolean>> getBooleans(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toBooleanList);
    }

    @Override
    public ConfigOptional<List<Character>> getCharacters(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toCharacterList);
    }

    @Override
    public ConfigOptional<List<String>> getStrings(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toStringList);
    }

    @Override
    public ConfigOptional<List<String>> getStrings(@NotNull String path, @NotNull StringFormatter formatter) {
        return getSequence(path).flatMap(sequence -> sequence.toStringList(formatter));
    }

    @Override
    public ConfigOptional<List<Integer>> getIntegers(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toIntegerList);
    }

    @Override
    public ConfigOptional<List<Long>> getLongs(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toLongList);
    }

    @Override
    public ConfigOptional<List<Float>> getFloats(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toFloatList);
    }

    @Override
    public ConfigOptional<List<Double>> getDoubles(@NotNull String path) {
        return getSequence(path).flatMap(ConfigSequence::toDoubleList);
    }

    private ConfigOptional<ConfigBranch> getBranch(@NotNull String path) {
        final PathIterator iterator = PathIterator.of(this, path);

        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }

        if (field instanceof ConfigBranch branch) {
            return ConfigOptional.of(branch);
        }

        return ConfigOptional.notSet(this, path, "Field is not set");
    }

//    public ConfigScalar[][] getMatrix(@NotNull String path, int length, int height) {
//        Preconditions.checkArgument(length > 0 && height > 0, "Matrix must have at least one row and one column.");
//        final List<ConfigField> rows = getFieldList(path);
//        if (rows.size() != height) {
//            throw new InvalidConfigurationException(this, path, "Could not load matrix", "Expected " + height + " rows, but got " + rows.size());
//        }
//        final ConfigScalar[][] matrix = new ConfigScalar[length][height];
//        for (int row = 0; row < height; row++) {
//            final List<ConfigField> columns = rows.get(row).toSequence().toFieldList();
//            if (columns.size() != length) {
//                throw new InvalidConfigurationException(this, path, "Could not load matrix",
//                        "Expected " + length + " columns at row " + (row + 1) + ", but got " + columns.size());
//            }
//            for (int column = 0; column < length; column++) {
//                final ConfigField field = columns.get(column);
//                if (!(field instanceof ConfigScalar scalar)) {
//                    throw new InvalidConfigurationException(this, path, "Could not load matrix",
//                            "Expected a value at row " + (row + 1) + " and column " + (column + 1));
//                }
//                matrix[column][row] = scalar;
//            }
//        }
//        return matrix;
//    }
//
//    @Override
//    public boolean[][] getBooleanMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final boolean[][] booleanMatrix = new boolean[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[column][row];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load boolean matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not a boolean");
//                booleanMatrix[column][row] = scalar.asBoolean().orElseThrow(() -> matrixException);
//            }
//        }
//        return booleanMatrix;
//    }
//
//    // Matrix[x][y] matrix;
//    // Matrix[column][row] matrix;
//    // Matrix[length][height] matrix;
//
//    @Override
//    public char[][] getCharacterMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final char[][] charMatrix = new char[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[column][row];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load character matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not a character");
//                charMatrix[column][row] = scalar.asCharacter().orElseThrow(() -> matrixException);
//            }
//        }
//        return charMatrix;
//    }
//
//    @Override
//    public String[][] getStringMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final String[][] stringMatrix = new String[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                stringMatrix[column][row] = matrix[column][row].toString();
//            }
//        }
//        return stringMatrix;
//    }
//
//    @Override
//    public String[][] getStringMatrix(@NotNull String path, int length, int height, StringFormatter formatter) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final String[][] stringMatrix = new String[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                stringMatrix[column][row] =  matrix[column][row].toString(formatter);
//            }
//        }
//        return stringMatrix;
//    }
//
//    @Override
//    public int[][] getIntegerMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final int[][] intMatrix = new int[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[row][column];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load integer matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not an integer");
//                intMatrix[column][row] = scalar.asInteger().orElseThrow(() -> matrixException);
//            }
//        }
//        return intMatrix;
//    }
//
//    @Override
//    public long[][] getLongMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final long[][] longMatrix = new long[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[column][row];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load long matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not a long");
//                longMatrix[column][row] = scalar.asLong().orElseThrow(() -> matrixException);
//            }
//        }
//        return longMatrix;
//    }
//
//    @Override
//    public float[][] getFloatMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final float[][] floatMatrix = new float[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[column][row];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load float matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not a float");
//                floatMatrix[column][row] = scalar.asFloat().orElseThrow(() -> matrixException);
//            }
//        }
//        return floatMatrix;
//    }
//
//    @Override
//    public double[][] getDoubleMatrix(@NotNull String path, int length, int height) {
//        final ConfigScalar[][] matrix = getMatrix(path, length, height);
//        final double[][] doubleMatrix = new double[length][height];
//        for (int column = 0; column < length; column++) {
//            for (int row = 0; row < height; row++) {
//                final ConfigScalar scalar = matrix[column][row];
//                final RuntimeException matrixException = new InvalidConfigurationException(this, path, "Could not load double matrix",
//                        "Element at row " + (row + 1) + " and column " + (column + 1) + " is not a double");
//                doubleMatrix[column][row] = scalar.asDouble().orElseThrow(() -> matrixException);
//            }
//        }
//        return doubleMatrix;
//    }

    private Scalar createScalar(@NotNull String path, @NotNull Object value) {
        final PathIterator pathIterator = PathIterator.of(this, path);
        while (pathIterator.hasNext()) {
            if (pathIterator.isLast()) {
                final Branch currentBranch = pathIterator.getCurrentBranch();
                final FieldKey lastKey = pathIterator.getLastKey();

                return lastKey.createScalar(currentBranch, value);
            }
            pathIterator.nextBranch();
        }
        throw new AssertionError();
    }

    @Override
    public Iterator<@NotNull ConfigField> iterator() {
        return fieldsByKey.values().iterator();
    }

    @Override
    public void clear() {
        fieldsByKey.clear();
    }

    @Override
    public @NotNull Object getValue() {
        return toMap();
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return FieldType.SECTION;
    }

    @Override
    public <T> ConfigOptional<T> load(@NotNull Class<T> classType) {
        final ConfigRoot root = getRoot();
        final Configurator configurator = root.getConfigurator();

        final ConfigLoader<? extends T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + classType.getSimpleName());
        }

        final String problemDescription = loader.getProblemDescription();
        root.addProblem(problemDescription);

        try {
            return ConfigOptional.of(this, loader.loadFromSection(this));
        }
        catch (InvalidConfigurationException e) {
            return ConfigOptional.invalid(e);
        }
        finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.invalid(this, "Expected a value but found a section");
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return ConfigOptional.of(this);
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return ConfigOptional.invalid(this, "Expected a list but found a section");
    }

    @Override
    public String toString() {
        return fieldsByKey.toString();
    }

}
