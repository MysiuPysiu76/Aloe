package com.example.aloe.components;

import com.example.aloe.utils.Translator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class ExtendedMenuItem extends MenuItem {
    public ExtendedMenuItem(String key, EventHandler<ActionEvent> eventHandler) {
        super(Translator.translate(key));
        this.setOnAction(eventHandler);
    }

    public ExtendedMenuItem(String key, Node graphic, EventHandler<ActionEvent> eventHandler) {
        this(key, eventHandler);
        this.setGraphic(graphic);
    }
}