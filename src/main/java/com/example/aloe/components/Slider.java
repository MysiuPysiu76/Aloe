package com.example.aloe.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Slider extends HBox {

    private final Label leftLabel = new Label();
    private final Label rightLabel = new Label();
    private javafx.scene.control.Slider slider;

    public Slider(double min, double max, double current) {
        super();
        this.slider = new javafx.scene.control.Slider(min, max, current);
        this.getChildren().addAll(leftLabel, slider, rightLabel);
        this.setAlignment(Pos.CENTER);

        HBox.setMargin(slider, new Insets(0, 7, 0, 7));
        HBox.setMargin(this, new Insets(0, 20, 0, 20));
    }

    public void setText(String text, String text1) {
        leftLabel.setText(text);
        leftLabel.getStyleClass().add("text");
        rightLabel.setText(text1);
        rightLabel.getStyleClass().add("text");
    }

    public void setMajorTickUnit(double value) {
        slider.setMajorTickUnit(value);
    }

    public void setBlockIncrement(double value) {
        slider.setBlockIncrement(value);
    }

    public javafx.scene.control.Slider getSlider() {
        return slider;
    }

}