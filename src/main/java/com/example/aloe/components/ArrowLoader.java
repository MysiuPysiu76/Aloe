package com.example.aloe.components;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * Utility class responsible for creating simple arrow-like graphical elements
 * in a JavaFX user interface. Each arrow is composed of two {@link Line} segments,
 * styled using the "arrow-line" CSS class.
 *
 * <p>The direction of the arrow can be chosen from the {@link ArrowDirection} enum.
 *
 * @since 1.5.0
 */
public class ArrowLoader {

    /**
     * Enum representing supported directions for the arrow.
     * Each direction maps to a specific set of points that define two lines forming an arrow.
     */
    public enum ArrowDirection {

        /**
         * Left-pointing arrow.
         */
        LEFT(new double[]{10, 5, 5, 10, 5, 10, 10, 15}),
        /**
         * Upward-pointing arrow.
         */
        TOP(new double[]{5, 10, 10, 5, 10, 5, 15, 10}),

        /**
         * Right-pointing arrow.
         */
        RIGHT(new double[]{5, 5, 10, 10, 10, 10, 5, 15}),

        /**
         * Downward-pointing arrow.
         */
        BOTTOM(new double[]{5, 5, 10, 10, 10, 10, 15, 5});

        /**
         * Points variable
         */
        private double[] points;

        /**
         * Constructs an {@code ArrowDirection} with the given coordinate points
         * defining the shape of the arrow.
         *
         * @param points array of coordinates for two line segments forming the arrow
         */
        ArrowDirection(double[] points) {
            this.points = points;
        }

        /**
         * Returns the array of points defining the arrow shape.
         *
         * @return a double array representing x and y coordinates
         */
        public double[] getPoints() {
            return this.points;
        }
    }

    /**
     * Creates a JavaFX {@link Pane} containing two styled {@link Line} elements
     * arranged to visually represent an arrow in the specified direction.
     *
     * @param direction the direction in which the arrow should point
     * @return a {@link Pane} containing the arrow lines
     */
    public static Pane getArrow(ArrowDirection direction) {
        double[] points = direction.getPoints();
        Line line1 = new Line(points[0], points[1], points[2], points[3]);
        Line line2 = new Line(points[4], points[5], points[6], points[7]);

        line1.getStyleClass().add("arrow-line");
        line2.getStyleClass().add("arrow-line");

        return new Pane(line1, line2);
    }
}
