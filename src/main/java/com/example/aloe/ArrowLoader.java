package com.example.aloe;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class ArrowLoader {

    public static Pane getTopArrow() {
        Line line1 = new Line(5, 10, 10, 5);
        Line line2 = new Line(10, 5, 15, 10);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }

    public static Pane getRightArrow() {
        Line line1 = new Line(5, 5, 10, 10);
        Line line2 = new Line(10, 10, 5, 15);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }

    public static Pane getLeftArrow() {
        Line line1 = new Line(10, 5, 5, 10);
        Line line2 = new Line(5, 10, 10, 15);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }
}
