package com.example.aloe.menu;

import com.example.aloe.*;

import java.io.File;

class MenuItemContextMenu extends ExtendedContextMenu {
    public MenuItemContextMenu(MenuItem item) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem(Translator.translate("context-menu.open"), e -> Main.openFileInOptions(new File(item.getPath())));
        ExtendedMenuItem edit = new ExtendedMenuItem(Translator.translate("context-menu.edit"), e -> Main.editEditMenuItem(item.getTitle(), item.getPath(), item.getIcon()));
        ExtendedMenuItem remove = new ExtendedMenuItem(Translator.translate("context-menu.remove"), e -> MenuManager.removeItemFromMenu(item.getPath()));
        ExtendedMenuItem properties = new ExtendedMenuItem(Translator.translate("context-menu.properties"), e -> new PropertiesWindow(new File(item.getPath())));

        this.getItems().addAll(open, edit, remove, properties);
    }
}