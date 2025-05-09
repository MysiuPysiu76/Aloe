package com.example.aloe.window.interior.menu;

import com.example.aloe.utils.Translator;
import com.example.aloe.elements.menu.Menu;

/**
 * {@code EditMenuItemWindow} provides a graphical interface for editing an existing
 * custom item in the application's main menu. It extends {@link MenuItemWindow}
 * and pre-populates input fields with the current values of the menu item.
 *
 * <p>This window allows the user to update the title, path, and icon associated with a menu entry.
 * All labels and buttons are localized using the {@link Translator} utility.</p>
 *
 * <p>Upon confirmation, the window applies changes by invoking
 * {@link Menu#editItemInMenu(String, String, String, String)}, then closes the overlay.</p>
 *
 * @see MenuItemWindow
 * @see Menu#editItemInMenu(String, String, String, String)
 * @since 2.3.0
 */
public class EditMenuItemWindow extends MenuItemWindow {

    /**
     * Constructs a new {@code EditMenuItemWindow} for editing a menu item.
     *
     * @param titleText the current title of the menu item
     * @param pathText  the current path associated with the menu item (used as an identifier)
     * @param iconText  the current icon name of the menu item (as {@code FontAwesome} name)
     */
    public EditMenuItemWindow(String titleText, String pathText, String iconText) {
        super(Translator.translate("window.interior.menu.add"), titleText, pathText, iconText);

        this.setOnConfirm(event -> {
            Menu.editItemInMenu(pathText, path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}
