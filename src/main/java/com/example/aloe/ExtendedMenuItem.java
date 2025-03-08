package com.example.aloe;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class ExtendedMenuItem extends MenuItem {
    public ExtendedMenuItem(String text, EventHandler<ActionEvent> eventHandler) {
        super(text);
        this.setOnAction(eventHandler);
    }

    public ExtendedMenuItem(String text, Node graphic, EventHandler<ActionEvent> eventHandler) {
        this(text, eventHandler);
        this.setGraphic(graphic);
    }
}