package com.example.aloe.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * A custom JavaFX component that allows users to visually select a color using a hue slider and a saturation-value (SV) map.
 * <p>
 * The {@code ColorPicker} consists of:
 * <ul>
 *     <li>A hue selection gradient bar</li>
 *     <li>An SV selection area (saturation and brightness)</li>
 *     <li>A preview rectangle and a text field showing the selected color in HEX</li>
 * </ul>
 *
 * <p>
 * The component exposes a {@link #colorProperty} for easy property binding and programmatic access to the currently selected color.
 * Color can be updated through user interaction or by manually setting a valid HEX string to {@code colorProperty}.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * ColorPicker picker = new ColorPicker();
 * picker.colorProperty().addListener((obs, oldVal, newVal) -> {
 *     System.out.println("Selected color: " + newVal);
 * });
 * }</pre>
 *
 * @since 1.9.0
 */
public class ColorPicker extends VBox {

    private static final int HUE_WIDTH = 300;
    private static final int HUE_HEIGHT = 20;
    private static final int SV_WIDTH = 300;
    private static final int SV_HEIGHT = 190;

    private double hue = 0;
    private double saturation = 1;
    private double brightness = 1;
    private final Canvas hueCanvas = new Canvas(HUE_WIDTH, HUE_HEIGHT);
    private final Canvas svCanvas = new Canvas(SV_WIDTH, SV_HEIGHT);
    private final Circle huePicker = new Circle(6, Color.TRANSPARENT);
    private final Circle svPicker = new Circle(6, Color.TRANSPARENT);
    public final StringProperty colorProperty = new SimpleStringProperty();
    private final Rectangle colorPreview;
    private final TextField colorText;

    /**
     * Constructs a new {@code ColorPicker} instance and initializes the UI components.
     */
    public ColorPicker() {
        GraphicsContext hueGC = hueCanvas.getGraphicsContext2D();
        GraphicsContext svGC = svCanvas.getGraphicsContext2D();

        drawHueGradient(hueGC);
        drawSVGradient(svGC);

        huePicker.setStroke(Color.BLACK);
        huePicker.setStrokeWidth(2);
        huePicker.setMouseTransparent(true);

        svPicker.setStroke(Color.BLACK);
        svPicker.setStrokeWidth(2);
        svPicker.setMouseTransparent(true);

        colorPreview = new Rectangle(120, 50, getCurrentColor());
        colorPreview.setStroke(Color.BLACK);

        colorText = new TextField();
        colorText.getStyleClass().add("text");
        colorText.textProperty().addListener((obs, oldVal, newVal) -> updateColor(newVal));

        colorProperty.addListener((obs, oldVal, newVal) -> updateColor(newVal));

        Pane svPane = new Pane(svCanvas, svPicker);
        Pane huePane = new Pane(hueCanvas, huePicker);
        VBox.setMargin(svPane, new Insets(10, 0, 10, 0));

        HBox panel = new HBox(10, colorPreview, colorText);
        panel.setAlignment(Pos.CENTER);
        panel.setMinWidth(SV_WIDTH);

        hueCanvas.setOnMousePressed(this::handleHuePick);
        hueCanvas.setOnMouseDragged(this::handleHuePick);
        svCanvas.setOnMousePressed(this::handleSVPick);
        svCanvas.setOnMouseDragged(this::handleSVPick);

        this.getChildren().addAll(huePane, svPane, panel);
        this.setPadding(new Insets(10));

        updatePickers();
        updateColor();
    }

