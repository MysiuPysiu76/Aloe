package com.example.aloe.menu;

import com.example.aloe.ExtendedContextMenu;
import com.example.aloe.ExtendedMenuItem;
import com.example.aloe.Translator;

class MenuContextMenu extends ExtendedContextMenu {
    public MenuContextMenu() {
        super();

        ExtendedMenuItem add = new ExtendedMenuItem(Translator.translate("context-menu.add"), e -> MenuWindowManager.openAddItemToMenuWindow());

        this.getItems().addAll(add);
    }
}