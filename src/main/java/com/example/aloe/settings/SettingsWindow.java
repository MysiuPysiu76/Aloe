package com.example.aloe.settings;

import com.example.aloe.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SettingsWindow extends Stage {
    ScrollPane menu;
    ScrollPane settings;

    public SettingsWindow() {
        loadMenu();

        settings = new ScrollPane();
        loadMenuSettings();
        HBox root = new HBox(menu, settings);
        settings.setFitToWidth(true);
        HBox.setHgrow(settings, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 560);
        this.setTitle(Translator.translate("window.settings.title"));
        this.setScene(scene);
        this.setMinWidth(700);
        this.setMinHeight(380);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOnCloseRequest(event -> System.gc());
    }

    private void loadMenu() {
        Button menuButton = SettingsControls.getMenuButton("window.settings.menu", FontIcon.of(FontAwesome.BARS));
        menuButton.setOnAction(event -> loadMenuSettings());
        Button filesButton = SettingsControls.getMenuButton("window.settings.files", FontIcon.of(FontAwesome.FILE_TEXT_O));
        filesButton.setOnAction(event -> loadFilesSettings());
        this.menu = new ScrollPane(new VBox(menuButton, filesButton));
        menu.setFitToWidth(true);
        menu.setMaxWidth(200);
        menu.setMinWidth(200);
    }

    private VBox getSettingBox(String key, Node control, String key1, Node control1) {
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

    private HBox getSettingBox(String key, Node control) {
        Label title = getSettingLabel(key);
        HBox box = new HBox(title, getSpacer(), control);
        box.setSpacing(10);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private Label getSettingLabel(String key) {
        Label title = new Label(Translator.translate(key));
        title.setPadding(new Insets(4, 20, 4, 20));
        title.setStyle("-fx-font-size: 14px;");
        title.setAlignment(Pos.CENTER_LEFT);
        return title;
    }

    private VBox getContentBox(Node...nodes) {
        VBox content = new VBox(nodes);
        content.setSpacing(10);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);
        return content;
    }

    private Region getSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void loadMenuSettings() {
        SettingsManager.setCategory("menu");

        settings.setContent(getContentBox(SettingsControls.getTitleLabel(Translator.translate("window.settings.menu")),
            getSettingBox("window.settings.menu.use-menu", SettingsControls.getToggleSwitch("use-menu")),
            getSettingBox("window.settings.menu.menu-position", SettingsControls.getChoiceBox("position", "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"))),
            getSettingBox("window.settings.menu.use-icon", SettingsControls.getToggleSwitch("use-icon")),
            getSettingBox("window.settings.menu.use-text", SettingsControls.getToggleSwitch("use-text"))));
    }

    private void loadFilesSettings() {
        SettingsManager.setCategory("files");

        ChoiceBox<Map.Entry<String, String>> startFolder = SettingsControls.getChoiceBox("start-folder", "home", Translator.translate("window.settings.files.start-folder.home"), "last", Translator.translate("window.settings.files.start-folder.last"), "custom", Translator.translate("window.settings.files.start-folder.custom"));
        TextField pathInput = SettingsControls.getTextField("start-folder-location");
        pathInput.setEditable(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom"));
        startFolder.setOnAction(event -> {
            if(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom")) {
                pathInput.setEditable(true);
            } else {
                pathInput.setEditable(false);
            }
        });

        settings.setContent(getContentBox(SettingsControls.getTitleLabel(Translator.translate("window.settings.files")),
            getSettingBox("window.settings.files.show-hidden-files", SettingsControls.getToggleSwitch("show-hidden")),
            getSettingBox("window.settings.files.view", SettingsControls.getChoiceBox("view", "grid", Translator.translate("window.settings.files.view.grid"), "list", Translator.translate("window.settings.files.view.list"))),
            getSettingBox("window.settings.files.use-binary-units", SettingsControls.getToggleSwitch("use-binary-units")),
            getSettingBox("window.settings.files.start-folder", startFolder, "window.settings.files.start-folder-location", pathInput),
            getSettingBox("window.settings.files.delete-archive-after-extract", SettingsControls.getToggleSwitch("delete-archive-after-extract")),
            getSettingBox("window.settings.files.file-box-size", SettingsControls.getSlider("file-box-size", 0.6, 2.0, 1.0, 0.1, "window.settings.files.file-box-size.small", "window.settings.files.file-box-size.large", true, IntStream.rangeClosed(5, 45).mapToDouble(i -> i / 10.0).boxed().collect(Collectors.toList()))),
            getSettingBox("window.settings.files.display-thumbnails", SettingsControls.getToggleSwitch("display-thumbnails"))));
    }

}