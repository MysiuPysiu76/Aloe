package com.example.aloe.window.interior.menu;

import com.example.aloe.utils.Translator;
import com.example.aloe.elements.menu.Menu;

public class EditMenuItemWindow extends MenuItemWindow {
    public EditMenuItemWindow(String titleText, String pathText, String iconText) {
        super(Translator.translate("window.interior.menu.add"), titleText, pathText, iconText);

        this.setOnConfirm(event -> {
            Menu.editItemInMenu(pathText, path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}