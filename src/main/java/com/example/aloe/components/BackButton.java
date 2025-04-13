package com.example.aloe.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class BackButton extends HBox {

    private Label label;
    private FontIcon icon;

    public BackButton(String text, boolean isLeft) {
        label = new Label(text);
        label.setPadding(new Insets(3, 5, 3, 5));

        if (isLeft) {
            icon = new FontIcon(FontAwesome.ANGLE_LEFT);
        } else {
            icon = new FontIcon(FontAwesome.ANGLE_RIGHT);
        }

        icon.getStyleClass().add("icon");
        icon.setIconSize(25);

        this.getStyleClass().addAll("button", "back-button");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(label);
        this.getChildren().add(isLeft ? 0 : 1, icon);
    }

    public void setColor(String color) {
        label.setStyle("-fx-text-fill: " + color + ";");
        icon.setIconColor(Color.web(color));
    }
}