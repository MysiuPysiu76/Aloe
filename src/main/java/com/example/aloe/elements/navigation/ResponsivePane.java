package com.example.aloe.elements.navigation;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ResponsivePane extends ScrollPane {

    public ResponsivePane() {
        this.setHvalue(Double.MAX_VALUE);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setMinWidth(300);
        this.setMaxWidth(1200);
        HBox.setHgrow(this, Priority.ALWAYS);

    }
}
