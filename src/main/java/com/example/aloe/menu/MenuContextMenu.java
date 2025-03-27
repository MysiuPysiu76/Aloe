package com.example.aloe.menu;

import com.example.aloe.ExtendedContextMenu;
import com.example.aloe.ExtendedMenuItem;
import com.example.aloe.Translator;
import com.example.aloe.window.interior.menu.AddMenuItemWindow;

class MenuContextMenu extends ExtendedContextMenu {
    public MenuContextMenu() {
        super();

        ExtendedMenuItem add = new ExtendedMenuItem(Translator.translate("context-menu.add"), e -> new AddMenuItemWindow());

        this.getItems().addAll(add);
    }
}