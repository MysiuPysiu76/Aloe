package com.example.aloe.menu;

import com.example.aloe.Main;
import com.example.aloe.ObjectProperties;
import com.example.aloe.PropertiesWindow;
import com.example.aloe.Translator;
import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
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
        useIcon = Boolean.TRUE.equals(SettingsManager.getSetting("menu", "use-icon"));
        useText = Boolean.TRUE.equals(SettingsManager.getSetting("menu", "use-text"));
        rightPageIcon = Boolean.TRUE.equals(SettingsManager.getSetting("menu", "icon-position"));
    }

    public MenuItem(String icon, String title, String path) {
        this.icon = icon;
        this.title = title;
        this.path = path;
        if (useIcon) {
            FontIcon fontIcon = FontIcon.of(FontAwesome.valueOf(icon));
            fontIcon.setIconSize(16);
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
        setMenuItemOptions(this, path, title, FontAwesome.valueOf(icon));
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                new Main().loadDirectoryContents(new File(path), true);
            }
        });
    }

    private static void setMenuItemOptions(Button button, String path, String name, FontAwesome icon) {
        ContextMenu contextMenu = new ContextMenu();
        javafx.scene.control.MenuItem open = new javafx.scene.control.MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            new Main().loadDirectoryContents(new File(path), true);
        });
        javafx.scene.control.MenuItem edit = new javafx.scene.control.MenuItem(Translator.translate("context-menu.edit"));
        edit.setOnAction(event -> {
            MenuWindowManager.openEditItemInMenuWindow(path, name, icon);
        });
        javafx.scene.control.MenuItem remove = new javafx.scene.control.MenuItem(Translator.translate("context-menu.remove"));
        remove.setOnAction(event -> {
            MenuManager.removeItemFromMenu(path);
        });
        javafx.scene.control.MenuItem properties = new javafx.scene.control.MenuItem(Translator.translate("context-menu.properties"));
        properties.setOnAction(event -> {
            new PropertiesWindow(new File(path));
        });
        contextMenu.getItems().addAll(open, edit, remove, properties);
        button.setOnContextMenuRequested(event -> {
            contextMenu.show(button, event.getScreenX(), event.getScreenY());
            MenuManager.hideOptions();
            event.consume();
        });
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