package com.example.aloe.settings;

import com.example.aloe.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public final class SettingsWindow extends Stage {
    ScrollPane menu;
    ScrollPane settings;

    public SettingsWindow() {
        loadMenu();
        loadMenuSettings();

        HBox root = new HBox(menu, settings);
        Scene scene = new Scene(root, 900, 550);
        this.setTitle(Translator.translate("window.settings.title"));
        this.setScene(scene);
        this.setMinWidth(700);
        this.setMinHeight(350);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOnCloseRequest(event -> System.gc());
    }

    private void loadMenu() {
        Button menuButton = SettingsControls.getMenuButton("window.settings.menu.menu", FontIcon.of(FontAwesome.BARS));
        this.menu = new ScrollPane(new VBox(menuButton));
        menu.setFitToWidth(true);
        menu.setMaxWidth(200);
        menu.setMinWidth(200);
    }

    private void loadMenuSettings() {
        SettingsManager.setCategory("menu");
        Label title = new Label(Translator.translate("window.settings.menu.menu"));
        title.setStyle("-fx-font-size: 25px;");
        VBox.setMargin(title, new Insets(30, 10, 20, 10));

        HBox useMenuSection = getSettingBox("window.settings.menu.use-menu");
        ToggleSwitch useMenu = SettingsControls.getToggleSwitch("use-menu");
        useMenuSection.getChildren().add(useMenu);

        HBox menuPositionSection = getSettingBox("window.settings.menu.menu-position");
        ChoiceBox<String> menuPosition = SettingsControls.getChoiceBox("position", "left", Translator.translate("utils.left"), "right", Translator.translate("utils.right"));
        menuPosition.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equalsIgnoreCase(oldValue)) {
                SettingsManager.setSetting("menu", "divider-position", (1 - (double)SettingsManager.getSetting("menu", "divider-position")));
            }
        });
        menuPositionSection.getChildren().add(menuPosition);
        if (SettingsManager.getSetting(SettingsManager.getCategory(), "position").equals("right")) {
            menuPosition.getSelectionModel().select(1);
        } else {
            menuPosition.getSelectionModel().select(0);
        }
        HBox.setMargin(menuPosition, new Insets(0, 20, 0, 20));

        HBox useIconsSection = getSettingBox("window.settings.menu.use-icon");
        ToggleSwitch useIcon = SettingsControls.getToggleSwitch("use-icon");
        useIconsSection.getChildren().add(useIcon);

        HBox useTextSection = getSettingBox("window.settings.menu.use-text");
        ToggleSwitch useText = SettingsControls.getToggleSwitch("use-text");
        useTextSection.getChildren().add(useText);

        VBox settingsContent = new VBox(title, useMenuSection, menuPositionSection, useIconsSection, useTextSection);
        settingsContent.setSpacing(10);
        settingsContent.setPadding(new Insets(30));
        settingsContent.setFillWidth(true);
        settings = new ScrollPane(settingsContent);
        settings.setFitToWidth(true);
        HBox.setHgrow(settings, Priority.ALWAYS);
    }

    private HBox getSettingBox(String key) {
        Label title = new Label(Translator.translate(key));
        title.setStyle("-fx-font-size: 14px;");
        title.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(5, 20, 5, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox box = new HBox(title, spacer);
        box.setSpacing(10);
        box.setMinHeight(50);
        box.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-background-color: #dedede;-fx-alignment: CENTER_LEFT;");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }
}