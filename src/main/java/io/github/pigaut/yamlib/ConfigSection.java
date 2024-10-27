package io.github.pigaut.yamlib;

import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public interface ConfigSection {

    /**
     * Checks if this section is the root section.
     *
     * @return true if this section is the root, false otherwise
     */
    boolean isRoot();

    /**
     * Retrieves the root section.
     *
     * @return the root {@link Config}
     */
    @NotNull Config getRoot();

    /**
     * Retrieves the key of this section.
     *
     * @return the key or index as a string
     * @throws UnsupportedOperationException if this section is the root
     */
    @NotNull String getKey() throws UnsupportedOperationException;

    /**
     * Retrieves the parent section of this section.
     *
     * @return the parent {@link ConfigSection}
     * @throws UnsupportedOperationException if this section is the root
     */
    @NotNull ConfigSection getParent() throws UnsupportedOperationException;

    /**
     * Retrieves the path of this section from the root.
     *
     * @return the path as a string
     * @throws UnsupportedOperationException if this section is the root
     */
    @NotNull String getPath() throws UnsupportedOperationException;

    /**
     * Returns the number of keys in this section.
     *
     * @return the number of keys
     */
    int size();

    /**
     * Returns the set of keys present in this section.
     *
     * @return a set of strings representing the keys
     */
    Set<String> getKeys();

    /**
     * Returns the set of fields present in this section.
     *
     * @return a set of strings representing the fields
     */
    Set<Object> getFields();

    /**
     * Checks if the section contains a field at the specified path.
     *
     * @param path the path to check
     * @return true if the path exists, false otherwise
     */
    boolean contains(@NotNull String path);

    /**
     * Sets a value at the specified path.
     *
     * @param path the path where the value will be set
     * @param value the value to set
     */
    void set(@NotNull String path, Object value);

    /**
     * Adds a value to the section.
     *
     * @param value the value to add
     */
    void add(Object value);

    /**
     * Maps the specified value to this section.
     *
     * @param value the value to map
     */
    void map(@NotNull Object value);

    /**
     * Clears all fields in this section.
     */
    void clear();

    /**
     * Adds a new nested section.
     *
     * @return the new {@link ConfigSection}
     */
    ConfigSection addSection();

    /**
     * Checks if this section is keyless.
     *
     * @return true if this section is a list, false otherwise
     */
    boolean isKeyless();

    /**
     * Sets whether this section should be treated as a list.
     *
     * @param keyless true if this section should be keyless (Deletes all current key data), false otherwise
     */
    void setKeyless(boolean keyless);

    /**
     * Retrieves the current flow style for this section.
     *
     * @return the flow style of this section
     */
    @NotNull FlowStyle getFlowStyle();

    /**
     * Sets the flow style for this section.
     *
     * @param flowStyle the flow style to set
     */
    void setFlowStyle(@NotNull FlowStyle flowStyle);

    /**
     * Retrieves the default flow style for nested sections/lists.
     *
     * @return the flow style of this section or null if it should use the default
     */
    @Nullable FlowStyle getDefaultFlowStyle();

    /**
     * Sets the default flow style for the nested sections/lists.
     *
     * @param flowStyle the flow style to set
     */
    void setDefaultFlowStyle(@Nullable FlowStyle flowStyle);

    /**
     * Loads an object of the specified type from this section.
     *
     * @param type the class of the value to load
     * @return the loaded value
     * @throws InvalidConfigurationException if there was a config mistake during loading
     */
    @NotNull <T> T load(@NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Loads an object of the specified type from the section at the given path.
     *
     * @param path the path to load the value from
     * @param type the class type of the object to load
     * @return the loaded object
     * @throws InvalidConfigurationException if the section does not exist or if there was a config mistake during loading
     */
    @NotNull <T> T load(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Parses the string at the specified path as the given type.
     *
     * @param path the path of the string to parse
     * @param type the class of the string to parse
     * @return the parsed string
     * @throws InvalidConfigurationException if the value could not be parsed to the specified type
     */
    @NotNull <T> T get(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Attempts to parse or load an object of the given type.
     *
     * @param path the path of the string to parse or section to load from
     * @param type the class of the object to parse or load
     * @return the parsed string or loaded section
     * @throws InvalidConfigurationException if the object could not be parsed or loaded
     */
    @NotNull <T> T getOrLoad(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Attempts to load an object of the specified type from this section.
     *
     * @param type the class of the value to load
     * @return an {@link Optional} containing the value if present, or empty if it could not be loaded
     */
    <T> Optional<T> loadOptional(@NotNull Class<T> type);

    /**
     * Attempts to load an object of the specified type from the given path.
     *
     * @param path the path to load the value from
     * @param type the class type of the object to load
     * @return an {@link Optional} containing the object if present, or empty if it could not be loaded
     */
    <T> Optional<T> loadOptional(@NotNull String path, @NotNull Class<T> type);

    /**
     * Attempts to parse a string to an object of the specified type from the given path.
     *
     * @param path the path to parse the value from
     * @param type the class type of the object to parse
     * @return an {@link Optional} containing the object if present, or empty if it could not be parsed
     */
    <T> Optional<T> getOptional(@NotNull String path, @NotNull Class<T> type);

    /**
     * Attempts to parse or load an object of the given type at the specified path.
     *
     * @param path the path of the string to parse or section to load from
     * @param type the class of the object to parse or load
     * @return an {@link Optional} containing the object if present, or empty if it could not be parsed/loaded
     */
    <T> Optional<T> getOrLoadOptional(@NotNull String path, @NotNull Class<T> type);

    /**
     * Attempts to load all nested fields as objects of the given type, ignoring non-section elements and those who fail to load.
     *
     * @param type the class type of the objects to load
     * @return a {@link Stream} of successfully loaded objects
     */
    <T> Stream<@NotNull T> loadAll(@NotNull Class<T> type);

    /**
     * Attempts to load all nested fields as objects of the given type.
     *
     * @param type the class type of the objects to load
     * @return a {@link Stream} of successfully loaded objects
     * @throws InvalidConfigurationException if this section contains non-section elements or if any object fails to load
     */
    <T> Stream<@NotNull T> loadAllOrThrow(@NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Attempts to load all nested fields as objects of the given type, ignoring non-section elements and those who fail to load.
     *
     * @param path the path to load the values from
     * @param type the class type of the objects to load
     * @return a {@link Stream} of successfully loaded objects
     */
    <T> Stream<@NotNull T> loadAll(@NotNull String path, @NotNull Class<T> type);

    /**
     * Attempts to load all nested fields as objects of the given type.
     *
     * @param path the path to load the objects from
     * @param type the class type of the objects to load
     * @return a {@link Stream} of objects of the specified type
     * @throws InvalidConfigurationException if the path contains non-section elements or if any object fails to load
     */
    <T> Stream<@NotNull T> loadAllOrThrow(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Retrieves a nested section at the specified path;
     *
     * @param path the path of the nested section
     * @return the nested {@link ConfigSection}
     * @throws InvalidConfigurationException if the section is not found
     */
    @NotNull ConfigSection getSection(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves or creates a nested section at the specified path.
     *
     * @param path the path of the nested section
     * @return the existing or newly created {@link ConfigSection}
     */
    @NotNull ConfigSection getSectionOrCreate(@NotNull String path);

    /**
     * Attempts to retrieve a nested section at the specified path.
     *
     * @param path the path of the nested section
     * @return an {@link Optional} containing the nested section if present, or empty if not found
     */
    Optional<ConfigSection> getOptionalSection(@NotNull String path);

    /**
     * Retrieves all nested fields in this section as a map.
     *
     * @return a map of all nested fields
     */
    Map<@NotNull String, @NotNull Object> getNestedFields();

    /**
     * Retrieves all nested scalar values in this section as a map.
     *
     * @return a map of all nested scalar values
     */
    Map<@NotNull String, @NotNull Object> getNestedScalars();

    /**
     * Retrieves all nested sections within this section as a set.
     *
     * @return a set of nested {@link ConfigSection}s
     */
    Set<@NotNull ConfigSection> getNestedSections();

    /**
     * Retrieves all nested fields from the specified path.
     *
     * @param path the path of the section
     * @return a map of all nested fields, or an empty map if the section does not exist
     */
    Map<@NotNull String, @NotNull Object> getNestedFields(@NotNull String path);

    /**
     * Retrieves all nested scalar values from the specified path.
     *
     * @param path the path of the section
     * @return a map of all nested scalar values starting from the specified path
     */
    Map<@NotNull String, @NotNull Object> getNestedScalars(@NotNull String path);

    /**
     * Retrieves all nested sections from the specified path.
     *
     * @param path the path to of the section
     * @return a set of nested {@link ConfigSection}s at the path, or an empty set if the section does not exist
     */
    Set<@NotNull ConfigSection> getNestedSections(@NotNull String path);

    /**
     * Retrieves a list of all fields at the specified path.
     *
     * @param path the path of the field list
     * @return a list of fields
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Object> getFieldList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of scalar values at the specified path.
     *
     * @param path the path of the scalar list
     * @return a list of scalar values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Object> getScalarList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of sections at the specified path.
     *
     * @param path the path of the section list
     * @return a list of {@link ConfigSection}s
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull ConfigSection> getSectionList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of boolean values at the specified path.
     *
     * @param path the path of the boolean list
     * @return a list of boolean values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Boolean> getBooleanList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of character values at the specified path.
     *
     * @param path the path of the character list
     * @return a list of character values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Character> getCharacterList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of string values at the specified path.
     *
     * @param path the path of the string list
     * @return a list of string values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull String> getStringList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of formatted string values at the specified path.
     *
     * @param path the path of the string list
     * @param formatter the formatter to apply to the strings
     * @return a list of formatted string values
     * @throws InvalidConfigurationException if the list cannot be retrieved or formatted
     */
    List<@NotNull String> getStringList(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException;

    /**
     * Retrieves a list of integer values at the specified path.
     *
     * @param path the path of the integer list
     * @return a list of integer values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Integer> getIntegerList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of long values at the specified path.
     *
     * @param path the path of the long list
     * @return a list of long values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Long> getLongList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of float values at the specified path.
     *
     * @param path the path of the float list
     * @return a list of float values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Float> getFloatList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of double values at the specified path.
     *
     * @param path the path of the double list
     * @return a list of double values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull Double> getDoubleList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of BigInteger values at the specified path.
     *
     * @param path the path of the BigInteger list
     * @return a list of {@link BigInteger} values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull BigInteger> getBigIntegerList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of BigDecimal values at the specified path.
     *
     * @param path the path of the BigDecimal list
     * @return a list of {@link BigDecimal} values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull BigDecimal> getBigDecimalList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of strings and attempts to parse each value to the specified type
     *
     * @param path the path of the list
     * @param type the class of the object to retrieve
     * @return a list of values of the specified type
     * @throws InvalidConfigurationException if the list cannot be retrieved or parsed to the specified type
     */
    <T> List<@NotNull T> getList(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Retrieves a list of strings and attempts to parse each value to the specified type
     *
     * @param type the class of the object to retrieve
     * @return a list of values of the specified type
     * @throws InvalidConfigurationException if the list cannot be retrieved or parsed to the specified type
     */
    <T> List<@NotNull T> getList(@NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Retrieves the field at the specified path. (Boolean, Character, String, ConfigSection, Numbers)
     *
     * @param path the path of the field
     * @return the field value
     * @throws InvalidConfigurationException if the field is not set.
     */
    @NotNull Object getField(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve the field at the specified path. (Boolean, Character, String, ConfigSection, Numbers)
     *
     * @param path the path of the field
     * @return an {@link Optional} containing the field value if present, or empty if not set
     */
    Optional<Object> getOptionalField(@NotNull String path);

    /**
     * Retrieves a scalar value at the specified path.
     *
     * @param path the path of the scalar
     * @return the scalar value
     * @throws InvalidConfigurationException if the field is not set, or if the field is not a scalar
     */
    @NotNull Object getScalar(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a scalar value at the specified path.
     *
     * @param path the path of the scalar
     * @return an {@link Optional} containing the scalar value if present, or empty if not found
     */
    Optional<Object> getOptionalScalar(@NotNull String path);

    /**
     * Retrieves a boolean value at the specified path.
     *
     * @param path the path of the boolean value
     * @return the boolean value
     * @throws InvalidConfigurationException if the field is not set, or if the field is not a boolean
     */
    boolean getBoolean(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a boolean value at the specified path.
     *
     * @param path the path of the boolean value
     * @return an {@link Optional} containing the boolean value if present, or empty if not found
     */
    Optional<Boolean> getOptionalBoolean(@NotNull String path);

    /**
     * Retrieves a character value at the specified path.
     *
     * @param path the path of the character value
     * @return the character value
     * @throws InvalidConfigurationException if the field is not set, or if the field is not a character
     */
    char getCharacter(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a character value at the specified path.
     *
     * @param path the path of the character value
     * @return an {@link Optional} containing the character value if present, or empty if not found
     */
    Optional<Character> getOptionalCharacter(@NotNull String path);

    /**
     * Retrieves the scalar value at the specified path and turns it into a string
     *
     * @param path the path of the string value
     * @return the string value
     * @throws InvalidConfigurationException if the field is not set or is not a scalar
     */
    @NotNull String getString(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string value at the specified path.
     *
     * @param path the path of the string value
     * @return an {@link Optional} containing the string value if present, or empty if not found
     */
    Optional<String> getOptionalString(@NotNull String path);


    /**
     * Splits a string based on the specified regular expression and attempts to parse each resulting part
     * to the provided types in the {@code elementTypes} array.
     *
     * @param path         the path of the string to split
     * @param regex        the regular expression to use for splitting the input string.
     * @param elementTypes the types that each split element should be cast to. Each type corresponds to the
     *                     respective split part in the resulting array.
     * @return an array of objects where each element is a part of the split string, parsed as the specified types.
     * @throws InvalidConfigurationException if the number of parts after splitting does not match the number of
     *                                  specified types, or if a split element cannot be parsed as the specified type.
     */
    Object[] getSplitString(String path, String regex, Class<?>... elementTypes) throws InvalidConfigurationException;

    /**
     * Retrieves the scalar value at the specified path and turns it into a formatted string
     *
     * @param path the path of the string value
     * @param formatter the {@link StringFormatter} to apply to the string
     * @return the formatted string value
     * @throws InvalidConfigurationException if the field is not set, or if the field is not a scalar
     */
    @NotNull String getString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a formatted string value at the specified path.
     *
     * @param path the path of the string value
     * @param formatter the {@link StringFormatter} to apply to the string
     * @return an {@link Optional} containing the formatted string value if present, or empty if not found
     */
    Optional<String> getOptionalString(@NotNull String path, @NotNull StringFormatter formatter);

    /**
     * Retrieves an integer value at the specified path.
     *
     * @param path the path of the integer value
     * @return the integer value
     * @throws InvalidConfigurationException if the integer value does not exist or cannot be retrieved
     */
    int getInteger(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve an integer value at the specified path.
     *
     * @param path the path of the integer value
     * @return an {@link Optional} containing the integer value if present, or empty if not found
     */
    Optional<Integer> getOptionalInteger(@NotNull String path);

    /**
     * Retrieves a long value at the specified path.
     *
     * @param path the path of the long value
     * @return the long value
     * @throws InvalidConfigurationException if the long value does not exist or cannot be retrieved
     */
    long getLong(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a long value at the specified path.
     *
     * @param path the path of the long value
     * @return an {@link Optional} containing the long value if present, or empty if not found
     */
    Optional<Long> getOptionalLong(@NotNull String path);

    /**
     * Retrieves a float value at the specified path.
     *
     * @param path the path of the float value
     * @return the float value
     * @throws InvalidConfigurationException if the float value does not exist or cannot be retrieved
     */
    float getFloat(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a float value at the specified path.
     *
     * @param path the path of the float value
     * @return an {@link Optional} containing the float value if present, or empty if not found
     */
    Optional<Float> getOptionalFloat(@NotNull String path);

    /**
     * Retrieves a double value at the specified path.
     *
     * @param path the path of the double value
     * @return the double value
     * @throws InvalidConfigurationException if the double value does not exist or cannot be retrieved
     */
    double getDouble(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a double value at the specified path.
     *
     * @param path the path of the double value
     * @return an {@link Optional} containing the double value if present, or empty if not found
     */
    Optional<Double> getOptionalDouble(@NotNull String path);

    /**
     * Retrieves a BigInteger value at the specified path.
     *
     * @param path the path of the BigInteger value
     * @return the BigInteger value
     * @throws InvalidConfigurationException if the BigInteger value does not exist or cannot be retrieved
     */
    @NotNull BigInteger getBigInteger(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a BigInteger value at the specified path.
     *
     * @param path the path of the BigInteger value
     * @return an {@link Optional} containing the BigInteger value if present, or empty if not found
     */
    Optional<BigInteger> getOptionalBigInteger(@NotNull String path);

    /**
     * Retrieves a BigDecimal value at the specified path.
     *
     * @param path the path of the BigDecimal value
     * @return the BigDecimal value
     * @throws InvalidConfigurationException if the BigDecimal value does not exist or cannot be retrieved
     */
    @NotNull BigDecimal getBigDecimal(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a BigDecimal value at the specified path.
     *
     * @param path the path of the BigDecimal value
     * @return an {@link Optional} containing the BigDecimal value if present, or empty if not found
     */
    Optional<BigDecimal> getOptionalBigDecimal(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link LocalDate}.
     *
     * @param path the path of the {@link LocalDate} value
     * @return the {@link LocalDate} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link LocalDate}
     */
    @NotNull LocalDate getDate(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link LocalDate}.
     *
     * @param path the path of the {@link LocalDate} value
     * @return an {@link Optional} containing the {@link LocalDate} value, or empty if parsing failed
     */
    Optional<LocalDate> getOptionalDate(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link LocalTime}.
     *
     * @param path the path of the {@link LocalTime} value
     * @return the {@link LocalTime} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link LocalTime}
     */
    @NotNull LocalTime getTime(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link LocalTime}.
     *
     * @param path the path of the {@link LocalTime} value
     * @return an {@link Optional} containing the {@link LocalTime} value, or empty if parsing failed
     */
    Optional<LocalTime> getOptionalTime(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link LocalDateTime}.
     *
     * @param path the path of the {@link LocalDateTime} value
     * @return the {@link LocalDateTime} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link LocalDateTime}
     */
    @NotNull LocalDateTime getDateTime(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link LocalDateTime}.
     *
     * @param path the path of the {@link LocalDateTime} value
     * @return an {@link Optional} containing the {@link LocalDateTime} value, or empty if parsing failed
     */
    Optional<LocalDateTime> getOptionalDateTime(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link File}.
     *
     * @param path the path of the {@link File} value
     * @return the {@link File} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link File}
     */
    @NotNull File getFile(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a string at the specified path and parses it as a {@link Locale}.
     *
     * @param path the path of the {@link Locale} value
     * @return the {@link Locale} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link Locale}
     */
    @NotNull Locale getLocale(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link Locale}.
     *
     * @param path the path of the {@link Locale} value
     * @return an {@link Optional} containing the {@link Locale} value, or empty if parsing failed
     */
    Optional<Locale> getOptionalLocale(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link UUID}.
     *
     * @param path the path of the {@link UUID} value
     * @return the {@link UUID} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link UUID}
     */
    @NotNull UUID getUUID(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link UUID}.
     *
     * @param path the path of the {@link UUID} value
     * @return an {@link Optional} containing the {@link UUID} value, or empty if parsing failed
     */
    Optional<UUID> getOptionalUUID(@NotNull String path);

    /**
     * Retrieves a string at the specified path and parses it as a {@link URL}.
     *
     * @param path the path of the {@link URL} value
     * @return the {@link URL} value
     * @throws InvalidConfigurationException if the string could not be parsed to a {@link URL}
     */
    @NotNull URL getURL(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a string at the specified path and parse it as a {@link URL}.
     *
     * @param path the path of the {@link URL} value
     * @return an {@link Optional} containing the {@link URL} value, or empty if parsing failed
     */
    Optional<URL> getOptionalURL(@NotNull String path);

    /**
     * Retrieves a 2D array of objects at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array representing the matrix
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    Object[][] getMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of strings at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of strings
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    String[][] getStringMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of characters at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of characters
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    char[][] getCharMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of integers at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of integers
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    int[][] getIntegerMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of booleans at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of booleans
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    boolean[][] getBooleanMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of doubles at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of doubles
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    double[][] getDoubleMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of longs at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of longs
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    long[][] getLongMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of floats at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of floats
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    float[][] getFloatMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

    /**
     * Retrieves a 2D array of the specified type at the given path.
     *
     * @param path the path of the matrix
     * @param type the class of the elements in the matrix
     * @param rowCount the number of rows in the matrix
     * @param columnCount the number of columns in the matrix
     * @param <T> the type of the elements in the matrix
     * @return a 2D array of the specified type
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    @NotNull <T> T[][] getMatrix(@NotNull String path, @NotNull Class<T> type, int rowCount, int columnCount) throws InvalidConfigurationException;

    /**
     * Converts this section into a list of its contained fields.
     *
     * @return a list of fields
     */
    List<Object> toList();

}
