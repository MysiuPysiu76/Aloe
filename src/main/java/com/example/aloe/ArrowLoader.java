package com.example.aloe;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class ArrowLoader {

    public enum ArrowDirection {
        LEFT(new double[] {10, 5, 5, 10, 5, 10, 10, 15}),
        TOP(new double[] {5, 10, 10, 5, 10, 5, 15, 10}),
        RIGHT(new double[] {5, 5, 10, 10, 10, 10, 5, 15});

        private double[] points;

        ArrowDirection(double[] points) {
            this.points = points;
        }

        public double[] getPoints() {
            return this.points;
        }
    }

    public static Pane getArrow(ArrowDirection direction) {
        double[] points = direction.getPoints();
        Line line1 = new Line(points[0], points[1], points[2], points[3]);
        Line line2 = new Line(points[4], points[5], points[6], points[7]);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }
}
