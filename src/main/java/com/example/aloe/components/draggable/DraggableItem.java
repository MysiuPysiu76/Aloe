package com.example.aloe.components.draggable;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Represents a single draggable item within a draggable container (e.g. {@code DraggablePane}).
 * This class extends {@link Button}, combining a visual label and optional icon with associated data.
 *
 * <p>Each {@code DraggableItem} wraps an {@link ObjectProperties} instance that stores the item's metadata,
 * allowing the item to be moved and organized within a drag-and-drop interface.
 *
 * <p>This class only handles the visual and data representation of the item. The logic for
 * drag-and-drop interactions, ordering, and layout must be handled by the parent container.
 *
 * @since 1.5.1
 */
public class DraggableItem extends Button {

    /**
     * The object associated with this item, containing its properties and configuration.
     */
    private ObjectProperties object;

    /**
     * Creates a {@code DraggableItem} with the specified object and text label.
     *
     * @param object the underlying data model for this item
     * @param text   the text to display on the button
     */
    public DraggableItem(ObjectProperties object, String text) {
        this(object, text, null);
    }

    /**
     * Creates a {@code DraggableItem} with the specified object, text label, and optional icon.
     *
     * @param object the underlying data model for this item
     * @param text   the text to display on the button
     * @param icon   optional icon to display alongside the text
     */
    public DraggableItem(ObjectProperties object, String text, FontIcon icon) {
        this.object = object;
        setText(text);
        if (icon != null) setGraphic(icon);
        setMinHeight(25);
    }

    /**
     * Returns the object associated with this draggable item.
     *
     * @return the {@link ObjectProperties} linked to this item
     */
    public ObjectProperties getObject() {
        return object;
    }

    /**
     * Sets a new {@link ObjectProperties} object for this item.
     *
     * @param object the new object to associate with this item
     */
    public void setObject(ObjectProperties object) {
        this.object = object;
    }
}