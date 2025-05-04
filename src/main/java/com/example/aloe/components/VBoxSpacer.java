package com.example.aloe.components;

import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A flexible vertical spacer for use within a {@link VBox} layout.
 * <p>
 * This component expands to fill available vertical space and can be used
 * to push other components upward or downward inside a {@code VBox}.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * VBox vbox = new VBox();
 * vbox.getChildren().addAll(new Label("Top"), new VBoxSpacer(), new Label("Bottom"));
 * }</pre>
 * </p>
 *
 * <p>
 * Internally, it sets {@code VBox.setVgrow(this, Priority.ALWAYS)} to ensure it expands.
 * </p>
 *
 * @since 1.8.6
 */
public class VBoxSpacer extends Region {

    /**
     * Constructs a vertical spacer with grow priority set to {@code ALWAYS}.
     */
    public VBoxSpacer() {
        VBox.setVgrow(this, Priority.ALWAYS);
    }
}
