package com.example.aloe.components;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * A custom animated toggle switch control built using JavaFX.
 * <p>
 * This switch mimics the appearance of a mobile-style toggle button.
 * Clicking on the switch moves the internal knob using a smooth animation
 * and changes its selection state.
 * </p>
 *
 * <p>
 * The switch can be styled via CSS using the style classes:
 * <ul>
 *   <li><code>toggle-switch</code> – applied to the main container</li>
 *   <li><code>knob</code> – applied to the sliding knob</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * ToggleSwitch toggle = new ToggleSwitch();
 * toggle.setSelected(true); // set initial state
 * if (toggle.isSelected()) {
 *     System.out.println("Switch is ON");
 * }
 * }</pre>
 * </p>
 *
 * @since 1.8.8
 */
public class ToggleSwitch extends StackPane {

    /**
     * The movable knob representing the toggle handle.
     */
    private final Pane knob = new Pane();

    /**
     * Indicates whether the toggle switch is currently selected (ON).
     */
    private boolean isSelected = false;

    /**
     * Constructs an unselected toggle switch with default styling and behavior.
     * Clicking toggles the state and animates the knob.
     */
    public ToggleSwitch() {
        this.getStyleClass().add("toggle-switch");
        this.knob.getStyleClass().add("knob");

        this.setMinHeight(20);
        this.setMaxHeight(20);
        this.setMinWidth(40);
        this.setMaxWidth(40);
        this.setStyle("-fx-background-radius: 100px; -fx-border-color: #a8a8a8; -fx-border-width: 1px; -fx-border-radius: 100px");

        this.knob.setMinSize(18, 18);
        this.knob.setMaxSize(18, 18);
        this.knob.setStyle("-fx-background-color: #aa9494; -fx-background-radius: 100px; -fx-border-color: #b1b1b1; -fx-border-width: 1px; -fx-border-radius: 100px;");

        this.getChildren().add(knob);
        this.setAlignment(Pos.CENTER_LEFT);

        this.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            this.isSelected = !this.isSelected;
            this.animateToggle();
        });
    }

    /**
     * Constructs a toggle switch with an initial selection state.
     *
     * @param isSelected {@code true} for selected (ON), {@code false} for unselected (OFF)
     */
    public ToggleSwitch(boolean isSelected) {
        this();
        this.setSelected(isSelected);
    }

    /**
     * Returns whether the toggle switch is currently selected (ON).
     *
     * @return {@code true} if selected, otherwise {@code false}
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets the selection state of the switch.
     * Also updates the animation and knob color accordingly.
     *
     * @param selected {@code true} to set ON, {@code false} to set OFF
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        animateToggle();
        updateColors();
    }

    /**
     * Performs the animation that slides the knob between ON and OFF positions.
     */
    private void animateToggle() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), knob);
        if (isSelected) {
            transition.setToX(20);
        } else {
            transition.setToX(0);
        }
        transition.play();
    }

    /**
     * Updates the knob color to indicate the selected state (typically ON).
     * This method is called when the switch is toggled programmatically.
     */
    private void updateColors() {
        this.knob.setStyle("-fx-background-color: #05a6d6; -fx-background-radius: 100px;");
    }

    /**
     * Manually sets the knob color using a CSS-compatible color string.
     *
     * @param color a valid CSS color string (e.g., "#ff0000", "blue")
     */
    public void setColor(String color) {
        knob.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 100px;", color));
    }

    /**
     * Returns the knob pane, allowing advanced customization or inspection.
     *
     * @return the internal knob {@link Pane}
     */
    public Pane getKnob() {
        return knob;
    }
}
