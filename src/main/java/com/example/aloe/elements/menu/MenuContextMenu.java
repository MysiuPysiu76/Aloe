package com.example.aloe.elements.menu;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.window.interior.menu.AddMenuItemWindow;

class MenuContextMenu extends ExtendedContextMenu {
    public MenuContextMenu() {
        super();

        ExtendedMenuItem add = new ExtendedMenuItem("context-menu.add", e -> new AddMenuItemWindow());

        this.getItems().addAll(add);
    }
}