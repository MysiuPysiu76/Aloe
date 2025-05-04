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

    public static boolean isColorValid(String color) {
        try {
            Color.web(color);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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

    private void drawHueGradient(GraphicsContext gc) {
        for (int x = 0; x < HUE_WIDTH; x++) {
            double h = (x / (double) HUE_WIDTH) * 360;
            Color color = Color.hsb(h, 1.0, 1.0);
            gc.setFill(color);
            gc.fillRect(x, 0, 1, HUE_HEIGHT);
        }
    }

    private void drawSVGradient(GraphicsContext gc) {
        for (int x = 0; x < SV_WIDTH; x++) {
            for (int y = 0; y < SV_HEIGHT; y++) {
                double s = x / (double) SV_WIDTH;
                double v = 1 - (y / (double) SV_HEIGHT);
                Color color = Color.hsb(hue, s, v);
                gc.getPixelWriter().setColor(x, y, color);
            }
        }
    }

    private void handleHuePick(MouseEvent e) {
        double x = clamp(e.getX(), 0, HUE_WIDTH - 1);
        hue = (x / HUE_WIDTH) * 360;
        drawSVGradient(svCanvas.getGraphicsContext2D());
        huePicker.setCenterX(x);
        huePicker.setCenterY(HUE_HEIGHT / 2);
        updateColor();
    }

    private void handleSVPick(MouseEvent e) {
        double x = clamp(e.getX(), 0, SV_WIDTH - 1);
        double y = clamp(e.getY(), 0, SV_HEIGHT - 1);
        saturation = x / SV_WIDTH;
        brightness = 1 - (y / SV_HEIGHT);
        svPicker.setCenterX(x);
        svPicker.setCenterY(y);
        updateColor();
    }

    private void updateColor() {
        Color c = getCurrentColor();
        colorPreview.setFill(c);
        colorProperty.set(toHex(c));
    }

    private void updatePickers() {
        huePicker.setCenterX((hue / 360) * HUE_WIDTH);
        huePicker.setCenterY(HUE_HEIGHT / 2);

        svPicker.setCenterX(saturation * SV_WIDTH);
        svPicker.setCenterY((1 - brightness) * SV_HEIGHT);
    }

    private Color getCurrentColor() {
        return Color.hsb(hue, saturation, brightness);
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
