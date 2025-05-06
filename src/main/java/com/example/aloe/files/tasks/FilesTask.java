package com.example.aloe.files.tasks;

import com.example.aloe.utils.Translator;
import com.example.aloe.Utils;
import com.example.aloe.elements.navigation.ProgressManager;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.utils.UnitConverter;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

/**
 * Abstract base class for performing file-related background tasks in a JavaFX application.
 * <p>
 * This class provides core logic for tracking and reporting progress on file operations
 * (such as copying, moving, or deleting files). It uses JavaFX properties to update
 * progress and descriptive text in the UI. It is intended to be extended by specific
 * implementations of file tasks.
 * </p>
 *
 * <p>
 * Key features:
 * <ul>
 *     <li>Runs tasks on a separate daemon thread to avoid blocking the UI.</li>
 *     <li>Tracks the total size of all files involved and reports progress in real time.</li>
 *     <li>Registers tasks with the {@link ProgressManager} for visual feedback to users.</li>
 * </ul>
 * </p>
 *
 * @see javafx.concurrent.Task
 * @see com.example.aloe.files.FilesUtils
 * @see com.example.aloe.elements.navigation.ProgressManager
 * @since 2.0.0
 */
abstract class FilesTask extends Task<Void> {

    /**
     * Property representing the progress of the task (value between 0 and 1).
     */
    protected final DoubleProperty progressProperty = new SimpleDoubleProperty(0);

    /**
     * Property holding a textual description of the current task progress (e.g. "1.2 MB / 10 MB").
     */
    protected final StringProperty descriptionProperty = new SimpleStringProperty();

    /**
     * List of files involved in the task.
     */
    protected List<File> files;

    /**
     * Total size (in bytes) of all files involved in the task.
     */
    protected long totalSize;

    /**
     * Human-readable string representation of {@code totalSize} (e.g. "10 MB").
     */
    protected String totalSizeString;

    /**
     * Current progress in bytes.
     */
    protected long progress = 0;

    /**
     * Starts the task on a new background daemon thread.
     * This ensures that the task does not block the JavaFX application thread.
     */
    protected void runTask() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Attempts to register this task with the {@link ProgressManager},
     * unless the task is for a single small file (&lt; 1MB).
     *
     * @param type            the type of operation (e.g. "copy", "move")
     * @param destination     the destination category or label
     * @param destinationName the name of the destination directory or location
     */
    protected void tryAddToTasksList(String type, String destination, String destinationName) {
        totalSize = FilesUtils.calculateFileSize(files);
        if (!(files.size() == 1 && files.getFirst().length() < 1048576)) {
            String title = Translator.translate("task." + type)
                    + files.size()
                    + Translator.translate("task." + (files.size() == 1 ? "item-" : "items-") + destination)
                    + destinationName;
            ProgressManager.addTask(title, progressProperty, descriptionProperty);
            totalSizeString = UnitConverter.convert(totalSize);
        }
    }

    /**
     * Updates the progress UI properties based on the current number of bytes processed.
     * This method is run on the JavaFX application thread using {@code Platform.runLater}.
     */
    protected void updateProgress() {
        Platform.runLater(() -> {
            progressProperty.set(Utils.calculatePercentage(progress, totalSize) / 100);
            descriptionProperty.set(UnitConverter.convert(progress) + " / " + totalSizeString);
        });
    }
}
