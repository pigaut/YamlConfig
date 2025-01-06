package io.github.pigaut.yaml;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigSection extends ConfigBranch {

    /**
     * Returns the set of keys present in this section.
     *
     * @return a set of strings representing the keys
     */
    Set<String> getKeys();

    /**
     * Checks if the section contains a field at the specified path.
     *
     * @param path the path to check
     * @return true if the path exists, false otherwise
     */
    boolean contains(@NotNull String path);

    boolean isSet(@NotNull String path);

    boolean isSection(@NotNull String path);

    boolean isSequence(@NotNull String path);

    /**
     * Sets a value at the specified path.
     *
     * @param path the path where the value will be set
     * @param value the value to set
     */
    <T> void set(@NotNull String path, @NotNull T value);

    void remove(@NotNull String path);

    void formatKeys(StringFormatter formatter);

    @NotNull <T> T get(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    <T> Optional<T> getOptional(@NotNull String path, @NotNull Class<T> type);

    <T> List<@NotNull T> getAll(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Retrieves a nested section at the specified path;
     *
     * @param path the path of the nested section
     * @return the nested {@link ConfigSection}
     * @throws InvalidConfigurationException if the section is not found
     */
    @NotNull ConfigSection getSection(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a nested section at the specified path.
     *
     * @param path the path of the nested section
     * @return an {@link Optional} containing the nested section if present, or empty if not found
     */
    Optional<ConfigSection> getOptionalSection(@NotNull String path);

    /**
     * Retrieves or creates a nested section at the specified path.
     *
     * @param path the path of the nested section
     * @return the existing or newly created {@link ConfigSection}
     */
    @NotNull ConfigSection getSectionOrCreate(@NotNull String path);

    @NotNull
    ConfigSequence getSequence(@NotNull String path) throws InvalidConfigurationException;

    Optional<ConfigSequence> getOptionalSequence(@NotNull String path);

    @NotNull
    ConfigSequence getSequenceOrCreate(@NotNull String path);

    @NotNull Set<ConfigField> getNestedFields(@NotNull String path);
    @NotNull Set<ConfigSection> getNestedSections(@NotNull String path);

    /**
     * Retrieves a list of all fields at the specified path.
     *
     * @param path the path of the field list
     * @return a list of fields
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull ConfigField> getFieldList(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Retrieves a list of scalar values at the specified path.
     *
     * @param path the path of the scalar list
     * @return a list of scalar values
     * @throws InvalidConfigurationException if the list cannot be retrieved
     */
    List<@NotNull ConfigScalar> getScalarList(@NotNull String path) throws InvalidConfigurationException;

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
     * Retrieves a list of strings and attempts to parse each value to the specified type
     *
     * @param path the path of the list
     * @param type the class of the object to retrieve
     * @return a list of values of the specified type
     * @throws InvalidConfigurationException if the list cannot be retrieved or parsed to the specified type
     */
    <T> List<@NotNull T> getList(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    /**
     * Retrieves the field at the specified path. (Boolean, Character, String, ConfigSection, Numbers)
     *
     * @param path the path of the field
     * @return the field value
     * @throws InvalidConfigurationException if the field is not set.
     */
    @NotNull
    ConfigField getField(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve the field at the specified path. (Boolean, Character, String, ConfigSection, Numbers)
     *
     * @param path the path of the field
     * @return an {@link Optional} containing the field value if present, or empty if not set
     */
    Optional<ConfigField> getOptionalField(@NotNull String path);

    /**
     * Retrieves a scalar value at the specified path.
     *
     * @param path the path of the scalar
     * @return the scalar value
     * @throws InvalidConfigurationException if the field is not set, or if the field is not a scalar
     */
    @NotNull ConfigScalar getScalar(@NotNull String path) throws InvalidConfigurationException;

    /**
     * Attempts to retrieve a scalar value at the specified path.
     *
     * @param path the path of the scalar
     * @return an {@link Optional} containing the scalar value if present, or empty if not found
     */
    Optional<ConfigScalar> getOptionalScalar(@NotNull String path);

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
     * Retrieves a 2D array of objects at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array representing the matrix
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    ConfigScalar[][] getMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

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
     * Retrieves a 2D array of characters at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of characters
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    char[][] getCharacterMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

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

    String[][] getStringMatrix(@NotNull String path, int rows, int columns, StringFormatter formatter) throws InvalidConfigurationException;

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
     * Retrieves a 2D array of doubles at the specified path.
     *
     * @param path the path of the matrix
     * @param rows the number of rows in the matrix
     * @param columns the number of columns in the matrix
     * @return a 2D array of doubles
     * @throws InvalidConfigurationException if the matrix cannot be retrieved
     */
    double[][] getDoubleMatrix(@NotNull String path, int rows, int columns) throws InvalidConfigurationException;

}
