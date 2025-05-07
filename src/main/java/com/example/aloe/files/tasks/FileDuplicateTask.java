package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import javafx.application.Platform;

import java.io.File;
import java.util.List;

/**
 * A background task for duplicating one or more files or directories within the same directory.
 * <p>
 * This task creates a copy of each file or folder next to the original, automatically renaming the duplicate
 * to avoid conflicts (e.g., appending "(copy 1)", "(copy 2)", etc.).
 * </p>
 *
 * <p>
 * Progress is updated as the files are duplicated, and the file view is refreshed on completion.
 * The task executes asynchronously using JavaFX's {@link javafx.concurrent.Task} framework.
 * </p>
 *
 * @see FileCopyTask
 * @since 2.0.5
 */
public class FileDuplicateTask extends FileCopyTask {

    /**
     * Constructs a task that duplicates a single file or directory.
     *
     * @param file      the file or directory to duplicate
     * @param autoStart whether to start the task immediately upon creation
     */
    public FileDuplicateTask(File file, boolean autoStart) {
        this.files = List.of(file);

        if (autoStart) runTask();
    }

    /**
     * Constructs a task that duplicates multiple files or directories.
     *
     * @param files     the list of files or directories to duplicate
     * @param autoStart whether to start the task immediately upon creation
     */
    public FileDuplicateTask(List<File> files, boolean autoStart) {
        this.files = files;

        if (autoStart) runTask();
    }

    /**
     * Executes the duplication task. Each file or directory is copied next to itself with a modified name.
     * Progress is updated as duplication proceeds, and the file list is refreshed afterward.
     *
     * @return {@code null} when the task finishes
     * @throws Exception if an error occurs during duplication
     */
    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("duplicating", "to", new File(files.getFirst().getParent()).getName());

        for (File file : files) {
            copyNextTo(file.toPath(), file.toPath());
            updateProgress();
        }

        Platform.runLater(FilesLoader::refresh);
        return null;
    }
}
