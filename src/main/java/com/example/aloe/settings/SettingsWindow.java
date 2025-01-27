package com.example.aloe.settings;

import com.example.aloe.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SettingsWindow extends Stage {
    private static ScrollPane settings;

    public SettingsWindow() {
        settings = new ScrollPane();
        settings.setFitToWidth(true);
        settings.setPadding(new Insets(0, 65, 5, 65));

        HBox.setHgrow(settings, Priority.ALWAYS);

        Scene scene = new Scene(settings, 900, 560);
        this.setTitle(Translator.translate("window.settings.title"));
        this.setScene(scene);
        this.setMinWidth(700);
        this.setMinHeight(500);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOnCloseRequest(event -> System.gc());
        this.setResizable(false);
        loadMenu();
    }

    private static void loadMenu() {
        Label titleLabel = SettingsControls.getTitleLabel(Translator.translate("window.settings.title"));
        titleLabel.setPadding(new Insets(20, 0, 0, 0));

        HBox optionMenu = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.BARS), "window.settings.menu", "window.settings.menu.description");
        optionMenu.setOnMouseClicked(event -> {
            loadMenuSettings();
        });

        HBox optionFiles = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.FILE_TEXT_O), "window.settings.files", "window.settings.files.description");
        optionFiles.setOnMouseClicked(event -> {
            loadFilesSettings();
        });

        HBox optionsLanguage = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.GLOBE), "window.settings.language", "window.settings.language.description");
        optionsLanguage.setOnMouseClicked(event -> {
            loadLanguageSettings();
        });

        HBox optionsAppearance = SettingsControls.getMenuButton(FontIcon.of(FontAwesome.PAINT_BRUSH), "window.settings.appearance", "window.settings.appearance.description");
        optionsAppearance.setOnMouseClicked(event -> {
            loadAppearanceSettings();
        });

        VBox content = new VBox(titleLabel, optionMenu, optionFiles, optionsLanguage, optionsAppearance);
        content.setMaxWidth(700);
        VBox root = new VBox(content);
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);
        HBox.setHgrow(root, Priority.ALWAYS);
        settings.setContent(root);
    }

    private static HBox getSettingBox(String key, Node control) {
        Label title = getSettingLabel(key);
        HBox box = new HBox(title, SettingsControls.getSpacer(), control);
        box.setSpacing(10);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(1000);
        return box;
    }

    private static VBox getSettingBox(String key, Node control, String key1, Node control1) {
        VBox box = new VBox();
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        box.setAlignment(Pos.CENTER);
        Line line = new Line();
        VBox.setVgrow(line, Priority.ALWAYS);
        VBox.setMargin(line, new Insets(0, 0, 0, 18));
        line.setStroke(Color.rgb(185, 185, 185));
        line.endXProperty().bind(box.widthProperty().subtract(36));
        box.getChildren().addAll(getSettingBox(key, control), line, getSettingBox(key1, control1));
        return box;
    }

    private static Label getSettingLabel(String key) {
        Label title = new Label(Translator.translate(key));
        title.setPadding(new Insets(4, 20, 4, 20));
        title.setStyle("-fx-font-size: 14px;");
        title.setAlignment(Pos.CENTER_LEFT);
        return title;
    }

    private static VBox getContentBox(Node... nodes) {
        VBox content = new VBox(nodes);
        content.setSpacing(10);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);
        return content;
    }

    static Button getBackToMenuButton() {
        FontIcon icon = new FontIcon(FontAwesome.ANGLE_LEFT);
        icon.setIconSize(25);

        Button button = new Button(Translator.translate("window.settings.back-to-menu").intern(), icon);
        button.setStyle("-fx-font-size: 15px; -fx-background-color: transparent; -fx-border-color: transparent;");
        button.setGraphicTextGap(8);

        button.setOnAction(event -> {
            loadMenu();
        });
        return button;
    }

    private static void loadMenuSettings() {
        SettingsManager.setCategory("menu");

        settings.setContent(getContentBox(getBackToMenuButton(),
                SettingsControls.getTitleLabel(Translator.translate("window.settings.menu")),
                getSettingBox("window.settings.menu.use-menu", SettingsControls.getToggleSwitch("use-menu")),
                getSettingBox("window.settings.menu.menu-position", SettingsControls.getChoiceBox("position", "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"))),
                getSettingBox("window.settings.menu.use-icon", SettingsControls.getToggleSwitch("use-icon")),
                getSettingBox("window.settings.menu.use-text", SettingsControls.getToggleSwitch("use-text"))));
    }

    private static void loadFilesSettings() {
        SettingsManager.setCategory("files");

        ChoiceBox<Map.Entry<String, String>> startFolder = SettingsControls.getChoiceBox("start-folder", "home", Translator.translate("window.settings.files.start-folder.home"), "last", Translator.translate("window.settings.files.start-folder.last"), "custom", Translator.translate("window.settings.files.start-folder.custom"));
        TextField pathInput = SettingsControls.getTextField("start-folder-location");
        pathInput.setEditable(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom"));
        startFolder.setOnAction(event -> {
            if (startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom")) {
                pathInput.setEditable(true);
            } else {
                pathInput.setEditable(false);
            }
        });

        settings.setContent(getContentBox(getBackToMenuButton(),
                SettingsControls.getTitleLabel(Translator.translate("window.settings.files")),
                getSettingBox("window.settings.files.show-hidden-files", SettingsControls.getToggleSwitch("show-hidden")),
                getSettingBox("window.settings.files.view", SettingsControls.getChoiceBox("view", "grid", Translator.translate("window.settings.files.view.grid"), "list", Translator.translate("window.settings.files.view.list"))),
                getSettingBox("window.settings.files.use-binary-units", SettingsControls.getToggleSwitch("use-binary-units")),
                getSettingBox("window.settings.files.start-folder", startFolder, "window.settings.files.start-folder-location", pathInput),
                getSettingBox("window.settings.files.delete-archive-after-extract", SettingsControls.getToggleSwitch("delete-archive-after-extract")),
                getSettingBox("window.settings.files.display-directories-before-files", SettingsControls.getToggleSwitch("display-directories-before-files")),
                getSettingBox("window.settings.files.file-box-size", SettingsControls.getSlider("file-box-size", 0.6, 2.0, 1.0, 0.1, "window.settings.files.file-box-size.small", "window.settings.files.file-box-size.large", true, IntStream.rangeClosed(5, 45).mapToDouble(i -> i / 10.0).boxed().collect(Collectors.toList()))),
                getSettingBox("window.settings.files.display-thumbnails", SettingsControls.getToggleSwitch("display-thumbnails"))));
    }

    private static void loadLanguageSettings() {
        SettingsManager.setCategory("language");

        settings.setContent(getContentBox(getBackToMenuButton(),
                SettingsControls.getTitleLabel(Translator.translate(Translator.translate("window.settings.language"))),
                getSettingBox("window.settings.language", SettingsControls.getChoiceBox("lang", "en", "English", "pl", "Polski"))));
    }

    private static void loadAppearanceSettings() {
        SettingsManager.setCategory("appearance");

        settings.setContent(getContentBox(getBackToMenuButton(),
                SettingsControls.getTitleLabel(Translator.translate(Translator.translate("window.settings.appearance"))),
                getSettingBox("window.settings.appearance.theme", SettingsControls.getChoiceBox("theme", "light", Translator.translate("window.settings.appearance.theme.light"), "dark", Translator.translate("window.settings.appearance.theme.dark")))));
    }
}