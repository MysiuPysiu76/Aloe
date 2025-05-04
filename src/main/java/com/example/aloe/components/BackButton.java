package com.example.aloe.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * A customizable back button component with an icon and text label.
 * <p>
 * This button extends {@link HBox} and allows placing a directional arrow
 * (left or right) next to a text label. It is typically used for navigation purposes,
 * such as returning to a previous view.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * BackButton backButton = new BackButton("Back", true); // Arrow on the left
 * }</pre>
 * </p>
 *
 * <p>
 * CSS classes applied: <code>button</code>, <code>back-button</code>, and <code>icon</code>.
 * </p>
 *
 * @since 1.8.5
 */
public class BackButton extends HBox {

    private Label label;
    private FontIcon icon;

    /**
     * Constructs a {@code BackButton} with the specified text and arrow direction.
     *
     * @param text   the label text to display next to the icon
     * @param isLeft if {@code true}, the icon is placed on the left; otherwise on the right
     */
    public BackButton(String text, boolean isLeft) {
        this.label = new Label(text);
        this.label.setPadding(new Insets(3, 5, 3, 5));
        this.setUpIcon(isLeft);
        this.getStyleClass().addAll("button", "back-button");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(label);
        this.getChildren().add(isLeft ? 0 : 1, icon);
    }

    /**
     * Sets the color of both the icon and the text label.
     *
     * @param color a valid CSS color string (e.g., "#000000", "red")
     */
    public void setColor(String color) {
        label.setStyle("-fx-text-fill: " + color + ";");
        icon.setIconColor(Color.web(color));
    }

    /**
     * Initializes the icon based on the desired direction.
     *
     * @param isLeft if {@code true}, uses a left-pointing arrow; otherwise a right-pointing arrow
     */
    private void setUpIcon(boolean isLeft) {
        if (isLeft) {
            this.icon = new FontIcon(FontAwesome.ANGLE_LEFT);
        } else {
            this.icon = new FontIcon(FontAwesome.ANGLE_RIGHT);
        }
        this.icon.getStyleClass().add("icon");
        this.icon.setIconSize(25);
    }
}
