package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public abstract class Sequence extends Branch implements ConfigSequence {

    private final List<@NotNull KeylessField> fields = new ArrayList<>();

    protected Sequence(@NotNull FlowStyle flowStyle) {
        super(flowStyle);
    }

    public @Nullable ConfigField getNode(int index) {
        if (index < size()) {
            return fields.get(index);
        }
        return null;
    }

    public void addNode(KeylessField node) {
        int index = node.getIndex();
        while (index >= size()) {
            add("");
        }
        fields.set(index, node);
    }

    @Override
    public Iterator<KeylessField> iterator() {
        return fields.iterator();
    }

    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public @NotNull Sequence convertToSequence() {
        return this;
    }

    @Override
    public <T> void map(T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.mapToSequence(this, value);
    }

    @Override
    public <T> void add(@NotNull T value) {
        Preconditions.checkNotNull(value, "Value cannot be null");

        final var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            fields.add(new KeylessScalar(this, size(), value));
            return;
        }

        @SuppressWarnings("unchecked") final var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        try {
            switch (mapper.getDefaultMappingType()) {
                case SCALAR -> {
                    final ConfigScalar scalar = addEmptyScalar();
                    if (mapper.clearExistingFields()) {
                        scalar.clear();
                    }
                    mapper.mapToScalar(scalar, value);
                }
                case SECTION -> {
                    final ConfigSection section = addEmptySection();
                    if (mapper.clearExistingFields()) {
                        section.clear();
                    }
                    mapper.mapToSection(section, value);
                }
                case SEQUENCE -> {
                    final ConfigSequence sequence = addEmptySequence();
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
        Preconditions.checkNotNull(elements, "Elements cannot be null");
        for (T value : elements) {
            add(value);
        }
    }

    @Override
    public @NotNull List<@NotNull Object> toList() {
        return stream().map(ConfigField::getValue).toList();
    }

    @Override
    public @NotNull Map<String, Object> toMap() {
        final Map<String, Object> map = new LinkedHashMap<>();

        for (int i = 0; i < size(); i++) {
            map.put(Integer.toString(i), fields.get(i).getValue());
        }

        return map;
    }

    @Override
    public Stream<KeylessField> stream() {
        return fields.stream();
    }

    @Override
    public Set<KeylessField> getNestedFields() {
        return new LinkedHashSet<>(fields);
    }

    @Override
    public boolean isSet(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        return index < size();
    }

    @Override
    public <T> void set(int index, @Nullable T value) {
        if (index > 0 && index < size()) {
            throw new IndexOutOfBoundsException(index);
        }

        if (value == null) {
            addNode(new KeylessScalar(this, size(), ""));
            return;
        }

        final var classType = value.getClass();
        if (YamlConfig.isScalar(classType)) {
            addNode(new KeylessScalar(this, size(), value));
            return;
        }

        @SuppressWarnings("unchecked") final var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        final ConfigField existingField = getField(index).orElse(null);
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
                    final ConfigScalar scalar = getScalarOrCreate(index);
                    if (mapper.clearExistingFields()) {
                        scalar.clear();
                    }
                    mapper.mapToScalar(scalar, value);
                }
                case SECTION -> {
                    final ConfigSection section = getSectionOrCreate(index);
                    if (mapper.clearExistingFields()) {
                        section.clear();
                    }
                    mapper.mapToSection(section, value);
                }
                case SEQUENCE -> {
                    final ConfigSequence sequence = getSequenceOrCreate(index);
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
    public void remove(int index) {
        fields.remove(index);
        for (int i = 0; i < size(); i++) {
            KeylessField field = fields.get(i);
            field.setIndex(i);
        }
    }

    @Override
    public @NotNull ConfigSection getSectionOrCreate(int index) {
        var optionalSection = getSection(index);
        if (optionalSection.isValid()) {
            return optionalSection.value();
        }

        KeylessSection section = new KeylessSection(this, index);
        addNode(section);
        return section;
    }

    @Override
    public @NotNull ConfigSequence getSequenceOrCreate(int index) {
        var optionalSequence = getSequence(index);
        if (optionalSequence.isValid()) {
            return optionalSequence.value();
        }

        KeylessSequence sequence = new KeylessSequence(this, index);
        addNode(sequence);
        return sequence;
    }

    @Override
    public @NotNull ConfigScalar getScalarOrCreate(int index) {
        var optionalScalar = getScalar(index);
        if (optionalScalar.isValid()) {
            return optionalScalar.value();
        }

        KeylessScalar scalar = new KeylessScalar(this, index, "");
        addNode(scalar);
        return scalar;
    }

    @Override
    public @NotNull ConfigSection addEmptySection() {
        KeylessSection childSection = new KeylessSection(this, size());
        fields.add(childSection);
        return childSection;
    }

    @Override
    public @NotNull ConfigSequence addEmptySequence() {
        KeylessSequence childSequence = new KeylessSequence(this, size());
        fields.add(childSequence);
        return childSequence;
    }

    @Override
    public @NotNull ConfigScalar addEmptyScalar() {
        KeylessScalar childScalar = new KeylessScalar(this, size(), "");
        fields.add(childScalar);
        return childScalar;
    }

    @Override
    public <T> @NotNull T getRequired(int index, Class<T> classType) throws InvalidConfigurationException {
        return get(index, classType).orThrow();
    }

    @Override
    public @NotNull ConfigField getRequiredField(int index) throws InvalidConfigurationException {
        return getField(index).orThrow();
    }

    @Override
    public @NotNull ConfigScalar getRequiredScalar(int index) throws InvalidConfigurationException {
        return getScalar(index).orThrow();
    }

    @Override
    public @NotNull ConfigSection getRequiredSection(int index) throws InvalidConfigurationException {
        return getSection(index).orThrow();
    }

    @Override
    public @NotNull ConfigSequence getRequiredSequence(int index) throws InvalidConfigurationException {
        return getSequence(index).orThrow();
    }

    @Override
    public @NotNull ConfigLine getRequiredLine(int index) throws InvalidConfigurationException {
        return getLine(index).orThrow();
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
    public <T> @NotNull ConfigOptional<T> get(int index, Class<T> classType) {
        return getField(index).flatMap(field -> field.load(classType));
    }

    @Override
    public ConfigOptional<ConfigField> getField(int index) {
        if (index >= fields.size()) {
            return ConfigOptional.notSet(this, index, "Field is not set");
        }
        return ConfigOptional.of(this, fields.get(index));
    }

    @Override
    public ConfigOptional<ConfigScalar> getScalar(int index) {
        return getField(index).flatMap(ConfigField::toScalar);
    }

    @Override
    public ConfigOptional<ConfigSection> getSection(int index) {
        return getField(index).flatMap(ConfigField::toSection);
    }

    @Override
    public ConfigOptional<ConfigSequence> getSequence(int index) {
        return getField(index).flatMap(ConfigField::toSequence);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(int index) {
        return getScalar(index).map(ConfigScalar::toLine);
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
    public ConfigOptional<String> getString(int index, StringFormatter formatter) {
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
    public List<ConfigField> toFieldList() {
        return new ArrayList<>(fields);
    }

    @Override
    public <T> ConfigList<T> toList(Class<T> classType) {
        return createList(field -> field.load(classType).orThrow());
    }

    @Override
    public ConfigList<ConfigScalar> toScalarList() {
        return createList(field -> field.toScalar().orThrow());
    }

    @Override
    public ConfigList<ConfigSection> toSectionList() {
        return createList(field -> field.toSection().orThrow());
    }

    @Override
    public ConfigList<ConfigSequence> toSequenceList() {
        return createList(field -> field.toSequence().orThrow());
    }

    @Override
    public ConfigList<Boolean> toBooleanList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toBoolean).orThrow());
    }

    @Override
    public ConfigList<Character> toCharacterList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toCharacter).orThrow());
    }

    @Override
    public ConfigList<String> toStringList() {
        return createList(field -> field.toScalar().map(ConfigScalar::toString).orThrow());
    }

    @Override
    public ConfigList<String> toStringList(StringFormatter formatter) {
        return createList(field -> field.toScalar().map(scalar -> scalar.toString(formatter)).orThrow());
    }

    @Override
    public ConfigList<Integer> toIntegerList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toInteger).orThrow());
    }

    @Override
    public ConfigList<Long> toLongList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toLong).orThrow());
    }

    @Override
    public ConfigList<Float> toFloatList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toFloat).orThrow());
    }

    @Override
    public ConfigList<Double> toDoubleList() {
        return createList(field -> field.toScalar().flatMap(ConfigScalar::toDouble).orThrow());
    }

    @FunctionalInterface
    private interface ListMapper<T> {
        T apply(ConfigField field) throws InvalidConfigurationException;
    }

    private <T> ConfigList<T> createList(ListMapper<T> mapper) {
        List<T> elements = new ArrayList<>();
        for (ConfigField field : this) {
            try {
                elements.add(mapper.apply(field));
            }
            catch (InvalidConfigurationException e) {
                return ConfigList.invalid(e);
            }
        }
        return ConfigList.of(this, elements);
    }

    @Override
    public void clear() {
        fields.clear();
    }

    @Override
    public @NotNull Object getValue() {
        return toList();
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return FieldType.SEQUENCE;
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
            return ConfigOptional.of(this, loader.loadFromSequence(this));
        } catch (InvalidConfigurationException e) {
            return ConfigOptional.invalid(e);
        } finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.invalid(this, "Expected a value but found a list");
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return ConfigOptional.invalid(this, "Expected a section but found a list");
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return ConfigOptional.of(this);
    }

    @Override
    public String toString() {
        return fields.toString();
    }

}
