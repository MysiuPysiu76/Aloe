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

class MenuItemContextMenu extends ExtendedContextMenu {
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
    }
}