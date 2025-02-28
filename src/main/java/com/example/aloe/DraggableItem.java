package com.example.aloe;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class DraggableItem<T> extends Button {

    private ObjectProperties object;

    public DraggableItem(ObjectProperties object, String text) {
        init(object);
        setText(text);
    }

    public DraggableItem(ObjectProperties object, String text, FontIcon icon) {
        init(object);
        setText(text);
        setGraphic(icon);
    }

    private void init(ObjectProperties object) {
        setMinHeight(25);
        setHeight(25);
        this.object = object;
    }

    public ObjectProperties getObject() {
        return object;
    }

    public void setObject(ObjectProperties object) {
        this.object = object;
    }
}