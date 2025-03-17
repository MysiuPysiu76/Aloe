package com.example.aloe.menu;

import com.example.aloe.Main;
import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class MenuManager {
    private static VBox menu;
    private static MenuContextMenu contextMenu;

    static {
        loadMenu();
    }

    public static VBox getMenu() {
        return menu;
    }

    private static void loadMenu() {
        List<Map<String, Object>> items = SettingsManager.getSetting("menu", "items");
        menu = new VBox();
        menu.setAlignment(Pos.TOP_CENTER);
        if (!(items == null || items.isEmpty())) {
            for (Map<String, Object> item : items) {
                menu.getChildren().add(new com.example.aloe.menu.MenuItem((String) item.get("icon"), (String) item.get("name"), (String) item.get("path")));
            }
        }
        Main.loadMenu();
        MenuManager.setMenuOptions();
    }

     static void setMenuOptions() {
        contextMenu = new MenuContextMenu();

        menu.setOnContextMenuRequested(event -> {
            contextMenu.show(menu, event.getScreenX(), event.getScreenY());
        });
        menu.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });
    }

    public static void addItemToMenu(String path, String name, String icon) {
        List<Map<String, Object>> items = SettingsManager.getSetting("menu", "items");
        items.add(Map.of("path", path, "name", name, "icon", icon));
        SettingsManager.setSetting("menu", "items", items);
        loadMenu();
    }

    public static void editItemInMenu(String oldPath, String newPath, String name, String icon) {
        List<Map<String, Object>> items = SettingsManager.getSetting("menu", "items");
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
        List<Map<String, Object>> items = SettingsManager.getSetting("menu", "items");
        for (Map<String, Object> item : items) {
            if (item.get("path").equals(path)) {
                items.remove(item);
                break;
            }
        }
        SettingsManager.setSetting("menu", "items", items);
        loadMenu();
    }

    static void hideOptions() {
        contextMenu.hide();
    }
}