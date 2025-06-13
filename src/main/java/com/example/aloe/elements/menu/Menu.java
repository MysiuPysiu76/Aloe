package com.example.aloe.elements.menu;

import com.example.aloe.settings.Settings;
import com.example.aloe.window.MainWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.*;

public class Menu extends ScrollPane {

    private static Menu instance;
    private static final MenuContextMenu contextMenu = new MenuContextMenu();

    private final VBox content = new VBox();

    private Menu() {
        setupContent();
        setupScrollPane();
        setupContextMenu();
    }

    public static Menu get() {
        if (instance == null) instance = new Menu();
        return instance;
    }

    public static void reload() {
        instance = new Menu();
        MainWindow.loadMenu();
    }

    public static void addItem(String path, String name, String icon) {
        List<Map<String, Object>> items = getMenuItems();
        items.add(createItemMap(path, name, icon));
        saveMenuItems(items);
        reload();
    }

    public static void editItem(String oldPath, String newPath, String name, String icon) {
        List<Map<String, Object>> items = getMenuItems();
        for (Map<String, Object> item : items) {
            if (Objects.equals(item.get("path"), oldPath)) {
                item.put("path", newPath);
                item.put("name", name);
                item.put("icon", icon);
                break;
            }
        }
        saveMenuItems(items);
        reload();
    }

    public static void removeItem(String path) {
        List<Map<String, Object>> items = getMenuItems();
        items.removeIf(item -> Objects.equals(item.get("path"), path));
        saveMenuItems(items);
        reload();
    }

    public static void hideContextMenu() {
        contextMenu.hide();
    }

    private void setupContent() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setSpacing(0);
        content.getChildren().setAll(buildMenuItems());
        this.setContent(content);
    }

    private void setupScrollPane() {
        this.setFitToWidth(true);
        this.setFitToHeight(true);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setPadding(new Insets(5));
        this.getStyleClass().add("menu");
    }

    private void setupContextMenu() {
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) contextMenu.hide();
        });
    }

    private List<MenuItem> buildMenuItems() {
        List<Map<String, Object>> items = getMenuItems();
        if (items == null) return Collections.emptyList();

        List<MenuItem> result = new ArrayList<>();
        for (Map<String, Object> item : items) {
            result.add(new MenuItem(
                    (String) item.get("icon"),
                    (String) item.get("name"),
                    (String) item.get("path")
            ));
        }
        return result;
    }

    private static Map<String, Object> createItemMap(String path, String name, String icon) {
        Map<String, Object> item = new HashMap<>();
        item.put("path", path);
        item.put("name", name);
        item.put("icon", icon);
        return item;
    }

    private static List<Map<String, Object>> getMenuItems() {
        return Settings.getSetting("menu", "items");
    }

    private static void saveMenuItems(List<Map<String, Object>> items) {
        Settings.setSetting("menu", "items", items);
    }
}
