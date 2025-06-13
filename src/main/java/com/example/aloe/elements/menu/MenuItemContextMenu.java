package com.example.aloe.elements.menu;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.settings.Settings;
import com.example.aloe.window.PropertiesWindow;
import com.example.aloe.window.interior.menu.EditMenuItemWindow;

import java.io.File;
import java.util.Arrays;

/**
 * A context menu for individual {@link MenuItem} elements within the application menu system.
 * <p>
 * The {@code MenuItemContextMenu} provides several context-sensitive actions:
 * <ul>
 *     <li><b>Open</b> — Opens the file or directory associated with the menu item.</li>
 *     <li><b>Edit</b> — Opens a dialog to edit the item's title, path, and icon.</li>
 *     <li><b>Remove</b> — Removes the item from the menu configuration.</li>
 *     <li><b>Properties</b> — Displays file properties for the item's path.</li>
 * </ul>
 *
 * <p>Special handling is implemented for system placeholders:
 * <ul>
 *     <li><b>%trash%</b> — Enables an additional option to empty the trash directory.</li>
 *     <li><b>%disks%</b> — Removes the properties item as it is not relevant.</li>
 * </ul>
 *
 * @since 2.7.3
 */
class MenuItemContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a context menu with relevant actions for the given {@code MenuItem}.
     *
     * @param item the menu item for which the context menu should be created
     */
    public MenuItemContextMenu(MenuItem item) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem("context-menu.open", e -> FilesOpener.open(new File(item.getPath())));
        ExtendedMenuItem edit = new ExtendedMenuItem("context-menu.edit", e -> new EditMenuItemWindow(item.getTitle(), item.getPath(), item.getIcon()));
        ExtendedMenuItem remove = new ExtendedMenuItem("context-menu.remove", e -> Menu.removeItemFromMenu(item.getPath()));
        ExtendedMenuItem properties = new ExtendedMenuItem("context-menu.properties", e -> new PropertiesWindow(new File(item.getPath())));

        this.getItems().addAll(open, edit, remove, properties);

        if (item.getPath().equals("%trash%")) {
            File file = new File(Settings.getSetting("files", "trash").toString());
            properties.setOnAction(e -> new PropertiesWindow(file));
            ExtendedMenuItem empty = new ExtendedMenuItem("context-menu.empty", e -> new FileDeleteTask(Arrays.stream(file.listFiles()).toList(), true));
            this.getItems().add(3, empty);
        }

        if (item.getPath().equals("%disks%")) {
            this.getItems().removeLast();
        }
    }
}
