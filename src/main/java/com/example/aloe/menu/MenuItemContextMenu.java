package com.example.aloe.menu;

import com.example.aloe.*;
import com.example.aloe.window.PropertiesWindow;
import com.example.aloe.window.interior.menu.EditMenuItemWindow;

import java.io.File;

class MenuItemContextMenu extends ExtendedContextMenu {
    public MenuItemContextMenu(MenuItem item) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem("context-menu.open", e -> Main.openFileInOptions(new File(item.getPath())));
        ExtendedMenuItem edit = new ExtendedMenuItem("context-menu.edit", e -> new EditMenuItemWindow(item.getTitle(), item.getPath(), item.getIcon()));
        ExtendedMenuItem remove = new ExtendedMenuItem("context-menu.remove", e -> MenuManager.removeItemFromMenu(item.getPath()));
        ExtendedMenuItem properties = new ExtendedMenuItem("context-menu.properties", e -> new PropertiesWindow(new File(item.getPath())));

        this.getItems().addAll(open, edit, remove, properties);
    }
}