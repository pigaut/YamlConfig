package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.path.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public abstract class Section extends Branch implements ConfigSection {

    private final Map<@NotNull String, @NotNull KeyedField> fieldsByKey = new LinkedHashMap<>();

    protected Section(FlowStyle flowStyle) {
        super(flowStyle);
    }

    public ConfigField getNode(String key) {
        return fieldsByKey.get(key);
    }

    public void addNode(KeyedField keyedField) {
        fieldsByKey.put(keyedField.getKey(), keyedField);
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
            createScalar(YamlConfig.generateRandomKey(), value);
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
            switch (mapper.getDefaultMappingType()) {
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
        final Map<String, Object> map = new LinkedHashMap<>();
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
    public Set<String> getKeys() {
        return new LinkedHashSet<>(fieldsByKey.keySet());
    }

    @Override
    public boolean contains(@NotNull String path) {
        return getField(path).isValid();
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return getScalar(path).isValid();
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return getSection(path).isValid();
    }

    @Override
    public boolean isSequence(@NotNull String path) {
        return getSequence(path).isValid();
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
    public @NotNull Section getSectionOrCreate(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch();
        }
        return currentBranch.convertToSection();
    }

    @Override
    public @NotNull Sequence getSequenceOrCreate(@NotNull String path) {
        PathIterator iterator = PathIterator.of(this, path);
        Branch currentBranch = this;
        while (iterator.hasNext()) {
            currentBranch = iterator.nextBranch();
        }
        return currentBranch.convertToSequence();
    }

    @Override
    public ConfigScalar getScalarOrCreate(@NotNull String path) {
        ConfigScalar scalar = getScalar(path).orElse(null);
        return scalar != null ? scalar : createScalar(path, "");
    }

    @Override
    public <T> ConfigList<T> getAll(@NotNull String path, @NotNull Class<T> classType) {
        return getBranch(path).mapToList(branch -> branch.getAll(classType));
    }

    @Override
    public <T> List<T> getAllRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigException {
        return getAll(path, classType).withDefault(List.of());
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
        return getField(path).flatMap(field -> field.get(classType));
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
    public <T> ConfigList<T> getList(@NotNull String path, Class<T> classType) {
        return getSequence(path).mapToList(sequence -> sequence.toList(classType));
    }

    @Override
    public ConfigList<ConfigField> getFieldList(@NotNull String path) {
        return getSequence(path).mapToList(sequence -> ConfigList.of(sequence, sequence.toFieldList()));
    }

    @Override
    public ConfigList<ConfigScalar> getScalarList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toScalarList);
    }

    @Override
    public ConfigList<ConfigSection> getSectionList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toSectionList);
    }

    @Override
    public ConfigList<Boolean> getBooleanList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toBooleanList);
    }

    @Override
    public ConfigList<Character> getCharacterList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toCharacterList);
    }

    @Override
    public ConfigList<String> getStringList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toStringList);
    }

    @Override
    public ConfigList<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) {
        return getSequence(path).mapToList(seq -> seq.toStringList(formatter));
    }

    @Override
    public ConfigList<Integer> getIntegerList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toIntegerList);
    }

    @Override
    public ConfigList<Long> getLongList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toLongList);
    }

    @Override
    public ConfigList<Float> getFloatList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toFloatList);
    }

    @Override
    public ConfigList<Double> getDoubleList(@NotNull String path) {
        return getSequence(path).mapToList(ConfigSequence::toDoubleList);
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
        } catch (InvalidConfigException e) {
            return ConfigOptional.invalid(e);
        } finally {
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
