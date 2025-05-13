package com.example.aloe.elements.menu;

import com.example.aloe.components.draggable.ObjectProperties;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Map;

public class MenuItem extends Button implements ObjectProperties {

    private final String icon;
    private final String title;
    private final String path;
    private static final boolean useIcon;
    private static final boolean useText;
    private static final boolean rightPageIcon;

    static {
        useIcon = Boolean.TRUE.equals(Settings.getSetting("menu", "use-icon"));
        useText = Boolean.TRUE.equals(Settings.getSetting("menu", "use-text"));
        rightPageIcon = Settings.getSetting("menu", "icon-position").toString().equalsIgnoreCase("right");
    }

    public MenuItem(String icon, String title, String path) {
        this.icon = icon;
        this.title = title;
        this.path = path;
        this.setFocusTraversable(false);
        if (useIcon) {
            FontIcon fontIcon = FontIcon.of(FontAwesome.valueOf(icon));
            fontIcon.setIconSize(18);
            fontIcon.getStyleClass().add("font-icon");
            fontIcon.setWrappingWidth(20);
            this.setGraphicTextGap(10);
            this.setGraphic(fontIcon);
            if (rightPageIcon) {
                this.setAlignment(Pos.CENTER_RIGHT);
                this.setContentDisplay(ContentDisplay.RIGHT);
            } else {
                this.setAlignment(Pos.CENTER_LEFT);
            }
        }
        if (useText) {
            this.setText(title);
        }
        HBox.setHgrow(this, Priority.ALWAYS);
        this.setMaxWidth(Double.MAX_VALUE);
        MenuItemContextMenu menu = new MenuItemContextMenu(this);
        this.setContextMenu(menu);
        this.getStyleClass().addAll("transparent", "text", "menu-option");
        this.setOnContextMenuRequested(e -> Menu.hideOptions());

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                FilesOpener.open(new File(path));
            }
        });
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    @Override
    public Map<String, String> getObjectProperties() {
        return Map.of("name", this.title, "icon", this.icon, "path", this.path);
    }

    @Override
    public Map<String, String> getObjectPropertiesView() {
        return Map.of(Translator.translate("menu.title"), this.title, Translator.translate("menu.icon"), this.icon, Translator.translate("menu.path"), this.path);
    }
}