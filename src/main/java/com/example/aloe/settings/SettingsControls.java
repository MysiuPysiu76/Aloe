package com.example.aloe.settings;

import com.example.aloe.Translator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

class SettingsControls {

    static Button getMenuButton(String key, FontIcon icon) {
        icon.setIconSize(20);
        Button button = new Button(Translator.translate(key), icon);
        VBox.setMargin(button, new Insets(0));
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setPadding(new Insets(0, 0, 0, 15));
        button.setGraphicTextGap(10);
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

    static ChoiceBox<Map.Entry<String, String>> getChoiceBox(String key, String ...values) {
        ChoiceBox<Map.Entry<String, String>> choiceBox = new ChoiceBox<>();
        Map<String, String> items = getMapFromValues(values);
        HBox.setMargin(choiceBox, new Insets(0, 20, 0, 20));
        choiceBox.getItems().addAll(items.entrySet());
        choiceBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Map.Entry<String, String> entry) {
                return entry.getValue();
            }
            @Override
            public Map.Entry<String, String> fromString(String string) {
                return null;
            }
        });
        choiceBox.getItems().stream()
            .filter(entry -> entry.getKey().equals((String) SettingsManager.getSetting("files", "start-folder")))
            .findFirst()
            .ifPresentOrElse(entry -> choiceBox.getSelectionModel().select(entry), () -> choiceBox.getSelectionModel().selectFirst());
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SettingsManager.setSetting(SettingsManager.getCategory(), key, newValue.getKey());
            }
        });
        return choiceBox;
    }

    private static Map<String, String> getMapFromValues(String ...values) {
        Map<String, String> map = new HashMap<>();
        if (values.length % 2 == 0) {
            for (int i = 0; i < values.length; i += 2) {
                map.put(values[i], values[i + 1]);
            }
        }
        return map;
    }

    static Label getTitleLabel(String title) {
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 25px;");
        VBox.setMargin(label, new Insets(30, 10, 20, 10));
        return label;
    }

    static TextField getTextField(String key) {
        TextField textField = new TextField(SettingsManager.getSetting(SettingsManager.getCategory(), key));
        textField.setPadding(new Insets(5, 7, 5, 7));
        textField.setOnKeyReleased(event -> {
            System.out.println(textField.getText());
            SettingsManager.setSetting(SettingsManager.getCategory(), key, textField.getText());});
        textField.setPromptText(Translator.translate("files-menu.example-path"));
        HBox.setMargin(textField, new Insets(0, 20, 0, 20));
        return textField;
    }
}