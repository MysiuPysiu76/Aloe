package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.CurrentDirectory;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * A background task for moving (cutting) one or more files or directories to a specified destination.
 * <p>
 * This class extends {@link FileCopyTask} to reuse its recursive copying logic, but deletes the original
 * files after a successful copy to simulate a "cut" operation.
 * </p>
 *
 * <p>
 * Progress and status are updated via observable properties, allowing UI components to track task state.
 * The task is executed asynchronously using JavaFX's {@code Task} infrastructure.
 * </p>
 *
 * @see FileCopyTask
 * @since 2.0.3
 */
public class FileCutTask extends FileCopyTask {

    /**
     * The destination directory where files will be moved.
     */
    private Path destination;

    /**
     * Constructs a task to move a single file to the current working directory.
     *
     * @param file      the file to move
     * @param autoStart whether to automatically start the task after creation
     */
    public FileCutTask(File file, boolean autoStart) {
        this.files = List.of(file);
        this.destination = CurrentDirectory.get().toPath();

        if (autoStart) runTask();
    }

    /**
     * Constructs a task to move a list of files to the current working directory.
     *
     * @param files     the list of files or directories to move
     * @param autoStart whether to automatically start the task after creation
     */
    public FileCutTask(List<File> files, boolean autoStart) {
        this.files = files;
        this.destination = CurrentDirectory.get().toPath();

        if (autoStart) runTask();
    }

    /**
     * Executes the file cut operation. Each file is copied to the destination and then deleted.
     * Updates progress and refreshes the file view upon completion.
     *
     * @return {@code null} when the task finishes
     * @throws Exception if an error occurs during copying or deletion
     */
    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("cutting", "to", destination.getFileName().toString());

        for (File file : files) {
            copyRecursive(file.toPath(), destination.resolve(file.getName()));
            FileDeleteTask.deleteInCurrentThread(file);
            progress += file.length();
            updateProgress();
        }

        Platform.runLater(FilesLoader::refresh);
        return null;
    }
}
