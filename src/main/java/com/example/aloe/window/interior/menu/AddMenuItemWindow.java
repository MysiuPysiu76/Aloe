package com.example.aloe.window.interior.menu;

import com.example.aloe.utils.Translator;
import com.example.aloe.elements.menu.Menu;

/**
 * {@code AddMenuItemWindow} is a graphical interface used to add a new custom item
 * to the application's main menu. It extends {@link MenuItemWindow} and provides
 * predefined behavior for collecting input and committing a new menu entry.
 *
 * <p>This window includes fields for entering the item's title, path, and icon,
 * and uses localized labels for multilingual support.</p>
 *
 * <p>Once the user confirms, the new item is added to the menu via
 * {@link Menu#addItemToMenu(String, String, String)}.</p>
 *
 * @see MenuItemWindow
 * @see Menu#addItemToMenu(String, String, String)
 * @since 2.2.9
 */
public class AddMenuItemWindow extends MenuItemWindow {

    /**
     * Constructs a new {@code AddMenuItemWindow} with empty input fields and a default icon.
     * The window uses localized labels and sets up a confirmation handler that adds the
     * new item to the menu and closes the window overlay.
     */
    public AddMenuItemWindow() {
        super(Translator.translate("window.interior.menu.add"), "", "", "FOLDER_OPEN_O");

        this.setOnConfirm(event -> {
            Menu.addItemToMenu(path.getText(), title.getText(), icon.getValue().toString());
            hideOverlay();
        });
    }
}
