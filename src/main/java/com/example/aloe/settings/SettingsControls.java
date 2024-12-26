package com.example.aloe.settings;

import com.example.aloe.Translator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

class SettingsControls {

    static Button getMenuButton(String key, FontIcon icon) {
        Button button = new Button(Translator.translate(key), icon);
        VBox.setMargin(button, new Insets(0));
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setPadding(new Insets(0, 0, 0, 15));
        button.setGraphicTextGap(7);
        button.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER_LEFT;-fx-border-radius: 0px; -fx-background-radius: 0px;");
        return button;
    }

    static ToggleSwitch getToggleSwitch(String key) {
        ToggleSwitch toggleSwitch = new ToggleSwitch();
        toggleSwitch.setSelected(SettingsManager.getSetting(SettingsManager.getCategory(), key));
        toggleSwitch.setOnMouseClicked(event -> {
            SettingsManager.setSetting(SettingsManager.getCategory(), key, toggleSwitch.isSelected());
        });
        HBox.setMargin(toggleSwitch, new Insets(0, 20, 0, 20));
        return toggleSwitch;
    }

    static ChoiceBox<String> getChoiceBox(String key, String ...values) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        Map<String, String> items = new HashMap<>();
        if (values.length % 2 == 0) {
            for (int i = 0; i < values.length; i += 2) {
                items.put(values[i], values[i + 1]);
            }
        }
        choiceBox.getItems().addAll(items.values());
        choiceBox.getSelectionModel().select(SettingsManager.getSetting(SettingsManager.getCategory(), key));

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.setSetting(SettingsManager.getCategory(), key, items.entrySet().stream()
                .filter(entry -> entry.getValue().equals(newValue))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null));
        });
        return choiceBox;
    }
}