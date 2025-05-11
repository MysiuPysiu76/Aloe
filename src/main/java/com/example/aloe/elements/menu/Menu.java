package com.example.aloe.elements.menu;

import com.example.aloe.settings.Settings;
import com.example.aloe.window.MainWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class Menu extends ScrollPane {

    private static Menu menu;
    private static MenuContextMenu contextMenu;

    private Menu() {
        List<Map<String, Object>> items = Settings.getSetting("menu", "items");
        VBox content = new VBox();
        content.setAlignment(Pos.TOP_CENTER);
        if (!(items == null || items.isEmpty())) {
            for (Map<String, Object> item : items) {
                content.getChildren().add(new com.example.aloe.elements.menu.MenuItem((String) item.get("icon"), (String) item.get("name"), (String) item.get("path")));
            }
        }

        this.setContent(content);
        this.setFitToWidth(true);
        this.setFitToHeight(true);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setMenuOptions(this);
        this.setPadding(new Insets(5));
        this.getStyleClass().add("menu");
    }

    public static Menu get() {
        if (menu == null) menu = new Menu();
        return menu;
    }

    public static void reload() {
        menu = new Menu();
        MainWindow.loadMenu();
    }

     private void setMenuOptions(Menu content) {
        contextMenu = new MenuContextMenu();

        content.setOnContextMenuRequested(event -> {
            contextMenu.show(content, event.getScreenX(), event.getScreenY());
        });
        content.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });
    }

    public static void addItemToMenu(String path, String name, String icon) {
        List<Map<String, Object>> items = Settings.getSetting("menu", "items");
        items.add(Map.of("path", path, "name", name, "icon", icon));
        Settings.setSetting("menu", "items", items);
        reload();
    }

    public static void editItemInMenu(String oldPath, String newPath, String name, String icon) {
        List<Map<String, Object>> items = Settings.getSetting("menu", "items");
        for (Map<String, Object> item : items) {
            if (item.get("path").equals(oldPath)) {
                item.put("path", newPath);
                item.put("name", name);
                item.put("icon", icon);
            }
        }
        Settings.setSetting("menu", "items", items);
        reload();
    }

    static void removeItemFromMenu(String path) {
        List<Map<String, Object>> items = Settings.getSetting("menu", "items");
        for (Map<String, Object> item : items) {
            if (item.get("path").equals(path)) {
                items.remove(item);
                break;
            }
        }
        Settings.setSetting("menu", "items", items);
        reload();
    }

    static void hideOptions() {
        contextMenu.hide();
    }
}