package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.FieldType;
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
    public boolean isSet(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        return index < size();
    }

    @Override
    public Stream<ConfigField> stream() {
        return fields.stream();
    }

    @Override
    public <T> void map(T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getMapper(value.getClass());
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

        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getMapper(classType);

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
    public <T> void set(int index, @Nullable T value) {
        if (index > 0 && index < size()) {
            throw new IndexOutOfBoundsException(index);
        }
        if (value == null) {
            setScalar(index, "");
            return;
        }
        if (YamlConfig.isScalar(value)) {
            setScalar(index, value);
            return;
        }

        final Configurator configurator = getRoot().getConfigurator();

        @SuppressWarnings("unchecked") final ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) configurator.getMapper(value.getClass());

        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class type: " + value.getClass().getSimpleName());
        }

        final ConfigField existingField = getField(index).orElse(null);
        final FieldType defaultMappingType = mapper.getDefaultMappingType();

        if (existingField != null) {
            switch (existingField.getFieldType()) {
                case SCALAR -> {
                    try {
                        final Object scalarToSet = mapper.createScalar(value);
                        setScalar(index, scalarToSet);
                        return;
                    } catch (UnsupportedOperationException e) {
                        if (defaultMappingType == FieldType.SCALAR) {
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
                        if (defaultMappingType == FieldType.SECTION) {
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
                        if (defaultMappingType == FieldType.SEQUENCE) {
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
    public <T> ConfigOptional<T> get(int index, Class<T> classType) {
        return getField(index).flatMap(field -> field.load(classType));
    }

    @Override
    public ConfigOptional<ConfigField> getField(int index) {
        if (index >= fields.size()) {
            return ConfigOptional.empty(new InvalidConfigurationException(this, index, "Field is not set"));
        }
        return ConfigOptional.of(fields.get(index));
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
    public ConfigSection getSectionOrCreate(int index) {
        ConfigOptional<ConfigSection> optionalSection = getSection(index);

        if (optionalSection.isSet()) {
            return optionalSection.value();
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
    public ConfigOptional<ConfigSequence> getSequence(int index) {
        return getField(index).flatMap(ConfigField::toSequence);
    }

    @Override
    public ConfigSequence getSequenceOrCreate(int index) {
        ConfigOptional<ConfigSequence> optionalSequence = getSequence(index);

        if (optionalSequence.isSet()) {
            return optionalSequence.value();
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
    public List<ConfigScalar> toScalarList() throws InvalidConfigurationException {
        return stream().map(field -> field.toScalar().orElseThrow()).toList();
    }

    @Override
    public List<ConfigSection> toSectionList() throws InvalidConfigurationException {
        return stream().map(field -> field.toSection().orElseThrow()).toList();
    }

    @Override
    public List<ConfigSequence> toSequenceList() throws InvalidConfigurationException {
        return stream().map(field -> field.toSequence().orElseThrow()).toList();
    }

    @Override
    public List<Boolean> toBooleanList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toBoolean().orElseThrow())
                .toList();
    }

    @Override
    public List<Character> toCharacterList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toCharacter().orElseThrow())
                .toList();
    }

    @Override
    public List<String> toStringList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(ConfigScalar::toString)
                .toList();
    }

    @Override
    public List<String> toStringList(StringFormatter formatter) throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toString(formatter))
                .toList();
    }

    @Override
    public List<Integer> toIntegerList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toInteger().orElseThrow())
                .toList();
    }

    @Override
    public List<Long> toLongList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toLong().orElseThrow())
                .toList();
    }

    @Override
    public List<Float> toFloatList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toFloat().orElseThrow())
                .toList();
    }

    @Override
    public List<Double> toDoubleList() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toScalar().orElseThrow())
                .map(scalar -> scalar.toDouble().orElseThrow())
                .toList();
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
    public void clear() {
        fields.clear();
    }

    @Override
    public <T> ConfigOptional<T> load(@NotNull Class<T> type) {
        final ConfigRoot root = getRoot();
        final Configurator configurator = root.getConfigurator();

        final ConfigLoader<? extends T> loader = configurator.getLoader(type);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + type.getSimpleName());
        }

        final String problemDescription = loader.getProblemDescription();
        root.addProblem(problemDescription);

        try {
            return ConfigOptional.of(this, loader.loadFromSequence(this));
        }
        catch (InvalidConfigurationException e) {
            return ConfigOptional.empty(e);
        }
        finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.empty(this, "Expected a value but found a list");
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return ConfigOptional.empty(this, "Expected a section but found a list");
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return ConfigOptional.of(this);
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
