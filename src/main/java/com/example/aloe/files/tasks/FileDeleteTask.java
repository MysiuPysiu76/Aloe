package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A background task that handles deletion of one or more files or directories.
 * <p>
 * This class supports recursive deletion of directories and reports progress for each deleted file.
 * The task is executed asynchronously using JavaFX's {@link javafx.concurrent.Task} infrastructure
 * and updates UI elements through observable properties.
 * </p>
 *
 * <p>
 * Static helper methods are also provided to perform deletions synchronously within the current thread,
 * without launching the full task.
 * </p>
 *
 * @see FilesTask
 * @see com.example.aloe.elements.files.FilesLoader
 */
public class FileDeleteTask extends FilesTask {

    /**
     * Constructs a new task to delete a list of files or directories.
     *
     * @param files     the list of files to delete
     * @param autoStart whether to start the task immediately
     */
    public FileDeleteTask(List<File> files, boolean autoStart) {
        this.files = files;

        if (autoStart) runTask();
    }

    /**
     * Constructs a new task to delete a single file or directory.
     *
     * @param file      the file or directory to delete
     * @param autoStart whether to start the task immediately
     */
    public FileDeleteTask(File file, boolean autoStart) {
        this.files = List.of(file);

        if (autoStart) runTask();
    }

    /**
     * Executes the deletion task. Deletes each file or directory recursively if needed,
     * updates progress, and refreshes the file view.
     *
     * @return {@code null} upon completion
     * @throws Exception if an error occurs during file deletion
     */
    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("deleting", "from", new File(files.getFirst().getParent()).getName());

        for (File file : files) {
            if (file.exists()) {
                long fileLength = file.length();
                deleteRecursive(file.toPath());
                progress += fileLength;
            }
            updateProgress();
        }

        Platform.runLater(FilesLoader::refresh);
        return null;
    }

    /**
     * Deletes a file or directory recursively within the current thread context.
     *
     * @param path the path to delete
     * @throws Exception if an error occurs during deletion
     */
    public static void deleteInCurrentThread(Path path) throws Exception {
        new FileDeleteTask(List.of(), false).deleteRecursive(path);
    }

    /**
     * Deletes a file or directory recursively within the current thread context.
     *
     * @param file the file to delete
     * @throws Exception if an error occurs during deletion
     */
    public static void deleteInCurrentThread(File file) throws Exception {
        new FileDeleteTask(List.of(), false).deleteRecursive(file.toPath());
    }

    /**
     * Deletes a list of files or directories recursively within the current thread context.
     *
     * @param files the list of files to delete
     * @throws Exception if an error occurs during deletion
     */
    public static void deleteInCurrentThread(List<File> files) throws Exception {
        for (File file : files) {
            deleteInCurrentThread(file.toPath());
        }
    }

    /**
     * Recursively deletes a file or directory and all its contents if applicable.
     *
     * @param path the path to delete
     * @throws Exception if an error occurs during recursive deletion
     */
    private void deleteRecursive(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteRecursive(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}
