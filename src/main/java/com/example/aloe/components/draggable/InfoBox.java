package com.example.aloe.components.draggable;

import com.example.aloe.components.HBoxSpacer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * The {@code InfoBox} class is a UI component that displays information about a selected item.
 * It extends {@link VBox} and provides methods to display either a map of properties of an
 * {@link ObjectProperties} instance or a custom {@link Node}. This component is typically
 * used in combination with draggable items, showing their relevant information when clicked.
 *
 * @since 1.5.1
 */
public class InfoBox extends VBox {

    /**
     * Constructs an empty {@code InfoBox} with a minimum width of 100 pixels.
     */
    public InfoBox() {
        this.setMinWidth(100);
    }

    /**
     * Sets the content of the {@code InfoBox} to display the properties of the given {@link ObjectProperties} object.
     * Each property of the object is displayed as a key-value pair in a horizontal layout, where the key is
     * shown in a {@link Label} and the value in another {@link Label}, with a spacer in between.
     *
     * @param object the {@link ObjectProperties} whose properties are to be displayed in the {@code InfoBox}
     */
    public void setContent(ObjectProperties object) {
        this.getChildren().clear();
        for (Map.Entry<String, String> entry : object.getObjectPropertiesView().entrySet()) {
            Label key = new Label(entry.getKey());
            key.getStyleClass().add("text");
            Label value = new Label(entry.getValue());
            value.getStyleClass().add("text");
            this.getChildren().add(new HBox(key, new HBoxSpacer(), value));
        }
    }

    /**
     * Sets the content of the {@code InfoBox} to display a custom {@link Node}.
     * The current content is cleared, and the provided node is added to the {@code InfoBox}.
     *
     * @param node the {@link Node} to be displayed in the {@code InfoBox}
     */
    public void setContent(Node node) {
        this.getChildren().clear();
        this.getChildren().add(node);
    }
}