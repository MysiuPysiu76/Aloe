package com.example.aloe.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

/**
 * A custom JavaFX button that displays a background color, which can be dynamically changed via property binding.
 * <p>
 * The {@code ColorChooser} is useful for color selection interfaces, previews, or interactive UI elements that
 * respond visually to color changes.
 * </p>
 *
 * <p>
 * The color is managed via a JavaFX {@link StringProperty}, allowing it to be bound to other properties or
 * observed for changes. The color string must be in valid CSS format (e.g. {@code "#FFFFFF"} or {@code "red"}).
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * ColorChooser chooser = new ColorChooser("#00FF00", 40, 40);
 * chooser.colorProperty().addListener((obs, oldColor, newColor) -> {
 *     System.out.println("Color changed to: " + newColor);
 * });
 * }</pre>
 *
 * @since 1.9.0
 */
public class ColorChooser extends Button {

    /**
     * The property representing the current background color of the button.
     */
    private final StringProperty colorProperty = new SimpleStringProperty();

    /**
     * Constructs a {@code ColorChooser} with an initial color and fixed dimensions.
     *
     * @param color  the initial color in CSS format (e.g. "#FF0000")
     * @param height the preferred height of the button
     * @param width  the preferred width of the button
     */
    public ColorChooser(String color, double height, double width) {
        this.setPrefHeight(height);
        this.setPrefWidth(width);

        this.styleProperty().bind(colorProperty.concat(";").map(k -> "-fx-background-color: " + k));
        this.colorProperty.set(color);
    }

    /**
     * Returns the current color of the button as a CSS string.
     *
     * @return the current background color
     */
    public String getColor() {
        return this.colorProperty.get();
    }

    /**
     * Returns the {@link StringProperty} representing the button's background color.
     * <p>
     * This allows for binding or listening to color changes.
     * </p>
     *
     * @return the color property
     */
    public StringProperty colorProperty() {
        return this.colorProperty;
    }
}
