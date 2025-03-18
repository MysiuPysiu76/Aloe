package com.example.aloe.window.interior.menu;

import com.example.aloe.Translator;
import com.example.aloe.menu.MenuManager;

public class EditMenuItemWindow extends MenuItemWindow {
    public EditMenuItemWindow(String titleText, String pathText, String iconText) {
        super(Translator.translate("window.interior.menu.add"), titleText, pathText, iconText);

        this.setOnConfirm(event -> {
            MenuManager.editItemInMenu(pathText, path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}