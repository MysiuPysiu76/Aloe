package com.example.aloe.menu;

import com.example.aloe.Main;
import com.example.aloe.PropertiesWindow;
import com.example.aloe.Translator;
import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private static VBox menu;
    private static ContextMenu menuOptions;

    static {
        menuOptions = new ContextMenu();
        loadMenu();
    }

    public static VBox getMenu() {
        return menu;
    }

    private static void loadMenu() {
        List<Map<String, Object>> items = (List<Map<String, Object>>) SettingsManager.getSetting("menu", "items");
        menu = new VBox();
        menu.setAlignment(Pos.TOP_CENTER);
        boolean useIcon = SettingsManager.getSetting("menu", "use-icon");
        boolean useText = SettingsManager.getSetting("menu", "use-text");
        if (!(items == null || items.size() == 0)) {
            for (Map<String, Object> item : items) {
                menu.getChildren().add(getMenuButton((String) item.get("path"), (String) item.get("name"), (String) item.get("icon"), useIcon, useText));
            }
        }
        Main.loadMenu();
        MenuManager.setMenuOptions();
    }

    private static Button getMenuButton(String path, String name, String icon, boolean useIcon, boolean useText) {
        Button button = new Button();
        if (useIcon) {
            FontIcon fontIcon = FontIcon.of(FontAwesome.valueOf(icon));
            fontIcon.setIconSize(16);
            button.setGraphicTextGap(10);
            button.setGraphic(fontIcon);
        }
        if (useText) {
            button.setText(name);
        }
        button.getStyleClass().add("menu-option");
        button.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);
        setMenuItemOptions(button, path, name, FontAwesome.valueOf(icon));
        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                new Main().loadDirectoryContents(new File(path), true);
            }
        });
        Main.filesMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
            button.setMinWidth(newValue.doubleValue());
        });
        return button;
    }

    private static void setMenuItemOptions(Button button, String path, String name, FontAwesome icon) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem open = new MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            new Main().loadDirectoryContents(new File(path), true);
        });
        MenuItem edit = new MenuItem(Translator.translate("context-menu.edit"));
        edit.setOnAction(event -> {
            MenuWindowManager.openEditItemInMenuWindow(path, name, icon);
        });
        MenuItem remove = new MenuItem(Translator.translate("context-menu.remove"));
        remove.setOnAction(event -> {
            removeItemFromMenu(path);
        });
        MenuItem properties = new MenuItem(Translator.translate("context-menu.properties"));
        properties.setOnAction(event -> {
            new PropertiesWindow(new File(path));
        });
        contextMenu.getItems().addAll(open, edit, remove, properties);
        button.setOnContextMenuRequested(event -> {
            contextMenu.show(button, event.getScreenX(), event.getScreenY());
            menuOptions.hide();
            event.consume();
        });
    }

    public static void setMenuOptions() {
        menuOptions = new ContextMenu();
        MenuItem add = new MenuItem(Translator.translate("context-menu.add"));
        add.setOnAction(event -> {
            MenuWindowManager.openAddItemToMenuWindow();
        });
        menuOptions.getItems().add(add);
        menu.setOnContextMenuRequested(event -> {
            menuOptions.show(menu, event.getScreenX(), event.getScreenY());
        });
        menu.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                menuOptions.hide();
            }
        });
    }

    public static void addItemToMenu(String path, String name, String icon) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) SettingsManager.getSetting("menu", "items");
        items.add(Map.of("path", path, "name", name, "icon", icon));
        SettingsManager.setSetting("menu", "items", items);
        loadMenu();
    }

    static void editItemInMenu(String oldPath, String newPath, String name, String icon) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) SettingsManager.getSetting("menu", "items");
        for (Map<String, Object> item : items) {
            if (item.get("path").equals(oldPath)) {
                item.put("path", newPath);
                item.put("name", name);
                item.put("icon", icon);
            }
        }
        SettingsManager.setSetting("menu", "items", items);
        loadMenu();
    }

    static void removeItemFromMenu(String path) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) SettingsManager.getSetting("menu", "items");
        for (Map<String, Object> item : items) {
            if (item.get("path").equals(path)) {
                items.remove(item);
                break;
            }
        }
        SettingsManager.setSetting("menu", "items", items);
        loadMenu();
    }
}