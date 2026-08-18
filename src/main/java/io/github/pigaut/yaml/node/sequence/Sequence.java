package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.regex.*;
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

        var classType = value.getClass();
        if (YamlConfig.isScalarType(classType)) {
            fields.add(new KeylessScalar(this, size(), value));
            return;
        }

        @SuppressWarnings("unchecked")
        var mapper = (ConfigMapper<? super T>) getRoot().getConfigurator().getMapper(classType);
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for value of class type: " + classType.getSimpleName());
        }

        try {
            switch (mapper.getDefaultMappingType(value)) {
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
    public Set<KeylessScalar> getNestedScalars() {
        Set<KeylessScalar> nestedScalars = new LinkedHashSet<>();
        for (KeylessField field : this) {
            if (field instanceof KeylessScalar keylessScalar) {
                nestedScalars.add(keylessScalar);
            }
        }
        return nestedScalars;
    }

    @Override
    public Set<KeylessSection> getNestedSections() {
        Set<KeylessSection> nestedSections = new LinkedHashSet<>();
        for (KeylessField field : this) {
            if (field instanceof KeylessSection keylessSection) {
                nestedSections.add(keylessSection);
            }
        }
        return nestedSections;
    }

    @Override
    public Set<KeylessSequence> getNestedSequences() {
        Set<KeylessSequence> nestedSequence = new LinkedHashSet<>();
        for (KeylessField field : this) {
            if (field instanceof KeylessSequence keylessSequence) {
                nestedSequence.add(keylessSequence);
            }
        }
        return nestedSequence;
    }

    @Override
    public boolean isSet(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        return index < size();
    }

    @Override
    public boolean isScalar(int index) {
        if (!isSet(index)) {
            return false;
        }
        KeylessField field = fields.get(index);
        return field instanceof ConfigScalar;
    }

    @Override
    public boolean isSection(int index) {
        if (!isSet(index)) {
            return false;
        }
        KeylessField field = fields.get(index);
        return field instanceof ConfigSection;
    }

    @Override
    public boolean isSequence(int index) {
        if (!isSet(index)) {
            return false;
        }
        KeylessField field = fields.get(index);
        return field instanceof ConfigSequence;
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
            switch (mapper.getDefaultMappingType(value)) {
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
    public <T> @NotNull T getRequired(int index, Class<T> classType) throws InvalidConfigException {
        return get(index, classType).orThrow();
    }

    @Override
    public @NotNull ConfigField getRequiredField(int index) throws InvalidConfigException {
        return getField(index).orThrow();
    }

    @Override
    public @NotNull ConfigScalar getRequiredScalar(int index) throws InvalidConfigException {
        return getScalar(index).orThrow();
    }

    @Override
    public @NotNull ConfigSection getRequiredSection(int index) throws InvalidConfigException {
        return getSection(index).orThrow();
    }

    @Override
    public @NotNull ConfigSequence getRequiredSequence(int index) throws InvalidConfigException {
        return getSequence(index).orThrow();
    }

    @Override
    public @NotNull ConfigLine getRequiredLine(int index) throws InvalidConfigException {
        return getLine(index).orThrow();
    }

    @Override
    public @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigException {
        return getBoolean(index).orThrow();
    }

    @Override
    public @NotNull Character getRequiredCharacter(int index) throws InvalidConfigException {
        return getCharacter(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index) throws InvalidConfigException {
        return getString(index).orThrow();
    }

    @Override
    public @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigException {
        return getString(index, formatter).orThrow();
    }

    @Override
    public @NotNull Integer getRequiredInteger(int index) throws InvalidConfigException {
        return getInteger(index).orThrow();
    }

    @Override
    public @NotNull Long getRequiredLong(int index) throws InvalidConfigException {
        return getLong(index).orThrow();
    }

    @Override
    public @NotNull Float getRequiredFloat(int index) throws InvalidConfigException {
        return getFloat(index).orThrow();
    }

    @Override
    public @NotNull Double getRequiredDouble(int index) throws InvalidConfigException {
        return getDouble(index).orThrow();
    }

    @Override
    public <T> @NotNull ConfigOptional<T> get(int index, Class<T> classType) {
        return getField(index).flatMapIfValid(field -> field.get(classType));
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
        return getField(index).flatMapIfValid(ConfigField::asScalar);
    }

    @Override
    public ConfigOptional<ConfigSection> getSection(int index) {
        return getField(index).flatMapIfValid(ConfigField::asSection);
    }

    @Override
    public ConfigOptional<ConfigSequence> getSequence(int index) {
        return getField(index).flatMapIfValid(ConfigField::asSequence);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(int index) {
        return getScalar(index).mapIfValid(ConfigScalar::toLine);
    }

    @Override
    public ConfigOptional<ConfigLine> getLine(int index, @NotNull LineStyle lineStyle) {
        return getScalar(index).mapIfValid(scalar -> scalar.toLine(lineStyle));
    }

    @Override
    public ConfigOptional<Boolean> getBoolean(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toBoolean);
    }

    @Override
    public ConfigOptional<Character> getCharacter(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toCharacter);
    }

    @Override
    public ConfigOptional<String> getString(int index) {
        return getScalar(index).mapIfValid(ConfigScalar::toString);
    }

    @Override
    public ConfigOptional<String> getString(int index, StringFormatter formatter) {
        return getScalar(index).mapIfValid(scalar -> scalar.toString(formatter));
    }

    @Override
    public ConfigOptional<Integer> getInteger(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toInteger);
    }

    @Override
    public ConfigOptional<Long> getLong(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toLong);
    }

    @Override
    public ConfigOptional<Float> getFloat(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toFloat);
    }

    @Override
    public ConfigOptional<Double> getDouble(int index) {
        return getScalar(index).flatMapIfValid(ConfigScalar::toDouble);
    }

    @Override
    public List<ConfigField> toFieldList() {
        return new ArrayList<>(fields);
    }

    @Override
    public <T> ConfigList<T> toList(Class<T> classType) {
        return createList(field -> field.get(classType).orThrow());
    }

    @Override
    public ConfigList<ConfigScalar> toScalarList() {
        return createList(field -> field.asScalar().orThrow());
    }

    @Override
    public ConfigList<ConfigSection> toSectionList() {
        return createList(field -> field.asSection().orThrow());
    }

    @Override
    public ConfigList<ConfigSequence> toSequenceList() {
        return createList(field -> field.asSequence().orThrow());
    }

    @Override
    public ConfigList<Boolean> toBooleanList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toBoolean).orThrow());
    }

    @Override
    public ConfigList<Character> toCharacterList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toCharacter).orThrow());
    }

    @Override
    public ConfigList<String> toStringList() {
        return createList(field -> field.asScalar().mapIfValid(ConfigScalar::toString).orThrow());
    }

    @Override
    public ConfigList<String> toStringList(StringFormatter formatter) {
        return createList(field -> field.asScalar().mapIfValid(scalar -> scalar.toString(formatter)).orThrow());
    }

    @Override
    public ConfigList<Integer> toIntegerList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toInteger).orThrow());
    }

    @Override
    public ConfigList<Long> toLongList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toLong).orThrow());
    }

    @Override
    public ConfigList<Float> toFloatList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toFloat).orThrow());
    }

    @Override
    public ConfigList<Double> toDoubleList() {
        return createList(field -> field.asScalar().flatMapIfValid(ConfigScalar::toDouble).orThrow());
    }

    @Override
    public @Nullable Object getValue(int index) {
        if (index >= fields.size()) {
            return null;
        }

        ConfigField field = fields.get(index);
        return field.getValue();
    }

    @Override
    public @NotNull ConfigSequence copy() {
        ConfigRoot root = getRoot();
        RootSequence sequence = new RootSequence(null, root.getConfigurator(), root.getPrefix());
        sequence.map(toList());
        return sequence;
    }

    private <T> ConfigList<T> createList(ListMapper<T> mapper) {
        List<T> elements = new ArrayList<>();
        for (ConfigField field : this) {
            try {
                elements.add(mapper.apply(field));
            } catch (InvalidConfigException e) {
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
    public <T> ConfigOptional<T> get(@NotNull Class<T> classType) {
        ConfigRoot root = getRoot();
        Configurator configurator = root.getConfigurator();

        ConfigLoader<T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class: " + classType.getSimpleName());
        }

        try (var scope = new LoaderScope(root, loader)) {
            return ConfigOptional.of(this, loader.loadFromSequence(this));
        } catch (InvalidConfigException e) {
            return ConfigOptional.invalid(e);
        }
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
    public ConfigOptional<ConfigScalar> asScalar() {
        return ConfigOptional.invalid(this, "Expected a value but found a list");
    }

    @Override
    public ConfigOptional<ConfigSection> asSection() {
        return ConfigOptional.invalid(this, "Expected a section but found a list");
    }

    @Override
    public ConfigOptional<ConfigSequence> asSequence() {
        return ConfigOptional.of(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        List<CommentLine> blockComments = getBlockComments();
        if (!blockComments.isEmpty()) {
            builder.append("BlockComments=").append(blockComments).append(", ");
        }

        builder.append("Elements=").append(fields);

        List<CommentLine> inlineComments = getInLineComments();
        if (!inlineComments.isEmpty()) {
            builder.append(", InlineComments=").append(inlineComments);
        }

        return builder.toString();
    }

    @FunctionalInterface
    private interface ListMapper<T> {
        T apply(ConfigField field) throws InvalidConfigException;
    }

}
