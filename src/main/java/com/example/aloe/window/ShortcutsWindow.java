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

/**
 * A JavaFX modal window that displays a list of predefined keyboard shortcuts.
 * Each shortcut is visually grouped with its description and displayed in a styled layout.
 *
 * <p>This window is typically used as a help or reference panel for end-users,
 * showing combinations such as <kbd>Ctrl + C</kbd>, <kbd>F5</kbd>, etc., along with their meanings.
 *
 * <p>Shortcuts and their corresponding labels are internationalized using the {@link Translator} utility.
 *
 * @since 2.6.2
 */
public class ShortcutsWindow extends Stage {

    /**
     * Constructs a new {@code ShortcutsWindow} and displays it immediately.
     * The window is initialized with the current application theme and includes
     * a list of shortcut descriptions and their associated key combinations.
     */
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

    /**
     * Creates and returns the main container for shortcut rows.
     *
     * @return a VBox styled as a container
     */
    private VBox getContainer() {
        VBox container = new VBox();
        container.getStyleClass().addAll("background", "container");
        return container;
    }

    /**
     * Constructs a single row containing a visual representation of a shortcut
     * and its corresponding label.
     *
     * @param shortcuts the list of key strings (e.g., "Ctrl", "C")
     * @param key       the translation key for the shortcut description
     * @return an HBox containing the formatted shortcut and title
     */
    private HBox getShortcutRow(List<String> shortcuts, String key) {
        HBox row = new HBox();
        row.getStyleClass().add("row");
        row.getChildren().addAll(getShortcut(shortcuts), new HBoxSpacer(), getTitleLabel(key));
        return row;
    }

    /**
     * Constructs a visual representation of a keyboard shortcut,
     * including separators between keys (e.g., "+" icons).
     *
     * @param shortcut a list of key segments to display
     * @return an HBox containing the full shortcut
     */
    private HBox getShortcut(List<String> shortcut) {
        HBox box = new HBox();
        box.getStyleClass().add("row");
        for (byte i = 0; i < shortcut.size(); i++) {
            box.getChildren().addAll(getShortcutBox(shortcut.get(i)), getPlus());
        }
        box.getChildren().removeLast(); // Remove last "+" sign
        return box;
    }

    /**
     * Wraps a single key label in a styled box.
     *
     * @param shortcut the key to display
     * @return a VBox styled as a keyboard key
     */
    private VBox getShortcutBox(String shortcut) {
        VBox box = new VBox(getShortcutLabel(shortcut));
        box.getStyleClass().add("box");
        return box;
    }

    /**
     * Returns a small "+" icon to visually connect key segments.
     *
     * @return a FontIcon representing a plus sign
     */
    private FontIcon getPlus() {
        FontIcon icon = FontIcon.of(FontAwesome.PLUS);
        icon.getStyleClass().add("font-icon");
        icon.setIconSize(11);
        HBox.setMargin(icon, new Insets(7, 11, 7, 11));
        return icon;
    }

    /**
     * Returns a styled label for a shortcut description, based on the translation key.
     *
     * @param key the i18n key for the shortcut's action (e.g., "window.shortcuts.copy")
     * @return a styled Label component
     */
    private Label getTitleLabel(String key) {
        Label label = new Label(Translator.translate(key));
        label.getStyleClass().addAll("text", "title");
        return label;
    }

    /**
     * Returns a styled label for a single key within the shortcut.
     *
     * @param text the key to display (e.g., "Ctrl")
     * @return a Label representing the key
     */
    private Label getShortcutLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().addAll("text");
        return label;
    }

    /**
     * Provides a list of i18n translation keys for each supported shortcut action.
     *
     * @return a list of title keys for each shortcut
     */
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

    /**
     * Provides the key combinations for each shortcut as a list of key sequences.
     *
     * @return a list of shortcut key combinations
     */
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
