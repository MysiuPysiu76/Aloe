package com.example.aloe.window;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class ShortcutsWindow extends Stage {

    public ShortcutsWindow() {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxWidth(300);
        root.getStyleClass().add("background");

        VBox container = getContainer();
        root.getChildren().add(container);

        List<String> titleList = getTitleList();
        List<List<String>> shortcutsList = getShortcutsList();

        for (int i = 0; i < titleList.size(); i++) {
            container.getChildren().add(getShortcutRow(shortcutsList.get(i), titleList.get(i)));
        }

        Scene scene = new Scene(root, 300, 495);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/shortcuts.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/shortcuts.css").toExternalForm());
        this.setScene(scene);
        this.setTitle(Translator.translate("window.shortcuts"));
        this.setMinHeight(130);
        this.setMinWidth(420);
        this.initModality(Modality.WINDOW_MODAL);
        this.show();
    }

    private VBox getContainer() {
        VBox container = new VBox();
        container.getStyleClass().addAll("background", "container");
        return container;
    }

    private HBox getShortcutRow(List<String> shortcuts, String key) {
        HBox row = new HBox();
        row.getStyleClass().add("row");
        row.getChildren().addAll(getShortcut(shortcuts), new HBoxSpacer(), getTitleLabel(key));
        return row;
    }

    private HBox getShortcut(List<String> shortcut) {
        HBox box = new HBox();
        box.getStyleClass().add("row");
        for (byte i = 0; i < shortcut.size(); i++) {
            box.getChildren().addAll(getShortcutBox(shortcut.get(i)), getPlus());
        }
        box.getChildren().removeLast();
        return box;
    }

    private VBox getShortcutBox(String shortcut) {
        VBox box = new VBox(getShortcutLabel(shortcut));
        box.getStyleClass().add("box");
        return box;
    }

    private FontIcon getPlus() {
        FontIcon icon = FontIcon.of(FontAwesome.PLUS);
        icon.getStyleClass().add("font-icon");
        icon.setIconSize(11);
        HBox.setMargin(icon, new Insets(7, 11, 7, 11));
        return icon;
    }

    private Label getTitleLabel(String key) {
        Label label = new Label(Translator.translate(key));
        label.getStyleClass().addAll("text", "title");
        return label;
    }

    private Label getShortcutLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().addAll("text");
        return label;
    }

    private List<String> getTitleList() {
        List<String> titleList = new ArrayList<>();
        titleList.add("window.shortcuts.copy");
        titleList.add("window.shortcuts.cut");
        titleList.add("window.shortcuts.paste");
        titleList.add("window.shortcuts.select-all");
        titleList.add("window.shortcuts.rename");
        titleList.add("window.shortcuts.refresh");
        titleList.add("window.shortcuts.move-to-trash");
        titleList.add("window.shortcuts.delete");
        return titleList;
    }

    private List<List<String>> getShortcutsList() {
        List<List<String>> shortcutsList = new ArrayList<>();
        shortcutsList.add(List.of("Ctrl", "C"));
        shortcutsList.add(List.of("Ctrl", "X"));
        shortcutsList.add(List.of("Ctrl", "V"));
        shortcutsList.add(List.of("Ctrl", "A"));
        shortcutsList.add(List.of("F2"));
        shortcutsList.add(List.of("F5"));
        shortcutsList.add(List.of("Delete"));
        shortcutsList.add(List.of("Shift", "Delete"));
        return shortcutsList;
    }
}
