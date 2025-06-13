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

/**
 * Represents the vertical side menu component in the application UI.
 * <p>
 * The {@code Menu} class displays a list of {@link MenuItem} components and supports
 * operations such as adding, editing, removing, and reloading items. Menu state is
 * persisted and retrieved from application {@link Settings}.
 * </p>
 *
 * <p>
 * A context menu is available on right-click, providing actions such as adding a new item.
 * </p>
 *
 * <p>
 * This class implements a singleton pattern, ensuring only one menu instance exists
 * throughout the application lifecycle.
 * </p>
 *
 * @since 2.7.5
 */
public class Menu extends ScrollPane {

    /** Singleton instance of the Menu. */
    private static Menu instance;

    /** Global context menu associated with the Menu. */
    private static final MenuContextMenu contextMenu = new MenuContextMenu();

    /** Container for menu items. */
    private final VBox content = new VBox();

    /**
     * Private constructor to prevent direct instantiation. Initializes layout,
     * menu items, context menu, and styling.
     */
    private Menu() {
        setupContent();
        setupScrollPane();
        setupContextMenu();
    }

    /**
     * Returns the singleton instance of the {@code Menu}, creating it if necessary.
     *
     * @return the current Menu instance
     */
    public static Menu get() {
        if (instance == null) instance = new Menu();
        return instance;
    }

    /**
     * Reloads the menu from persisted settings and replaces the singleton instance.
     * This triggers a UI refresh via {@link MainWindow#loadMenu()}.
     */
    public static void reload() {
        instance = new Menu();
        MainWindow.loadMenu();
    }

    /**
     * Adds a new menu item and persists it in application settings.
     *
     * @param path the file path the item points to
     * @param name the display title of the item
     * @param icon the FontAwesome icon name to use
     */
    public static void addItem(String path, String name, String icon) {
        List<Map<String, Object>> items = getMenuItems();
        items.add(createItemMap(path, name, icon));
        saveMenuItems(items);
        reload();
    }

    /**
     * Updates an existing menu item identified by its original path.
     *
     * @param oldPath the original file path to locate the item
     * @param newPath the new path to set
     * @param name    the new display name
     * @param icon    the new icon name
     */
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

    /**
     * Removes a menu item by matching its file path.
     *
     * @param path the path of the menu item to remove
     */
    public static void removeItem(String path) {
        List<Map<String, Object>> items = getMenuItems();
        items.removeIf(item -> Objects.equals(item.get("path"), path));
        saveMenuItems(items);
        reload();
    }

    /**
     * Hides the global context menu if it is currently visible.
     */
    public static void hideContextMenu() {
        contextMenu.hide();
    }

    /**
     * Initializes the VBox layout with loaded menu items and assigns it as the scroll pane's content.
     */
    private void setupContent() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setSpacing(0);
        content.getChildren().setAll(buildMenuItems());
        this.setContent(content);
    }

    /**
     * Configures visual properties and behavior of the scroll pane containing the menu.
     */
    private void setupScrollPane() {
        this.setFitToWidth(true);
        this.setFitToHeight(true);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setPadding(new Insets(5));
        this.getStyleClass().add("menu");
    }

    /**
     * Attaches context menu interaction and mouse click filtering to the component.
     */
    private void setupContextMenu() {
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) contextMenu.hide();
        });
    }

    /**
     * Builds a list of {@link MenuItem} instances from the stored menu configuration.
     *
     * @return list of menu items; may be empty but never {@code null}
     */
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

    /**
     * Utility method to construct a menu item map for persistence.
     *
     * @param path the file path
     * @param name the display name
     * @param icon the icon name
     * @return a new item map
     */
    private static Map<String, Object> createItemMap(String path, String name, String icon) {
        Map<String, Object> item = new HashMap<>();
        item.put("path", path);
        item.put("name", name);
        item.put("icon", icon);
        return item;
    }

    /**
     * Retrieves the current list of menu items from {@link Settings}.
     *
     * @return list of item maps
     */
    private static List<Map<String, Object>> getMenuItems() {
        return Settings.getSetting("menu", "items");
    }

    /**
     * Saves the given list of menu items to {@link Settings}.
     *
     * @param items the menu item list to persist
     */
    private static void saveMenuItems(List<Map<String, Object>> items) {
        Settings.setSetting("menu", "items", items);
    }
}
