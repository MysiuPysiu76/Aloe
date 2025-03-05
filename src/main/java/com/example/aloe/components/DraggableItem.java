package com.example.aloe.components;

import com.example.aloe.ObjectProperties;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class DraggableItem extends Button {
    private ObjectProperties object;

    public DraggableItem(ObjectProperties object, String text) {
        this(object, text, null);
    }

    public DraggableItem(ObjectProperties object, String text, FontIcon icon) {
        this.object = object;
        setText(text);
        if (icon != null) setGraphic(icon);
        setMinHeight(25);
    }

    public ObjectProperties getObject() {
        return object;
    }

    public void setObject(ObjectProperties object) {
        this.object = object;
    }
}