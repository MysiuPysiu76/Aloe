package com.example.aloe.settings;

import com.example.aloe.components.draggable.DraggablePane;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.ConfirmWindow;
import com.example.aloe.window.MainWindow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.aloe.settings.SettingsControls.*;

public class SettingsWindow extends Stage {

    private static ScrollPane settings;
    private static boolean isRestartRequired;

    public SettingsWindow() {
        isRestartRequired = false;
        settings = new ScrollPane();
        settings.getStyleClass().addAll("background", "root");
        settings.setFitToWidth(true);

        Scene scene = new Scene(settings, 900, 560);
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + Settings.getTheme() + "/settings.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/settings.css").toExternalForm());
        scene.getStylesheets().add(String.format("data:text/css, .choice-box .menu-item:hover, .choice-box .arrow, .choice-box .menu-item:focused, .slider .thumb { -fx-background-color: %s; } .text-field { -fx-highlight-fill: %s; }", Settings.getColor(), Settings.getColor(), Settings.getColor()));

        this.setTitle(Translator.translate("window.settings.title"));
        this.setScene(scene);
        this.setMinWidth(700);
        this.setMinHeight(500);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.show();
        loadMenu();
        setOnClose();
    }

    static void setRestartRequired() {
        isRestartRequired = true;
    }

    static void loadMenu() {
        Label titleLabel = SettingsControls.getTitleLabel(Translator.translate("window.settings.title"));
        titleLabel.setPadding(new Insets(20, 0, 0, 0));

        HBox optionMenu = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.BARS), "window.settings.menu", "window.settings.menu.description");
        optionMenu.setOnMouseClicked(event -> loadMenuSettings());

        HBox optionFiles = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.FILE_TEXT_O), "window.settings.files", "window.settings.files.description");
        optionFiles.setOnMouseClicked(event -> loadFilesSettings());

        HBox optionsLanguage = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.GLOBE), "window.settings.language", "window.settings.language.description");
        optionsLanguage.setOnMouseClicked(event -> loadLanguageSettings());

        HBox optionsAppearance = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.PAINT_BRUSH), "window.settings.appearance", "window.settings.appearance.description");
        optionsAppearance.setOnMouseClicked(event -> loadAppearanceSettings());

        VBox content = new VBox(titleLabel, optionMenu, optionFiles, optionsLanguage, optionsAppearance);
        content.getStyleClass().add("background");
        content.setPadding(new Insets(0, 0, 100, 0));
        content.setMaxWidth(700);
        VBox root = new VBox(content);
        root.getStyleClass().add("background");
        root.setAlignment(Pos.TOP_CENTER);
        root.setFillWidth(true);
        HBox.setHgrow(root, Priority.ALWAYS);
        settings.setContent(root);
        settings.setFitToHeight(true);
    }

    private static void loadMenuSettings() {
        Settings.setCategory("menu");
        settings.setFitToHeight(false);

        ChoiceBox<Map.Entry<String, String>> menuPosition = SettingsControls.getChoiceBox("position", true, "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"));
        AtomicReference<String> position = new AtomicReference<>(Settings.getSetting("menu", "position"));
        menuPosition.setOnAction(e -> {
            String newPosition = Settings.getSetting("menu", "position");
            if (!newPosition.equals(position)) {
                position.set(newPosition);
                double current = Settings.getSetting("menu", "divider-position");
                Settings.setSetting("menu", "divider-position", 1.0 - current);
            }
        });

        DraggablePane pane = SettingsControls.getDraggablePane("items", true);
        pane.add(SettingsControls.getDraggableItems("items"));

        settings.setContent(getContentBox(
                SettingsControls.getTitleLabel(Translator.translate("window.settings.menu")),
                getSettingBox("window.settings.menu.use-menu", SettingsControls.getToggleSwitch("use-menu", true)),
                getSettingBox("window.settings.menu.menu-position", menuPosition),
                getSettingBox("window.settings.menu.use-icon", SettingsControls.getToggleSwitch("use-icon", true)),
                getSettingBox("window.settings.menu.use-text", SettingsControls.getToggleSwitch("use-text", true)),
                getSettingBox("window.settings.menu.icons-page", SettingsControls.getChoiceBox("icon-position", true, "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"))),
                getSettingBox("window.settings.menu.edit-order", "window.settings.menu.item-info", pane, pane.getInfoBox())));
    }

    private static void loadFilesSettings() {
        Settings.setCategory("files");

        ChoiceBox<Map.Entry<String, String>> startFolder = SettingsControls.getChoiceBox("start-folder", false, "home", Translator.translate("window.settings.files.start-folder.home"), "last", Translator.translate("window.settings.files.start-folder.last"), "custom", Translator.translate("window.settings.files.start-folder.custom"));
        TextField pathInput = SettingsControls.getTextField("start-folder-location", Translator.translate("utils.example-path"), false);
        pathInput.setPromptText(Translator.translate("utils.example-path"));
        pathInput.setEditable(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom"));
        startFolder.setOnAction(event -> {
            if (startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom")) {
                pathInput.setEditable(true);
            } else {
                pathInput.setEditable(false);
            }
        });

        settings.setContent(getContentBox(
                SettingsControls.getTitleLabel(Translator.translate("window.settings.files")),
                getSettingBox("window.settings.files.show-hidden-files", SettingsControls.getToggleSwitch("show-hidden", false)),
                getSettingBox("window.settings.files.view", SettingsControls.getChoiceBox("view", false, "grid", Translator.translate("window.settings.files.view.grid"), "list", Translator.translate("window.settings.files.view.list"))),
                getSettingBox("window.settings.files.use-binary-units", SettingsControls.getToggleSwitch("use-binary-units", true)),
                getSettingBox("window.settings.files.start-folder", startFolder, "window.settings.files.start-folder-location", pathInput),
                getSettingBox("window.settings.files.extract-on-click", SettingsControls.getToggleSwitch("extract-on-click", false)),
                getSettingBox("window.settings.files.delete-archive-after-extract", SettingsControls.getToggleSwitch("delete-archive-after-extract", true)),
                getSettingBox("window.settings.files.use-double-click", SettingsControls.getToggleSwitch("use-double-click", false)),
                getSettingBox("window.settings.files.display-directories-before-files", SettingsControls.getToggleSwitch("display-directories-before-files", false)),
                getSettingBox("window.settings.files.file-box-size", SettingsControls.getSlider("file-box-size", 0.6, 2.0, 1.0, 0.1, "window.settings.files.file-box-size.small", "window.settings.files.file-box-size.large", false)),
                getSettingBox("window.settings.files.trash-location", SettingsControls.getTextField("trash", Translator.translate("utils.example-path"), true)),
                getSettingBox("window.settings.files.display-thumbnails", SettingsControls.getToggleSwitch("display-thumbnails", false))));
    }

    private static void loadLanguageSettings() {
        Settings.setCategory("language");

        settings.setContent(getContentBox(
                SettingsControls.getTitleLabel(Translator.translate(Translator.translate("window.settings.language"))),
                getSettingBox("window.settings.language", SettingsControls.getChoiceBox("lang", true, "en", "English", "pl", "Polski", "de", "Deutsch", "fr", "Français", "no", "Norsk", "sv", "Svenska", "is", "Íslenska", "fi", "Suomi", "es", "Español", "pt", "Português", "sk", "Slovenčina", "cs", "Čeština", "da", "Dansk", "it", "Italiano", "ja", "日本語 (Nihongo)", "zh", "中文 (Zhōngwén)", "nl", "Nederlands", "hu", "Magyar", "ru", "Русский", "uk", "Українська", "tr", "Türkçe", "el", "Ελληνικά"))));
    }

    private static void loadAppearanceSettings() {
        Settings.setCategory("appearance");

        settings.setContent(getContentBox(
                SettingsControls.getTitleLabel(Translator.translate(Translator.translate("window.settings.appearance"))),
                getSettingBox("window.settings.appearance.theme", SettingsControls.getChoiceBox("theme", true, "light", Translator.translate("window.settings.appearance.theme.light"), "dark", Translator.translate("window.settings.appearance.theme.dark"))),
                getSettingBox("window.settings.appearance.color", SettingsControls.getColorChooser("color", true))));
    }

    private void setOnClose() {
        this.setOnCloseRequest(e -> {
            if (isRestartRequired) {
                new ConfirmWindow(Translator.translate("window.settings.confirm.title"), Translator.translate("window.settings.confirm.description"), Translator.translate("button.restart"), event -> {
                    Platform.runLater(() -> {
                        MainWindow.getStage().close();
                        Settings.loadSettings();
                        Translator.reload();
                        MainWindow.create(new Stage());
                        settings = null;
                    });
                });
            }
        });
    }
}
