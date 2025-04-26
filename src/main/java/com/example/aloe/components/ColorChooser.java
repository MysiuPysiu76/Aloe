package com.example.aloe.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class ColorChooser extends Button {

    public StringProperty colorProperty = new SimpleStringProperty();

    public ColorChooser(String color, double v, double v1) {
        this.setPrefHeight(v);
        this.setPrefWidth(v1);

        this.styleProperty().bind(colorProperty.concat(";").map(k -> "-fx-background-color: " + k));
        this.colorProperty.set(color);
    }

    public String getColor() {
        return colorProperty.get();
    }
}