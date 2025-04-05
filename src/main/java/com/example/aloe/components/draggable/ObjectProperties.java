package com.example.aloe.components.draggable;

import java.util.Map;

/**
 * The {@code ObjectProperties} interface defines methods for retrieving the properties of an object as a map,
 * as well as a method for displaying those properties in a format that can be used in the user interface,
 * such as in the {@link InfoBox}.
 * <p>
 * A class implementing this interface should provide the object's properties in the form of key-value pairs.
 * </p>
 *
 * @since 1.5.1
 */
public interface ObjectProperties {

    /**
     * Returns the properties of the object as a map, where the keys are the property names,
     * and the values are the corresponding property values. This can be used for further processing or display.
     *
     * @return a map of the object's properties
     */
    Map<String, String> getObjectProperties();

    /**
     * Returns the properties of the object as a map, where the keys are the property names,
     * and the values are the corresponding property values formatted for display in the user interface.
     * This is typically used in components like {@link InfoBox}.
     *
     * @return a map of the object's properties for display purposes
     */
    Map<String, String> getObjectPropertiesView();
}