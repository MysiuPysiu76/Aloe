package com.example.aloe.components;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToggleSwitch extends StackPane {

    private final Pane knob = new Pane();
    private boolean isSelected = false;

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

    public ToggleSwitch(boolean isSelected) {
        this();
        this.setSelected(isSelected);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        animateToggle();
        updateColors();
    }

    private void animateToggle() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), knob);
        if (isSelected) {
            transition.setToX(20);
        } else {
            transition.setToX(0);
        }
        transition.play();
    }

    private void updateColors() {
        this.knob.setStyle("-fx-background-color: #05a6d6; -fx-background-radius: 100px;");
    }

    public void setColor(String color) {
        knob.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 100px;", color));
    }

    public Pane getKnob() {
        return knob;
    }
}