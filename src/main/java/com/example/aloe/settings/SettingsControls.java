package com.example.aloe.settings;

import com.example.aloe.*;
import com.example.aloe.components.*;
import com.example.aloe.components.ColorPicker;
import com.example.aloe.components.Slider;
import com.example.aloe.components.draggable.DraggableItem;
import com.example.aloe.components.draggable.DraggablePane;
import com.example.aloe.components.draggable.InfoBox;
import com.example.aloe.elements.menu.MenuItem;
import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SettingsControls {

    static HBox getMenuButton(FontIcon icon, String titleKey, String descriptionKey) {
        icon.setIconSize(21);
        icon.setIconColor(Color.valueOf(Settings.getColor()));
        VBox iconPane = new VBox(icon);
        iconPane.setPadding(new Insets(25, 20, 25, 30));
        Label title = new Label(Translator.translate(titleKey));
        VBox.setMargin(title, new Insets(0, 0, 0, -1));
        title.getStyleClass().addAll("menu-title", "text");
        Label description = new Label(Translator.translate(descriptionKey));
        description.getStyleClass().addAll("menu-description", "text");
        VBox descriptionVBox = new VBox(title, description);
        descriptionVBox.setAlignment(Pos.CENTER_LEFT);
        HBox box = new HBox(iconPane, descriptionVBox, new HBoxSpacer(), getMenuArrow());
        box.getStyleClass().addAll("box", "cursor-hand");
        VBox.setMargin(box, new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(500);
        box.setPrefWidth(700);
        box.setMaxWidth(750);
        return box;
    }

    private static FontIcon getMenuArrow() {
        FontIcon icon = FontIcon.of(FontAwesome.ANGLE_RIGHT);
        icon.setIconSize(27);
        icon.setIconColor(Color.valueOf(Settings.getColor()));
        HBox.setMargin(icon, new Insets(0, 30, 0, 30));
        return icon;
    }

    static ToggleSwitch getToggleSwitch(String key, boolean restartRequired) {
        ToggleSwitch toggleSwitch = new ToggleSwitch(Boolean.TRUE.equals(Settings.getSetting(Settings.getCategory(), key)));
        toggleSwitch.setColor(Settings.getColor());
        toggleSwitch.setOnMouseClicked(event -> {
            Settings.setSetting(Settings.getCategory(), key, toggleSwitch.isSelected());
            if (restartRequired) SettingsWindow.setRestartRequired();
        });
        HBox.setMargin(toggleSwitch, new Insets(0, 20, 0, 20));
        return toggleSwitch;
    }

    static ChoiceBox<Map.Entry<String, String>> getChoiceBox(String key, boolean restartRequired, String... values) {
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
        choiceBox.getSelectionModel().select(Utils.getKeyIndex(items, Settings.getSetting(Settings.getCategory(), key)));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Settings.setSetting(Settings.getCategory(), key, newValue.getKey());
            }
            if (restartRequired) SettingsWindow.setRestartRequired();
        });

        return choiceBox;
    }

    private static Map<String, String> getMapFromValues(String... values) {
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
        label.getStyleClass().addAll("text", "title");
        VBox.setMargin(label, new Insets(0, 10, 20, 10));
        return label;
    }

    static TextField getTextField(String key, String text, boolean restartRequired) {
        TextField textField = new TextField(Settings.getSetting(Settings.getCategory(), key));
        textField.getStyleClass().add("text");
        textField.setOnKeyReleased(event -> {
            Settings.setSetting(Settings.getCategory(), key, textField.getText());
            if (restartRequired) SettingsWindow.setRestartRequired();
        });
        textField.setPromptText(text);
        textField.setMinWidth(120);
        HBox.setMargin(textField, new Insets(0, 20, 0, 20));
        return textField;
    }

    static Slider getSlider(String key, double min, double max, double tickUnit, double step, String leftTitle, String rightTitle, boolean restartRequired) {
        Slider slider = new Slider(min, max, Settings.getSetting(Settings.getCategory(), "file-box-size"));
        slider.setMajorTickUnit(tickUnit);
        slider.setBlockIncrement(step);
        slider.setText(Translator.translate(leftTitle), Translator.translate(rightTitle));
        slider.getSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (restartRequired) SettingsWindow.setRestartRequired();
            Settings.setSetting(Settings.getCategory(), key, newValue);
        });
        return slider;
    }

    static DraggablePane getDraggablePane(String key, boolean restartRequired) {
        DraggablePane pane = new DraggablePane(330);
        InfoBox infoBox = new InfoBox();
        pane.setInfoBox(infoBox);
        infoBox.setMinWidth(200);
        infoBox.setPrefWidth(300);
        infoBox.setMaxWidth(300);
        Label info = new Label(Translator.translate("window.settings.menu.select-item"));
        info.getStyleClass().add("text");
        infoBox.setContent(info);

        pane.setOnUserChange(() -> {
            if (restartRequired) SettingsWindow.setRestartRequired();
            List<DraggableItem> draggableItems = pane.getItems();
            List<Map<String, String>> values = new ArrayList<>();

            for (DraggableItem item : draggableItems) {
                item.getStyleClass().add("draggable-item");
                values.add(item.getObject().getObjectProperties());
            }
            Settings.setSetting(Settings.getCategory(), key, values);
        });
        return pane;
    }

    static List<DraggableItem> getDraggableItems(String key) {
        List<Map<String, Object>> items = Settings.getSetting(Settings.getCategory(), key);
        List<DraggableItem> draggableItems = new ArrayList<>();
        if (!(items == null || items.isEmpty())) {
            for (Map<String, Object> item : items) {
                DraggableItem draggableItem = new DraggableItem(new MenuItem((String) item.get("icon"), (String) item.get("name"), (String) item.get("path")), (String) item.get("name"));
                draggableItem.getStyleClass().addAll("draggable-item", "text");
                draggableItem.setStyle(String.format("-fx-text-fill: %s;", Settings.getColor()));
                draggableItems.add(draggableItem);
            }
        }
        return draggableItems;
    }

    static ColorChooser getColorChooser(String key, boolean restartRequired) {
        String color = Settings.getSetting(Settings.getCategory(), key);
        ColorChooser colorChooser = new ColorChooser(color, 27, 100);
        HBox.setMargin(colorChooser, new Insets(0, 20, 0, 20));

        ColorPicker picker = new ColorPicker();
        colorChooser.colorProperty.bind(picker.colorProperty);
        picker.colorProperty.set(color);

        VBox wrapper = new VBox(picker);
        wrapper.setPadding(new Insets(10));
        wrapper.getStyleClass().add("background");
        PopOver box = new PopOver(wrapper);
        box.setDetachable(false);
        box.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);

        colorChooser.setOnMouseClicked(e -> {
            if (restartRequired) SettingsWindow.setRestartRequired();
            picker.colorProperty.addListener(((event, oldValue, newValue) -> {
                if (ColorPicker.isColorValid(newValue)) Settings.setSetting(Settings.getCategory(), key, newValue);
            }));
            box.show(colorChooser);
        });

        return colorChooser;
    }

    private static BackButton getBackToMenuButton() {
        BackButton button = new BackButton(Translator.translate("window.settings.back-to-menu"), true);
        button.setOnMouseClicked(e -> SettingsWindow.loadMenu());
        button.setColor(Settings.getColor());
        return button;
    }

    static Label getSettingLabel(String key) {
        Label title = new Label(Translator.translate(key));
        title.setAlignment(Pos.CENTER_LEFT);
        title.getStyleClass().addAll("text", "setting-label");
        return title;
    }

    static VBox getContentBox(Node... nodes) {
        VBox content = new VBox(new HBox(getBackToMenuButton(), new HBoxSpacer()));
        content.getChildren().addAll(nodes);
        content.setSpacing(10);
        content.setFillWidth(true);
        content.setAlignment(Pos.TOP_LEFT);
        content.getStyleClass().addAll("background", "content-box");
        return content;
    }

    static HBox getSettingBox(String key, Node control) {
        HBox box = new HBox(getSettingLabel(key), new HBoxSpacer(), control);
        box.setSpacing(10);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(1000);
        box.getStyleClass().add("box");
        return box;
    }

    static VBox getSettingBox(String key, Node control, String key1, Node control1) {
        VBox box = new VBox();
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        box.setAlignment(Pos.CENTER);
        Line line = new Line();
        VBox.setVgrow(line, Priority.ALWAYS);
        VBox.setMargin(line, new Insets(0, 0, 0, 18));
        line.getStyleClass().add("line");
        box.setMaxWidth(1000);
        box.getStyleClass().add("box");
        line.endXProperty().bind(box.widthProperty().subtract(36));
        box.getChildren().addAll(getSettingBox(key, control), line, getSettingBox(key1, control1));
        return box;
    }

    static HBox getSettingBox(String key, String key1, Node control1, Node control2) {
        Label label1 = getSettingLabel(key);
        label1.setPadding(new Insets(5, 0, 32, 0));
        VBox.setMargin(label1, new Insets(0, 0, 8, -10));
        Label label2 = getSettingLabel(key1);
        label2.setPadding(new Insets(5, 0, 10, 0));

        HBox box = new HBox(new VBox(label1, control1), new VBox(label2, control2));
        box.setPadding(new Insets(10, 30, 20, 20));
        box.setSpacing(20);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(1000);
        box.getStyleClass().add("box");
        return box;
    }
}