package com.example.aloe;

import com.example.aloe.menu.MenuManager;

class AddMenuItemWindow extends MenuItemWindow {
    public AddMenuItemWindow() {
        super(Translator.translate("window.interior.menu.add"), "", "", "FOLDER_OPEN_O");

        this.setOnConfirm(event -> {
            MenuManager.addItemToMenu(path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}