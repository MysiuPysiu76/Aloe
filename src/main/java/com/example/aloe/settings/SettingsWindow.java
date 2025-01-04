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

    private HBox getSettingBox(String key) {
        Label title = getSettingLabel(key);
        HBox box = new HBox(title, getSpacer());
        box.setSpacing(10);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private VBox getDoubleSettingBox(String key, String key1) {
        VBox box = new VBox();
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        box.setAlignment(Pos.CENTER);
        Line line = new Line();
        VBox.setVgrow(line, Priority.ALWAYS);
        VBox.setMargin(line, new Insets(0, 0, 0, 18));
        line.setStroke(Color.rgb(185, 185, 185));
        line.endXProperty().bind(box.widthProperty().subtract(36));
        box.getChildren().addAll(getSettingBox(key), line, getSettingBox(key1));
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

        HBox useMenuSection = getSettingBox("window.settings.menu.use-menu");
        ToggleSwitch useMenu = SettingsControls.getToggleSwitch("use-menu");
        useMenuSection.getChildren().add(useMenu);

        HBox menuPositionSection = getSettingBox("window.settings.menu.menu-position");
        ChoiceBox<Map.Entry<String, String>> menuPosition = SettingsControls.getChoiceBox("position", "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"));
        menuPositionSection.getChildren().add(menuPosition);

        HBox useIconsSection = getSettingBox("window.settings.menu.use-icon");
        ToggleSwitch useIcon = SettingsControls.getToggleSwitch("use-icon");
        useIconsSection.getChildren().add(useIcon);

        HBox useTextSection = getSettingBox("window.settings.menu.use-text");
        ToggleSwitch useText = SettingsControls.getToggleSwitch("use-text");
        useTextSection.getChildren().add(useText);

        settings.setContent(getContentBox(SettingsControls.getTitleLabel(Translator.translate("window.settings.menu")), useMenuSection, menuPositionSection, useIconsSection, useTextSection));
    }

    private void loadFilesSettings() {
        SettingsManager.setCategory("files");

        HBox showHiddenFilesSection = getSettingBox("window.settings.files.show-hidden-files");
        ToggleSwitch showHiddenFiles = SettingsControls.getToggleSwitch("show-hidden");
        showHiddenFilesSection.getChildren().add(showHiddenFiles);

        HBox useBinaryUnitsSection = getSettingBox("window.settings.files.use-binary-units");
        ToggleSwitch useBinaryUnits = SettingsControls.getToggleSwitch("use-binary-units");
        useBinaryUnitsSection.getChildren().add(useBinaryUnits);

        VBox startFolderSection = getDoubleSettingBox("window.settings.files.start-folder", "window.settings.files.start-folder-location");
        ChoiceBox<Map.Entry<String, String>> startFolder = SettingsControls.getChoiceBox("start-folder", "home", Translator.translate("window.settings.files.start-folder.home"), "last", Translator.translate("window.settings.files.start-folder.last"), "custom", Translator.translate("window.settings.files.start-folder.custom"));
        ((HBox)(startFolderSection.getChildren().get(0))).getChildren().add(startFolder);
        TextField pathInput = SettingsControls.getTextField("start-folder-location");
        pathInput.setEditable(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom"));
        ((HBox)(startFolderSection.getChildren().get(2))).getChildren().add(pathInput);
        startFolder.setOnAction(event -> {
            if(startFolder.getSelectionModel().getSelectedItem().toString().substring(0, startFolder.getSelectionModel().getSelectedItem().toString().indexOf("=")).equals("custom")) {
                pathInput.setEditable(true);
            } else {
                pathInput.setEditable(false);
            }
        });

        HBox deleteArchiveAfterExtractSection = getSettingBox("window.settings.files.delete-archive-after-extract");
        ToggleSwitch deleteArchiveAfterExtract = SettingsControls.getToggleSwitch("delete-archive-after-extract");
        deleteArchiveAfterExtractSection.getChildren().add(deleteArchiveAfterExtract);

        HBox fileBoxSizeSection = getSettingBox("window.settings.files.file-box-size");
        HBox fileBoxSize = SettingsControls.getSlider("file-box-size", 0.6, 2.0, 1.0, 0.1, "window.settings.files.file-box-size.small", "window.settings.files.file-box-size.large", true, IntStream.rangeClosed(5, 45).mapToDouble(i -> i / 10.0).boxed().collect(Collectors.toList()));
        fileBoxSizeSection.getChildren().add(fileBoxSize);

        HBox displayThumbnailsSection = getSettingBox("window.settings.files.display-thumbnails");
        ToggleSwitch displayThumbnails = SettingsControls.getToggleSwitch("display-thumbnails");
        displayThumbnailsSection.getChildren().add(displayThumbnails);

        settings.setContent(getContentBox(SettingsControls.getTitleLabel(Translator.translate("window.settings.files")), showHiddenFilesSection, useBinaryUnitsSection, startFolderSection, deleteArchiveAfterExtractSection, fileBoxSizeSection, displayThumbnailsSection));
    }

}