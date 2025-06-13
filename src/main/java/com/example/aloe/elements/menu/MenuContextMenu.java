package com.example.aloe.elements.menu;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.window.interior.menu.AddMenuItemWindow;

/**
 * A context menu specifically for managing menu-related actions within the application UI.
 * <p>
 * The {@code MenuContextMenu} class extends {@link ExtendedContextMenu} and currently provides
 * a single action for adding a new menu item through a dedicated window.
 * <p>
 * This class can be expanded to include additional context-related functionality,
 * such as editing or removing menu entries.
 *
 * <p>Current entries:
 * <ul>
 *   <li>{@code Add Menu Item} — Opens a window for creating and configuring a new menu item.</li>
 * </ul>
 *
 * @since 2.7.3
 */
class MenuContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a new {@code MenuContextMenu} with predefined menu actions.
     */
    public MenuContextMenu() {
        super();

        ExtendedMenuItem add = new ExtendedMenuItem("context-menu.add", e -> new AddMenuItemWindow());

        this.getItems().addAll(add);
    }
}
