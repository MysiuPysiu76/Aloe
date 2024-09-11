package com.example.aloe;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class ArrowLoader {

    public static Pane getTopArrow() {
        return ArrowLoader.createArrow(5, 10, 10, 5, 10, 5, 15, 10);
    }

    public static Pane getRightArrow() {
        return ArrowLoader.createArrow(5, 5, 10, 10, 10, 10, 5, 15);
    }

    public static Pane getLeftArrow() {
        return ArrowLoader.createArrow(10, 5, 5, 10, 5, 10, 10, 15);
    }

    private static Pane createArrow(double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8) {
        Line line1 = new Line(v1, v2, v3, v4);
        Line line2 = new Line(v5, v6, v7, v8);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }
}
