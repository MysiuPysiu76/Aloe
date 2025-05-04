package com.example.aloe.components;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A flexible horizontal spacer for use within an {@link HBox} layout.
 * <p>
 * This component expands to fill available horizontal space and can be used
 * to push other components to the left or right inside an {@code HBox}.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * HBox hbox = new HBox();
 * hbox.getChildren().addAll(new Button("Left"), new HBoxSpacer(), new Button("Right"));
 * }</pre>
 * </p>
 *
 * <p>
 * Internally, it sets {@code HBox.setHgrow(this, Priority.ALWAYS)} to ensure it expands.
 * </p>
 *
 * @since 1.8.6
 */
public class HBoxSpacer extends Region {

    /**
     * Constructs a horizontal spacer with grow priority set to {@code ALWAYS}.
     */
    public HBoxSpacer() {
        HBox.setHgrow(this, Priority.ALWAYS);
    }
}
