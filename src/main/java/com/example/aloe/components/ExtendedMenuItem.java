package com.example.aloe.components;

import com.example.aloe.utils.Translator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

/**
 * An extended version of {@link MenuItem} that integrates internationalization support
 * and allows setting an action handler and optional graphic node.
 * <p>
 * The text of the menu item is automatically translated using a key via the
 * {@link Translator#translate(String)} method.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * ExtendedMenuItem item = new ExtendedMenuItem("menu.save", e -> saveFile());
 * }</pre>
 * </p>
 *
 * <p>
 * You can also attach a graphic node, such as an icon:
 * <pre>{@code
 * Node icon = new FontIcon(FontAwesome.SAVE);
 * ExtendedMenuItem itemWithIcon = new ExtendedMenuItem("menu.save", icon, e -> saveFile());
 * }</pre>
 * </p>
 *
 * @see javafx.scene.control.MenuItem
 * @since 1.8.9
 */
public class ExtendedMenuItem extends MenuItem {

    /**
     * Constructs a translated {@code ExtendedMenuItem} with the specified translation key and event handler.
     *
     * @param key          the translation key for the menu item's text
     * @param eventHandler the action event handler to be called when the menu item is clicked
     */
    public ExtendedMenuItem(String key, EventHandler<ActionEvent> eventHandler) {
        super(Translator.translate(key));
        this.setOnAction(eventHandler);
    }

    /**
     * Constructs a translated {@code ExtendedMenuItem} with a graphic node and event handler.
     *
     * @param key          the translation key for the menu item's text
     * @param graphic      a Node to be displayed alongside the menu item text (e.g. an icon)
     * @param eventHandler the action event handler to be called when the menu item is clicked
     */
    public ExtendedMenuItem(String key, Node graphic, EventHandler<ActionEvent> eventHandler) {
        this(key, eventHandler);
        this.setGraphic(graphic);
    }
}