    /**
     * Checks whether a given string is a valid CSS color.
     *
     * @param color the string to validate
     * @return true if the string is a valid CSS color, false otherwise
     */
    public static boolean isColorValid(String color) {
        try {
            Color.web(color);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates the color picker based on a given color string.
     * <p>
     * This method validates the provided color string and, if valid, parses it into a {@link javafx.scene.paint.Color} object.
     * The color's hue, saturation, and brightness values are extracted, and the associated UI elements (pickers and previews)
     * are updated accordingly.
     * </p>
     *
     * @param color the color string in a valid CSS format (e.g., "#FF0000" or "red").
     */
    private void updateColor(String color) {
        if (isColorValid(color)) {
            Color parsed = Color.web(color);
            hue = parsed.getHue();
            saturation = parsed.getSaturation();
            brightness = parsed.getBrightness();
            updatePickers();
            drawSVGradient(svCanvas.getGraphicsContext2D());
            colorPreview.setFill(parsed);
            colorText.setText(toHex(parsed));
        }
    }

    /**
     * Draws a gradient representing the hue values on the hue canvas.
     * <p>
     * The gradient spans the width of the {@code hueCanvas} and goes through all 360 degrees of hue.
     * The method sets each vertical pixel to a color corresponding to its hue value.
     * </p>
     *
     * @param gc the graphics context used to draw the gradient.
     */
    private void drawHueGradient(GraphicsContext gc) {
        for (int x = 0; x < HUE_WIDTH; x++) {
            double h = (x / (double) HUE_WIDTH) * 360;
            gc.setFill(Color.hsb(h, 1.0, 1.0));
            gc.fillRect(x, 0, 1, HUE_HEIGHT);
        }
    }

    /**
     * Draws a gradient representing the saturation and value (brightness) on the SV canvas.
     * <p>
     * The gradient is drawn as a grid of colors where the x-axis represents saturation and the y-axis represents brightness.
     * Saturation goes from 0 (left) to 1 (right), and brightness goes from 1 (top) to 0 (bottom).
     * </p>
     *
     * @param gc the graphics context used to draw the gradient.
     */
    private void drawSVGradient(GraphicsContext gc) {
        for (int x = 0; x < SV_WIDTH; x++) {
            for (int y = 0; y < SV_HEIGHT; y++) {
                double s = x / (double) SV_WIDTH;
                double v = 1 - (y / (double) SV_HEIGHT);
                gc.getPixelWriter().setColor(x, y, Color.hsb(hue, s, v));
            }
        }
    }

    /**
     * Handles the user's interaction with the hue slider to select a hue.
     * <p>
     * This method updates the hue value based on the user's click or drag position on the hue canvas,
     * then redraws the SV gradient and updates the hue picker position.
     * </p>
     *
     * @param e the mouse event triggered by the user interaction.
     */
    private void handleHuePick(MouseEvent e) {
        double x = clamp(e.getX(), 0, HUE_WIDTH - 1);
        hue = (x / HUE_WIDTH) * 360;
        drawSVGradient(svCanvas.getGraphicsContext2D());
        huePicker.setCenterX(x);
        huePicker.setCenterY(HUE_HEIGHT / 2);
        updateColor();
    }

    /**
     * Handles the user's interaction with the saturation and value (SV) selector.
     * <p>
     * This method updates the saturation and brightness values based on the user's click or drag position on the SV canvas,
     * then updates the position of the SV picker and the selected color preview.
     * </p>
     *
     * @param e the mouse event triggered by the user interaction.
     */
    private void handleSVPick(MouseEvent e) {
        double x = clamp(e.getX(), 0, SV_WIDTH - 1);
        double y = clamp(e.getY(), 0, SV_HEIGHT - 1);
        saturation = x / SV_WIDTH;
        brightness = 1 - (y / SV_HEIGHT);
        svPicker.setCenterX(x);
        svPicker.setCenterY(y);
        updateColor();
    }

    /**
     * Updates the color preview and color property based on the current hue, saturation, and brightness values.
     * <p>
     * The method calculates the current color from the hue, saturation, and brightness values, updates the color preview
     * rectangle, and sets the {@link #colorProperty} to the color's corresponding HEX code.
     * </p>
     */
    private void updateColor() {
        Color c = getCurrentColor();
        colorPreview.setFill(c);
        colorProperty.set(toHex(c));
    }

    /**
     * Updates the positions of the hue and SV pickers based on the current hue, saturation, and brightness values.
     * <p>
     * The method sets the hue picker position relative to the current hue value, and the SV picker position relative to
     * the current saturation and brightness values.
     * </p>
     */
    private void updatePickers() {
        huePicker.setCenterX((hue / 360) * HUE_WIDTH);
        huePicker.setCenterY(HUE_HEIGHT / 2);
        svPicker.setCenterX(saturation * SV_WIDTH);
        svPicker.setCenterY((1 - brightness) * SV_HEIGHT);
    }

    /**
     * Calculates the current color based on the hue, saturation, and brightness values.
     *
     * @return the color represented by the current hue, saturation, and brightness values.
     */
    private Color getCurrentColor() {
        return Color.hsb(hue, saturation, brightness);
    }

    /**
     * Converts a {@link javafx.scene.paint.Color} object to its HEX string representation.
     * <p>
     * The color is converted to a string in the format "#RRGGBB".
     * </p>
     *
     * @param c the color to convert.
     * @return the HEX string representation of the color.
     */
    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    /**
     * Clamps a value between a minimum and a maximum value.
     * <p>
     * The value is adjusted such that it will not exceed the given range. If the value is smaller than the minimum,
     * the minimum value is returned. If it is larger than the maximum, the maximum value is returned.
     * </p>
     *
     * @param value the value to clamp.
     * @param min   the minimum allowable value.
     * @param max   the maximum allowable value.
     * @return the clamped value.
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
