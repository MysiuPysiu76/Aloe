package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * A background task responsible for moving one or more files or directories to a new destination.
 * <p>
 * This task uses a recursive file tree walk to move each file and subdirectory,
 * preserving the original structure and overwriting existing files if needed.
 * </p>
 *
 * <p>
 * The task runs asynchronously using JavaFX's {@link javafx.concurrent.Task} system
 * and updates UI components such as progress indicators and file listings.
 * </p>
 *
 * @see FilesTask
 * @since 2.0.6
 */
public class FileMoveTask extends FilesTask {

    private File destination;

    /**
     * Constructs a task to move a single file or directory to the given destination.
     *
     * @param file       the file or directory to move
     * @param destination the target directory where the file should be moved
     * @param autostart  whether to immediately start the task upon creation
     */
    public FileMoveTask(File file, File destination, boolean autostart) {
        this.files = List.of(file);
        this.destination = destination;

        if (autostart) runTask();
    }

    /**
     * Constructs a task to move multiple files or directories to the given destination.
     *
     * @param files      the list of files or directories to move
     * @param destination the target directory where the files should be moved
     * @param autostart  whether to immediately start the task upon creation
     */
    public FileMoveTask(List<File> files, File destination, boolean autostart) {
        this.files = files;
        this.destination = destination;

        if (autostart) runTask();
    }

    /**
     * Executes the file move task by recursively traversing and moving files to the new location.
     * If a file already exists at the destination, it will be replaced.
     * Updates the task's progress as each file is moved.
     *
     * @return {@code null} upon successful completion
     * @throws Exception if an error occurs during the move process
     */
    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("moving", "to", destination.getName());

        if (destination != null) {
            for (File file : files) {
                File newFile = new File(destination, file.getName());

                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                        Path relativePath = file.toPath().relativize(filePath);
                        Path destinationFile = newFile.toPath().resolve(relativePath);
                        Files.move(filePath, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path relativeDir = file.toPath().relativize(dir);
                        Path newDir = newFile.toPath().resolve(relativeDir);
                        if (!Files.exists(newDir)) {
                            Files.createDirectories(newDir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                progress += newFile.length();
                updateProgress();
            }
        }

        Platform.runLater(FilesLoader::refresh);
        return null;
    }
}
