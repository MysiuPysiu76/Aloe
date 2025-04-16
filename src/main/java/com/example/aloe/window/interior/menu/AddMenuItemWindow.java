package com.example.aloe.window.interior.menu;

import com.example.aloe.utils.Translator;
import com.example.aloe.elements.menu.MenuManager;

public class AddMenuItemWindow extends MenuItemWindow {
    public AddMenuItemWindow() {
        super(Translator.translate("window.interior.menu.add"), "", "", "FOLDER_OPEN_O");

        this.setOnConfirm(event -> {
            MenuManager.addItemToMenu(path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}