package com.example.aloe.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A custom slider component consisting of a JavaFX {@link javafx.scene.control.Slider}
 * placed between two optional text labels.
 * <p>
 * This component exposes a bindable {@link DoubleProperty} representing the current value,
 * which is automatically updated when the user moves the slider.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * Slider customSlider = new Slider(0, 100, 50);
 * customSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
 *     System.out.println("New value: " + newVal.doubleValue());
 * });
 * }</pre>
 * </p>
 * <p>
 * CSS style class applied to labels: <code>text</code>
 *
 * @since 1.8.7
 */
public class Slider extends HBox {

    private final Label leftLabel = new Label();
    private final Label rightLabel = new Label();
    private final javafx.scene.control.Slider slider;
    private final DoubleProperty value = new SimpleDoubleProperty();

    /**
     * Constructs a new Slider with given minimum, maximum and initial value.
     *
     * @param min     the minimum slider value
     * @param max     the maximum slider value
     * @param current the initial value of the slider
     */
    public Slider(double min, double max, double current) {
        super();
        this.slider = new javafx.scene.control.Slider(min, max, current);
        this.getChildren().addAll(leftLabel, slider, rightLabel);
        this.setAlignment(Pos.CENTER);
        this.leftLabel.getStyleClass().add("text");
        this.rightLabel.getStyleClass().add("text");

        HBox.setMargin(this.slider, new Insets(0, 7, 0, 7));
        HBox.setMargin(this, new Insets(0, 20, 0, 20));

        this.value.bind(slider.valueProperty());
    }

    /**
     * Sets the text of the left and right labels.
     *
     * @param leftText  the text for the left label
     * @param rightText the text for the right label
     */
    public void setText(String leftText, String rightText) {
        this.leftLabel.setText(leftText);
        this.rightLabel.setText(rightText);
    }

    /**
     * Sets the major tick unit for the slider.
     *
     * @param value the major tick unit
     */
    public void setMajorTickUnit(double value) {
        this.slider.setMajorTickUnit(value);
    }

    /**
     * Sets the block increment for the slider.
     *
     * @param value the amount the slider moves when clicked
     */
    public void setBlockIncrement(double value) {
        this.slider.setBlockIncrement(value);
    }

    /**
     * Returns the underlying JavaFX slider.
     *
     * @return the internal {@link javafx.scene.control.Slider}
     */
    public javafx.scene.control.Slider getSlider() {
        return this.slider;
    }

    /**
     * Returns the property representing the slider's current value.
     *
     * @return the value property
     */
    public DoubleProperty valueProperty() {
        return this.value;
    }
}
