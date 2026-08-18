package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.path.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public abstract class Section extends Branch implements ConfigSection {

    private final Map<String, KeyedField> fieldsByKey = new LinkedHashMap<>();

    protected Section(FlowStyle flowStyle) {
        super(flowStyle);
    }

    public KeyedField getNode(String key) {
        return fieldsByKey.get(key);
    }

    public void addNode(KeyedField keyedField) {
        fieldsByKey.put(keyedField.getKey(), keyedField);
    }

    public void removeNode(String key) {
        fieldsByKey.remove(key);
    }

    public void onKeyChanged(KeyedField field, String oldKey, String newKey) {
        if (fieldsByKey.get(oldKey) != field) {
            return;
        }
        fieldsByKey.remove(oldKey);
        fieldsByKey.put(newKey, field);
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
    public @NotNull Section convertToSection() {
        return this;
    }

    @Override
    public <T> void map(@NotNull T value) {
        Configurator configurator = getRoot().getConfigurator();
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
        var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            createScalar(YamlConfig.generateRandomKey(), value, true);
            return;
        }

        @SuppressWarnings("unchecked")
        var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        String key = mapper.createKey(value);
        ConfigField existingField = getField(key).orElse(null);
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
            switch (mapper.getDefaultMappingType(value)) {
                case SCALAR -> {
                    ConfigScalar scalar = getScalarOrCreate(key);
                    if (mapper.clearExistingFields()) {
                        scalar.clear();
                    }
                    mapper.mapToScalar(scalar, value);
                }
                case SECTION -> {
                    ConfigSection section = getSectionOrCreate(key);
                    if (mapper.clearExistingFields()) {
                        section.clear();
                    }
                    mapper.mapToSection(section, value);
                }
                case SEQUENCE -> {
                    ConfigSequence sequence = getSequenceOrCreate(key);
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
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, KeyedField> entry : fieldsByKey.entrySet()) {
            ConfigField field = entry.getValue();
            map.put(entry.getKey(), field.getValue());
        }
        return map;
    }

    @Override
    public Stream<KeyedField> stream() {
        return fieldsByKey.values().stream();
    }

    @Override
    public Set<KeyedField> getNestedFields() {
        return new LinkedHashSet<>(fieldsByKey.values());
    }

    @Override
    public Set<KeyedScalar> getNestedScalars() {
        Set<KeyedScalar> nestedScalars = new LinkedHashSet<>();
        for (KeyedField field : this) {
            if (field instanceof KeyedScalar keyedScalar) {
                nestedScalars.add(keyedScalar);
            }
        }
        return nestedScalars;
    }

    @Override
    public Set<KeyedSection> getNestedSections() {
        Set<KeyedSection> nestedSections = new LinkedHashSet<>();
        for (KeyedField field : this) {
            if (field instanceof KeyedSection keyedSection) {
                nestedSections.add(keyedSection);
            }
        }
        return nestedSections;
    }

    @Override
    public Set<KeyedSequence> getNestedSequences() {
        Set<KeyedSequence> nestedSequences = new LinkedHashSet<>();
        for (KeyedField field : this) {
            if (field instanceof KeyedSequence keyedSequence) {
                nestedSequences.add(keyedSequence);
            }
        }
        return nestedSequences;
    }

    @Override
    public Set<? extends ConfigSequence> getNestedSequences(@NotNull String path) {
        ConfigBranch branch = getBranch(path).orElse(null);
        return branch != null ? branch.getNestedSequences() : Set.of();
    }

    @Override
    public Set<? extends ConfigSection> getNestedSections(@NotNull String path) {
        ConfigBranch branch = getBranch(path).orElse(null);
        return branch != null ? branch.getNestedSections() : Set.of();
    }

    @Override
    public Set<? extends ConfigScalar> getNestedScalars(@NotNull String path) {
        ConfigBranch branch = getBranch(path).orElse(null);
        return branch != null ? branch.getNestedScalars() : Set.of();
    }

    @Override
    public Set<? extends ConfigField> getNestedFields(@NotNull String path) {
        ConfigBranch branch = getBranch(path).orElse(null);
        return branch != null ? branch.getNestedFields() : Set.of();
    }

    @Override
    public @NotNull Set<String> getKeys() {
        return new LinkedHashSet<>(fieldsByKey.keySet());
    }

    @Override
    public boolean isSet(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }
        return field != null;
    }

    @Override
    public boolean isScalar(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }
        return field instanceof ConfigScalar;
    }

    @Override
    public boolean isSection(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }
        return field instanceof ConfigSection;
    }

    @Override
    public boolean isSequence(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }
        return field instanceof ConfigSequence;
    }

    @Override
    public <T> void set(@NotNull String path, @Nullable T value) {
        if (value == null) {
            createScalar(path, "", true);
            return;
        }

        final var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            createScalar(path, value, true);
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
            switch (mapper.getDefaultMappingType(value)) {
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
        PathIterator iterator = PathIterator.of(this, path);
        while (iterator.hasNext()) {
            if (iterator.isLast()) {
                Branch currentBranch = iterator.getCurrentBranch();
                FieldKey lastKey = iterator.getLastKey();
                lastKey.remove(currentBranch);
                break;
            }
            iterator.nextBranch(true);
        }
    }

    @Override
    public void addDefaults(@NotNull ConfigSection defaultSection) {
        for (KeyedField field : defaultSection.getNestedFields()) {
            KeyedField existingField = getNode(field.getKey());
            if (existingField != null) {
                existingField.setBlockComments(field.getBlockComments());
                existingField.setInLineComments(field.getInLineComments());
                continue;
            }
            addNode(field);
        }
    }

    @Override
    public void reorderFields(@NotNull List<String> keysOrder) {
        LinkedHashMap<String, KeyedField> newMap = new LinkedHashMap<>();

        for (String key : keysOrder) {
            if (fieldsByKey.containsKey(key)) {
                newMap.put(key, fieldsByKey.get(key));
            }
        }
        newMap.putAll(fieldsByKey);

        fieldsByKey.clear();
        fieldsByKey.putAll(newMap);
    }

    @Override
    public void replaceAll(@NotNull CharSequence target, @NotNull CharSequence replacement) {
        for (ConfigField field : getNestedFields()) {
            field.replaceAll(target, replacement);
        }
    }

    @Override
    public void replaceAll(@NotNull Pattern pattern, @NotNull Map<String, String> replacements) {
        for (ConfigField field : getNestedFields()) {
            field.replaceAll(pattern, replacements);
        }
    }

    @Override
    public @NotNull Section getSectionOrCreate(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch(true);
        }
        return currentBranch.convertToSection();
    }

    @Override
    public @NotNull Sequence getSequenceOrCreate(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch(true);
        }
        return currentBranch.convertToSequence();
    }

    @Override
    public ConfigScalar getScalarOrCreate(@NotNull String path) {
        ConfigScalar scalar = getScalar(path).orElse(null);
        return scalar != null ? scalar : createScalar(path, "", true);
    }

    @Override
    public ConfigScalar getScalarOrEmpty(@NotNull String path) {
        ConfigScalar scalar = getScalar(path).orElse(null);
        return scalar != null ? scalar : createScalar(path, "", false);
    }

    @Override
    public ConfigLine getLineOrEmpty(@NotNull String path) {
        ConfigScalar scalar = getScalarOrEmpty(path);
        return scalar.toLine();
    }

    @Override
    public ConfigLine getLineOrEmpty(@NotNull String path, @NotNull LineStyle lineStyle) {
        ConfigScalar scalar = getScalarOrEmpty(path);
        return scalar.toLine(lineStyle);
    }

    @Override
    public ConfigSection getSectionOrEmpty(@NotNull String path) {
        ConfigSection section = getSection(path).orElse(null);
        return section != null ? section : createSection(path, false);
    }

    @Override
    public ConfigSequence getSequenceOrEmpty(@NotNull String path) {
        ConfigSequence sequence = getSequence(path).orElse(null);
        return sequence != null ? sequence : createSequence(path, false);
    }

    @Override
    public <T> ConfigList<T> getAll(@NotNull String path, @NotNull Class<T> classType) {
        return getBranch(path).mapToListIfValid(branch -> branch.getAll(classType));
    }

    @Override
    public <T> List<T> getAllRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigException {
        return getAll(path, classType).withDefaultOrThrow(List.of());
    }

    @Override
    public <T> @NotNull T getRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigException {
        return get(path, classType).orThrow();
    }

    @Override
    public @NotNull ConfigField getRequiredField(@NotNull String path) throws InvalidConfigException {
        return getField(path).orThrow();
    }

    @Override
    public @NotNull ConfigScalar getRequiredScalar(@NotNull String path) throws InvalidConfigException {
        return getScalar(path).orThrow();
    }

    @Override
    public @NotNull ConfigSection getRequiredSection(@NotNull String path) throws InvalidConfigException {
        return getSection(path).orThrow();
    }

    @Override
    public @NotNull ConfigSequence getRequiredSequence(@NotNull String path) throws InvalidConfigException {
        return getSequence(path).orThrow();
    }

    @Override
    public @NotNull ConfigLine getRequiredLine(@NotNull String path) throws InvalidConfigException {
        return getLine(path).orThrow();
    }

    @Override
    public @NotNull ConfigLine getRequiredLine(@NotNull String path, @NotNull LineStyle lineStyle) throws InvalidConfigException {
        return getLine(path, lineStyle).orThrow();
    }

    @Override
    public @NotNull ConfigLine getRequiredLine(@NotNull String path, @NotNull LineStyle lineStyle, @NotNull String format) throws InvalidConfigException {
        return getLine(path, lineStyle, format).orThrow();
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(@NotNull String path) throws InvalidConfigException {
        return getBoolean(path).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(@NotNull String path) throws InvalidConfigException {
        return getCharacter(path).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String path) throws InvalidConfigException {
        return getString(path).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigException {
        return getString(path, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(@NotNull String path) throws InvalidConfigException {
        return getInteger(path).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(@NotNull String path) throws InvalidConfigException {
        return getLong(path).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(@NotNull String path) throws InvalidConfigException {
        return getFloat(path).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(@NotNull String path) throws InvalidConfigException {
        return getDouble(path).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> get(@NotNull String path, @NotNull Class<T> classType) {
        return getField(path).flatMapIfValid(field -> field.get(classType));
    }

    public ConfigOptional<ConfigField> getField(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);

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
        return getField(path).flatMapIfValid(ConfigField::asScalar);
    }

    @Override
    public ConfigOptional<ConfigSection> getSection(@NotNull String path) {
        return getField(path).flatMapIfValid(ConfigField::asSection);
    }

    @Override
    public ConfigOptional<ConfigSequence> getSequence(@NotNull String path) {
        return getField(path).flatMapIfValid(ConfigField::asSequence);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(@NotNull String path) {
        return getScalar(path).mapIfValid(ConfigScalar::toLine);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(@NotNull String path, @NotNull LineStyle lineStyle) {
        return getScalar(path).mapIfValid(scalar -> scalar.toLine(lineStyle));
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(@NotNull String path, @NotNull LineStyle lineStyle, @NotNull String format) {
        return getScalar(path).flatMapIfValid(scalar -> scalar.toLine(lineStyle, format));
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toCharacter);
    }

    @Override
    public @NotNull ConfigOptional<String> getString(@NotNull String path) {
        return getScalar(path).mapIfValid(ConfigScalar::toString);
    }

    @Override
    public @NotNull ConfigOptional<String> getString(@NotNull String path, @NotNull StringFormatter formatter) {
        return getScalar(path).mapIfValid(scalar -> formatter.format(scalar.toString()));
    }

    @Override
    public ConfigOptional<Integer> getInteger(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(@NotNull String path) {
        return getScalar(path).flatMapIfValid(ConfigScalar::toDouble);
    }

    @Override
    public <T> ConfigList<T> getList(@NotNull String path, Class<T> classType) {
        return getSequence(path).mapToListIfValid(sequence -> sequence.toList(classType));
    }

    @Override
    public ConfigList<ConfigField> getFieldList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(sequence -> ConfigList.of(sequence, sequence.toFieldList()));
    }

    @Override
    public ConfigList<ConfigScalar> getScalarList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toScalarList);
    }

    @Override
    public ConfigList<ConfigSection> getSectionList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toSectionList);
    }

    @Override
    public ConfigList<Boolean> getBooleanList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toBooleanList);
    }

    @Override
    public ConfigList<Character> getCharacterList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toCharacterList);
    }

    @Override
    public ConfigList<String> getStringList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toStringList);
    }

    @Override
    public ConfigList<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) {
        return getSequence(path).mapToListIfValid(seq -> seq.toStringList(formatter));
    }

    @Override
    public ConfigList<Integer> getIntegerList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toIntegerList);
    }

    @Override
    public ConfigList<Long> getLongList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toLongList);
    }

    @Override
    public ConfigList<Float> getFloatList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toFloatList);
    }

    @Override
    public ConfigList<Double> getDoubleList(@NotNull String path) {
        return getSequence(path).mapToListIfValid(ConfigSequence::toDoubleList);
    }

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    @Override
    public String[][] getStringMatrix(@NotNull String path, int rows, int columns) {
        String[][] matrix = new String[rows][columns];

        ConfigSequence rowSequence = getSequence(path).orElse(null);
        if (rowSequence == null) {
            return matrix;
        }

        List<ConfigField> rowList = rowSequence.toFieldList();
        for (int i = 0; i < rows; i++) {
            if (i < rowList.size()) {
                ConfigSequence columnSequence = null;

                ConfigField field = rowList.get(i);
                if (field instanceof ConfigSequence sequence) {
                    columnSequence = sequence;
                } else if (field instanceof ConfigScalar scalar) {
                    columnSequence = scalar.split(WHITESPACE_PATTERN);
                }

                List<String> columnList = columnSequence != null ?
                        columnSequence.toStringList().orElse(List.of()) : List.of();

                for (int j = 0; j < columns; j++) {
                    if (j < columnList.size()) {
                        matrix[i][j] = columnList.get(j);
                    } else {
                        matrix[i][j] = "";
                    }
                }
            } else {
                Arrays.fill(matrix[i], "");
            }
        }

        return matrix;
    }

    @Override
    public @Nullable Object getValue(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);

        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }

        return field != null ? field.getValue() : null;
    }

    private ConfigOptional<ConfigBranch> getBranch(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);

        ConfigField field = null;
        while (iterator.hasNext()) {
            field = iterator.next();
        }

        if (field instanceof ConfigBranch branch) {
            return ConfigOptional.of(branch);
        }

        return ConfigOptional.notSet(this, path, "Field is not set");
    }

    private Scalar createScalar(@NotNull String path, @NotNull Object value, boolean attached) {
        PathIterator pathIterator = PathIterator.of(this, path);
        while (pathIterator.hasNext()) {
            if (pathIterator.isLast()) {
                Branch currentBranch = pathIterator.getCurrentBranch();
                FieldKey lastKey = pathIterator.getLastKey();
                return lastKey.createScalar(currentBranch, value, attached);
            }
            pathIterator.nextBranch(attached);
        }
        throw new AssertionError();
    }

    private Section createSection(@NotNull String path, boolean attached) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch(attached);
        }
        return currentBranch.convertToSection();
    }

    private Sequence createSequence(@NotNull String path, boolean attached) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch(attached);
        }
        return currentBranch.convertToSequence();
    }

    @Override
    public Iterator<KeyedField> iterator() {
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
    public <T> ConfigOptional<T> get(@NotNull Class<T> classType) {
        ConfigRoot root = getRoot();
        Configurator configurator = root.getConfigurator();

        ConfigLoader<T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + classType.getSimpleName());
        }

        try (var scope = new LoaderScope(root, loader)) {
            return ConfigOptional.of(this, loader.loadFromSection(this));
        } catch (InvalidConfigException e) {
            return ConfigOptional.invalid(e);
        }
    }

    @Override
    public ConfigOptional<ConfigScalar> asScalar() {
        return ConfigOptional.invalid(this, "Expected a value but found a section");
    }

    @Override
    public ConfigOptional<ConfigSection> asSection() {
        return ConfigOptional.of(this);
    }

    @Override
    public ConfigOptional<ConfigSequence> asSequence() {
        return ConfigOptional.invalid(this, "Expected a list but found a section");
    }

    @Override
    public @NotNull RootSection copy() {
        ConfigRoot root = getRoot();
        RootSection section = new RootSection(null, root.getConfigurator(), root.getPrefix());
        section.map(toMap());
        return section;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        List<CommentLine> blockComments = getBlockComments();
        if (!blockComments.isEmpty()) {
            builder.append("BlockComments=").append(blockComments).append(", ");
        }

        builder.append("Fields=").append(fieldsByKey);

        List<CommentLine> inlineComments = getInLineComments();
        if (!inlineComments.isEmpty()) {
            builder.append(", InlineComments=").append(inlineComments);
        }

        return builder.toString();
    }

}
