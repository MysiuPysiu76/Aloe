package com.example.aloe.elements.navigation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class ProgressManager extends PopOver {

    private static final VBox container = new VBox();

    {
        this.setContentNode(container);
        this.setDetachable(false);
    }

    public static void addTask(String title, DoubleProperty progress, StringProperty description) {
        container.getChildren().add(new ProgressBox(title, progress, description));
    }

    @Override
    protected void show() {
        if(!container.getChildren().isEmpty()) super.show();
    }
}