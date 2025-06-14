package com.example.aloe.elements.navigation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * A custom UI component that displays a title, a progress bar bound to a task's progress,
 * and a description label that updates in real-time.
 *
 * <p>This class is typically used in background task UIs to show the current status of an operation.</p>
 *
 * @since 2.8.1
 */
public class ProgressBox extends VBox {

    private static final double MIN_WIDTH = 200;
    private static final double MAX_WIDTH = 300;
    private static final double PROGRESS_BAR_HEIGHT = 10;

    /**
     * Constructs a new {@code ProgressBox}.
     *
     * @param title       The static title to display at the top.
     * @param progress    A {@link DoubleProperty} bound to the progress bar (range 0.0–1.0).
     * @param description A {@link StringProperty} bound to the description label.
     */
    public ProgressBox(String title, DoubleProperty progress, StringProperty description) {
        configureLayout();
        getChildren().addAll(createTitleLabel(title), createProgressBar(progress), createDescriptionLabel(description));
    }

    /**
     * Sets the basic layout properties for this container.
     */
    private void configureLayout() {
        setMinWidth(MIN_WIDTH);
        setMaxWidth(MAX_WIDTH);
        setPadding(new Insets(6, 13, 6, 13));
    }

    /**
     * Creates the title label.
     *
     * @param text The text to display.
     * @return A configured {@link Label}.
     */
    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setPadding(new Insets(7, 9, 7, 5));
        label.getStyleClass().add("text");
        return label;
    }

    /**
     * Creates and binds a progress bar to the given progress property.
     *
     * @param progress The progress property to bind.
     * @return A configured {@link ProgressBar}.
     */
    private ProgressBar createProgressBar(DoubleProperty progress) {
        ProgressBar bar = new ProgressBar(0);
        bar.progressProperty().bind(progress);
        bar.setMaxWidth(MAX_WIDTH);
        bar.setMaxHeight(PROGRESS_BAR_HEIGHT);
        bar.setPadding(new Insets(0));
        return bar;
    }

    /**
     * Creates and binds a description label to the given string property.
     *
     * @param description The description text property to bind.
     * @return A configured {@link Label}.
     */
    private Label createDescriptionLabel(StringProperty description) {
        Label label = new Label();
        label.setPadding(new Insets(7, 9, 7, 5));
        label.getStyleClass().add("text");
        label.textProperty().bind(description);
        return label;
    }
}
