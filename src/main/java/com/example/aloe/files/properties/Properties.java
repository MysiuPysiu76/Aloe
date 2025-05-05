package com.example.aloe.files.properties;

import java.util.List;
import java.util.Map;

/**
 * Represents a generic interface for accessing file or directory properties.
 * <p>
 * Implementations of this interface should provide a set of human-readable property names
 * and their corresponding values, which can be displayed in user interfaces or exported.
 * </p>
 * <p>
 * Typical properties may include file name, size, type, creation time, modification time,
 * parent directory, and so on.
 *
 * @see FileProperties
 * @see ImageProperties
 * @see VideoProperties
 * @since 1.9.4
 */
public interface Properties {

    /**
     * Returns a map of property names and their corresponding values.
     * <p>
     * The keys in the map represent localized or descriptive property names,
     * while the values represent the associated property values.
     * </p>
     *
     * @return a map of property name-value pairs
     */
    Map<String, String> getProperties();

    /**
     * Returns a list of property names.
     * <p>
     * These names are typically localized strings used for displaying
     * property labels in a user interface.
     * </p>
     *
     * @return a list of property names
     */
    List<String> getPropertiesNames();

    /**
     * Returns a list of property values.
     * <p>
     * Each value corresponds to the property name at the same index
     * in the list returned by {@link #getPropertiesNames()}.
     * </p>
     *
     * @return a list of property values
     */
    List<String> getPropertiesValues();
}
