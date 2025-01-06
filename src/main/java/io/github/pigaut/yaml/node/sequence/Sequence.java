package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.parser.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public abstract class Sequence extends Branch implements ConfigSequence {

    private final List<@NotNull ConfigField> fields = new ArrayList<>();

    protected Sequence(@NotNull FlowStyle flowStyle) {
        super(flowStyle);
    }

    public ConfigField getNode(int index) {
        if (index < size())
            return fields.get(index);
        return null;
    }

    public void setNode(int index, Field node) {
        while (index >= size()) {
            add("");
        }
        fields.set(index, node);
    }

    @NotNull
    @Override
    public Iterator<ConfigField> iterator() {
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
    public Stream<ConfigField> stream() {
        return fields.stream();
    }

    @Override
    public <T> void map(T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.mapSequence(this, value);
    }

    @Override
    public <T> void add(T value) {
        final Class<?> classType = value.getClass();

        if (YamlConfig.SCALARS.contains(classType)) {
            fields.add(new KeylessScalar(this, size(), value));
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();

        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(classType);

        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class type: " + classType.getSimpleName());
        }

        switch (mapper.getDefaultMappingType()) {
            case SCALAR -> {
                final Object scalarToSet = mapper.createScalar(value);
                addScalar(scalarToSet);
            }
            case SECTION -> {
                final ConfigSection sectionToMap = addSection();
                mapper.mapSection(sectionToMap, value);
            }
            case SEQUENCE -> {
                final ConfigSequence sequenceToMap = addSequence();
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

    private void setScalar(int index, Object value) {
        fields.set(index, new KeylessScalar(this, size(), value));
    }

    private void addScalar(Object value) {
        fields.add(new KeylessScalar(this, size(), value));
    }

    @Override
    public <T> void set(int index, T value) {
        if (index > 0 && index < size()) {
            throw new IndexOutOfBoundsException(index);
        }

        if (YamlConfig.isScalar(value)) {
            setScalar(index, value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();

        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getConfigMapper(value.getClass());

        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class type: " + value.getClass().getSimpleName());
        }

        final ConfigField existingField = getField(index);
        final MappingType defaultMappingType = mapper.getDefaultMappingType();

        if (existingField != null) {
            switch (existingField.getFieldType()) {
                case SCALAR -> {
                    try {
                        final Object scalarToSet = mapper.createScalar(value);
                        setScalar(index, scalarToSet);
                        return;
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SCALAR) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
                case SECTION -> {
                    try {
                        final ConfigSection sectionToMap = getSectionOrCreate(index);
                        if (!mapper.keepExistingFields()) {
                            sectionToMap.clear();
                        }
                        mapper.mapSection(sectionToMap, value);
                        return;
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SECTION) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
                case SEQUENCE -> {
                    try {
                        final ConfigSequence sequenceToMap = getSequenceOrCreate(index);
                        if (!mapper.keepExistingFields()) {
                            sequenceToMap.clear();
                        }
                        mapper.mapSequence(sequenceToMap, value);
                        return;
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == MappingType.SEQUENCE) {
                            throw new IllegalStateException(mapper.getClass() + " does not override default mapping method");
                        }
                    }
                }
            }
        }

        switch (defaultMappingType) {
            case SCALAR -> {
                final Object scalarToSet = mapper.createScalar(value);
                setScalar(index, scalarToSet);
            }
            case SECTION -> {
                final ConfigSection sectionToMap = getSectionOrCreate(index);
                if (!mapper.keepExistingFields()) {
                    sectionToMap.clear();
                }
                mapper.mapSection(sectionToMap, value);
            }
            case SEQUENCE -> {
                final ConfigSequence sequenceToMap = getSequenceOrCreate(index);
                if (!mapper.keepExistingFields()) {
                    sequenceToMap.clear();
                }
                mapper.mapSequence(sequenceToMap, value);
            }
        }

    }

    @Override
    public void remove(int index) {
        fields.remove(index);
        for (int i = 0; i < size(); i++) {
            if (fields.get(i) instanceof KeylessField keylessField) {
                keylessField.setIndex(i);
            }
        }
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        final ConfigField field = getField(index);
        return field.load(type);
    }

    @Override
    public <T> Optional<T> getOptional(int index, Class<T> type) {
        return ConfigOptional.of(() -> get(index, type));
    }

    @Override
    public ConfigField getField(int index) {
        return fields.get(index);
    }

    @Override
    public ConfigScalar getScalar(int index) {
        final ConfigField field = getField(index);
        return field.toScalar();
    }

    @Override
    public Optional<ConfigScalar> getOptionalScalar(int index) {
        return ConfigOptional.of(() -> getScalar(index));
    }

    @Override
    public ConfigSection getSection(int index) {
        final ConfigField field = getField(index);
        return field.toSection();
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(int index) {
        return ConfigOptional.of(() -> getSection(index));
    }

    @Override
    public ConfigSection getSectionOrCreate(int index) {
        final ConfigField field = getField(index);

        if (field.getFieldType() == MappingType.SECTION) {
            return field.toSection();
        }

        final ConfigSection section = new KeylessSection(this, index);
        fields.set(index, section);

        return section;
    }

    @Override
    public ConfigSection addSection() {
        final Section childSection = new KeylessSection(this, size());
        fields.add(childSection);
        return childSection;
    }

    @Override
    public ConfigSequence getSequence(int index) {
        final ConfigField field = getField(index);
        return field.toSequence();
    }

    @Override
    public Optional<ConfigSequence> getOptionalSequence(int index) {
        return ConfigOptional.of(() -> getSequence(index));
    }

    @Override
    public ConfigSequence getSequenceOrCreate(int index) {
        final ConfigField field = getField(index);

        if (field.getFieldType() == MappingType.SEQUENCE) {
            return field.toSequence();
        }

        final ConfigSequence sequence = new KeylessSequence(this, index);
        fields.set(index, sequence);

        return sequence;
    }

    @Override
    public ConfigSequence addSequence() {
        final Sequence childSequence = new KeylessSequence(this, size());
        fields.add(childSequence);
        return childSequence;
    }

    @Override
    public boolean getBoolean(int index) throws InvalidConfigurationException {
        return getOptionalBoolean(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "expected a boolean but received invalid type"));
    }

    @Override
    public char getCharacter(int index) throws InvalidConfigurationException {
        return getOptionalCharacter(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not a character"));
    }

    @Override
    public @NotNull String getString(int index) throws InvalidConfigurationException {
        return getOptionalString(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not a string"));
    }

    @Override
    public int getInteger(int index) throws InvalidConfigurationException {
        return getOptionalInteger(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not an integer"));
    }

    @Override
    public long getLong(int index) throws InvalidConfigurationException {
        return getOptionalLong(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not a long"));
    }

    @Override
    public float getFloat(int index) throws InvalidConfigurationException {
        return getOptionalFloat(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not a float"));
    }

    @Override
    public double getDouble(int index) throws InvalidConfigurationException {
        return getOptionalDouble(index)
                .orElseThrow(() -> new InvalidConfigurationException(this, index, "is not a double"));
    }

    @Override
    public Optional<Boolean> getOptionalBoolean(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asBoolean);
    }

    @Override
    public Optional<Character> getOptionalCharacter(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asCharacter);
    }

    @Override
    public Optional<String> getOptionalString(int index) {
        return getOptionalScalar(index)
                .map(ConfigScalar::toString);
    }

    @Override
    public Optional<Integer> getOptionalInteger(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asInteger);
    }

    @Override
    public Optional<Long> getOptionalLong(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asLong);
    }

    @Override
    public Optional<Float> getOptionalFloat(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asFloat);
    }

    @Override
    public Optional<Double> getOptionalDouble(int index) {
        return getOptionalScalar(index)
                .flatMap(ConfigScalar::asDouble);
    }

    @Override
    public @NotNull <T> List<@NotNull T> toList(Class<T> type) {
        return fields.stream()
                .map(field -> field.load(type))
                .toList();
    }

    @Override
    public List<ConfigField> toFieldList() {
        return new ArrayList<>(fields);
    }

    @Override
    public List<ConfigScalar> toScalarList() throws InvalidConfigurationException {
        return fields.stream().map(ConfigField::toScalar).toList();
    }

    @Override
    public List<ConfigSection> toSectionList() throws InvalidConfigurationException {
        return fields.stream().map(ConfigField::toSection).toList();
    }

    @Override
    public List<ConfigSequence> toSequenceList() throws InvalidConfigurationException {
        return fields.stream().map(ConfigField::toSequence).toList();
    }

    @Override
    public List<Boolean> toBooleanList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toBoolean)
                .toList();
    }

    @Override
    public List<Character> toCharacterList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toCharacter)
                .toList();
    }

    @Override
    public List<String> toStringList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toString)
                .toList();
    }

    @Override
    public List<String> toStringList(StringFormatter formatter) throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toString)
                .map(formatter::format)
                .toList();
    }

    @Override
    public List<Integer> toIntegerList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toInteger)
                .toList();
    }

    @Override
    public List<Long> toLongList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toLong)
                .toList();
    }

    @Override
    public List<Float> toFloatList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toFloat)
                .toList();
    }

    @Override
    public List<Double> toDoubleList() throws InvalidConfigurationException {
        return fields.stream()
                .map(ConfigField::toScalar)
                .map(ConfigScalar::toDouble)
                .toList();
    }

    @Override
    public @NotNull Object getValue() {
        return toList();
    }

    @Override
    public @NotNull MappingType getFieldType() {
        return MappingType.SEQUENCE;
    }

    @Override
    public void clear() {
        fields.clear();
    }

    @Override
    public <T> @NotNull T load(@NotNull Class<T> type) throws InvalidConfigurationException {
        final Configurator configurator = getRoot().getConfigurator();
        ConfigLoader<? extends T> loader = configurator.getLoader(type, this);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + type.getSimpleName());
        }
        this.setProblemDescription(loader.getProblemDescription());
        final T value = loader.loadFromSequence(this);
        this.setProblemDescription(null);
        return value;
    }

    @Override
    public @NotNull ConfigScalar toScalar() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a value but found a list");
    }

    @Override
    public @NotNull ConfigSection toSection() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a section but found a list");
    }

    @Override
    public @NotNull ConfigSequence toSequence() throws InvalidConfigurationException {
        return this;
    }

    @Override
    public @NotNull Sequence convertToSequence() {
        return this;
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
    public String toString() {
        return fields.toString();
    }

}
