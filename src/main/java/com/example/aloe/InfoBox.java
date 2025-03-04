package com.example.aloe;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class InfoBox extends VBox {

    public InfoBox() {
        this.setMinWidth(100);
    }

    public void setContent(ObjectProperties object) {
        this.getChildren().clear();
        for (Map.Entry<String, String> entry : object.getObjectPropertiesView().entrySet()) {
            this.getChildren().add(new HBox(new Label(entry.getKey()), WindowComponents.getSpacer(), new Label(entry.getValue())));
        }
    }
}