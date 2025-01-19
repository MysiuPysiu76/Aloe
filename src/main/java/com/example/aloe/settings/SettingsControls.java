package com.example.aloe.settings;

import com.example.aloe.Translator;
import com.example.aloe.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SettingsControls {

    static HBox getMenuButton(FontIcon icon, String titleKey, String descriptionKey) {
        icon.setIconSize(21);
        VBox iconPane = new VBox(icon);
        iconPane.setPadding(new Insets(25, 20, 25, 30));
        Label title = new Label(Translator.translate(titleKey));
        VBox.setMargin(title, new Insets(0, 0, -4, -1));
        title.setStyle("-fx-font-size: 17px");
        Label description = new Label(Translator.translate(descriptionKey));
        description.setStyle("-fx-font-size: 12px");
        VBox descriptionVBox = new VBox(title, description);
        descriptionVBox.setAlignment(Pos.CENTER_LEFT);
        HBox box = new HBox(iconPane, descriptionVBox, getSpacer(), getMenuArrow());
        VBox.setMargin(box, new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(500);
        box.setPrefWidth(700);
        box.setMaxWidth(750);
        box.setStyle("-fx-background-color: #dfdbdb;-fx-background-radius: 10px");
        return box;
    }

    private static FontIcon getMenuArrow() {
        FontIcon icon = FontIcon.of(FontAwesome.ANGLE_RIGHT);
        icon.setIconSize(27);
        HBox.setMargin(icon, new Insets(0, 30, 0, 30));
        return icon;
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
        choiceBox.getSelectionModel().select(Utils.getKeyIndex(items, SettingsManager.getSetting(SettingsManager.getCategory(), key)));
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
        } else {
            throw new IllegalArgumentException("Values must contain an even number of elements.");
        }
        return map;
    }

    static Label getTitleLabel(String title) {
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 25px;");
        VBox.setMargin(label, new Insets(0, 10, 20, 10));
        return label;
    }

    static TextField getTextField(String key) {
        TextField textField = new TextField(SettingsManager.getSetting(SettingsManager.getCategory(), key));
        textField.setPadding(new Insets(5, 7, 5, 7));
        textField.setOnKeyReleased(event -> {
            SettingsManager.setSetting(SettingsManager.getCategory(), key, textField.getText());});
        textField.setPromptText(Translator.translate("files-menu.example-path"));
        HBox.setMargin(textField, new Insets(0, 20, 0, 20));
        return textField;
    }

    static HBox getSlider(String key, double min, double max, double tickUnit, double step, String leftTitle, String rightTitle, boolean pointedValues, List<Double> values) {
        Slider slider = new Slider(min, max, SettingsManager.getSetting(SettingsManager.getCategory(), "file-box-size"));
        slider.setMajorTickUnit(tickUnit);
        slider.setBlockIncrement(step);
        Label left = new Label(Translator.translate(leftTitle));
        Label right = new Label(Translator.translate(rightTitle));
        HBox box = new HBox(left, slider, right);
        box.setAlignment(Pos.CENTER);
        if (pointedValues) {
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double closest = values.stream().min((a, b) -> Double.compare(Math.abs(a - newValue.doubleValue()), Math.abs(b - newValue.doubleValue()))).orElse(newValue.doubleValue());
                slider.setValue(closest);
                SettingsManager.setSetting(SettingsManager.getCategory(), key, closest);
            });
        }
        HBox.setMargin(slider, new Insets(0, 7, 0, 7));
        HBox.setMargin(box, new Insets(0, 20, 0, 20));
        return box;
    }

    public static Region getSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}