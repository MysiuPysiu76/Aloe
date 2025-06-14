package com.example.aloe.elements.navigation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

/**
 * Manages and displays a list of background tasks in a popover window.
 * <p>
 * The {@code ProgressManager} class extends {@link PopOver} and is designed to show one or more
 * {@link ProgressBox} components, each representing an individual task with a title, progress bar,
 * and description. It is especially useful for visualizing ongoing processes like file operations
 * or background computations.
 *
 * <p>
 * Tasks can be added dynamically using the {@link #addTask(String, DoubleProperty, StringProperty)} method.
 * The popover only appears when at least one task is present in the container.
 *
 * @since 2.8.1
 */
public class ProgressManager extends PopOver {

    /**
     * The container VBox holding all {@link ProgressBox} nodes representing active tasks.
     * Shared across all instances of {@code ProgressManager}.
     */
    private static final VBox container = new VBox();

    /**
     * Instance initializer block that sets up the popover's content and behavior.
     * <p>
     * It assigns the container as the content node and configures the popover to be non-detachable.
     */
    {
        this.setContentNode(container);
        this.setDetachable(false);
        container.getStyleClass().add("popover-content");
    }

    /**
     * Adds a new task to the popover.
     *
     * @param title       The display title of the task.
     * @param progress    The {@link DoubleProperty} representing the progress (0.0–1.0).
     * @param description A {@link StringProperty} providing a dynamic description of the task.
     */
    public static void addTask(String title, DoubleProperty progress, StringProperty description) {
        container.getChildren().add(new ProgressBox(title, progress, description));
    }

    /**
     * Overrides the {@code show()} method to prevent showing an empty popover.
     * <p>
     * The popover will only appear if there is at least one task in the container.
     */
    @Override
    protected void show() {
        if (!container.getChildren().isEmpty()) {
            super.show();
        }
    }
}
