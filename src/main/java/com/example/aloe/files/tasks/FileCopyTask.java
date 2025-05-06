package com.example.aloe.files.tasks;

import com.example.aloe.files.FilesUtils;
import com.example.aloe.utils.Translator;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FileDecision;
import com.example.aloe.window.DecisionWindow;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.List;

/**
 * A background task for copying one or more files or directories to a specified destination.
 * <p>
 * This class handles various copy scenarios, including:
 * <ul>
 *     <li>Recursive copying of directories</li>
 *     <li>Conflict resolution when a file or directory with the same name already exists at the target</li>
 *     <li>Integration with the UI to prompt users for decisions on conflicts</li>
 *     <li>Optional support for cut (move) operations via delegation to {@link FileCutTask}</li>
 * </ul>
 * </p>
 *
 * <p>
 * The task supports both immediate execution (when {@code autoStart} is true) and deferred start.
 * File size is tracked to enable accurate progress reporting via inherited properties.
 * </p>
 *
 * @see FilesTask
 * @since 1.0.2
 */
public class FileCopyTask extends FilesTask {

    /**
     * The destination path to which files will be copied.
     */
    private Path destination;

    /**
     * Flag indicating whether the operation is a cut (move) instead of a copy.
     */
    private static boolean isCut = false;

    /**
     * Default constructor. Used only internally or for placeholder instantiation.
     */
    FileCopyTask() {
        this.files = null;
        this.destination = null;
        this.totalSize = 0;
    }

    /**
     * Constructs a task to copy a list of files to the current directory.
     * If the cut flag is active, delegates the operation to {@link FileCutTask}.
     *
     * @param files     the list of files to copy
     * @param autoStart whether to start the task immediately
     */
    public FileCopyTask(List<File> files, boolean autoStart) {
        if (isCut) {
            new FileCutTask(files, autoStart);
            return;
        }

        this.files = files;
        this.destination = CurrentDirectory.get().toPath();
        this.totalSize = FilesUtils.calculateFileSize(files);

        if (autoStart) runTask();
    }

    /**
     * Constructs a task to copy a list of files to a specified directory.
     *
     * @param files       the files to copy
     * @param destination the target directory
     * @param autoStart   whether to start the task immediately
     */
    public FileCopyTask(List<File> files, File destination, boolean autoStart) {
        this(files, false);
        this.destination = destination.toPath();

        if (autoStart) runTask();
    }

    /**
     * Constructs a task to copy a single file to the current directory.
     * If the cut flag is active, delegates the operation to {@link FileCutTask}.
     *
     * @param file      the file to copy
     * @param autoStart whether to start the task immediately
     */
    public FileCopyTask(File file, boolean autoStart) {
        if (isCut) {
            new FileCutTask(file, autoStart);
            return;
        }

        this.files = List.of(file);
        this.destination = CurrentDirectory.get().toPath();
        this.totalSize = FilesUtils.calculateFileSize(file);

        if (autoStart) runTask();
    }

    /**
     * Constructs a task to copy a single file to a specific destination directory.
     *
     * @param file        the file to copy
     * @param destination the destination directory
     * @param autoStart   whether to start the task immediately
     */
    public FileCopyTask(File file, File destination, boolean autoStart) {
        this(file, false);
        this.destination = destination.toPath();

        if (autoStart) runTask();
    }

    /**
     * Main logic for copying files. Handles file conflict resolution and recursive directory copying.
     *
     * @return {@code null} upon successful completion
     * @throws Exception if an error occurs during the copy process
     */
    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("copying", "to", destination.getFileName().toString());

        for (File source : files) {
            if (!source.exists()) continue;
            Path target = destination.resolve(source.getName());

            if (Files.exists(target)) {
                handleExistingFile(source, target);
            } else {
                copyRecursive(source.toPath(), target);
            }
        }

        Platform.runLater(FilesLoader::refresh);
        return null;
    }

    /**
     * Handles cases where a file or directory with the same name already exists at the destination.
     * Prompts the user for a decision via the UI, then takes the corresponding action.
     *
     * @param source the source file or directory
     * @param target the conflicting target path
     * @throws Exception if an error occurs during conflict resolution
     */
    private void handleExistingFile(File source, Path target) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final FileDecision[] decision = new FileDecision[1];

        Platform.runLater(() -> {
            decision[0] = source.isFile() ? DecisionWindow.addFile(target.toFile()) : DecisionWindow.addDirectory(target.toFile());
            latch.countDown();
        });

        latch.await();

        switch (decision[0]) {
            case COMBINE -> copyRecursive(source.toPath(), target);
            case NEXT_TO -> copyNextTo(source.toPath(), target);
            case REPLACE -> copyReplace(source.toPath(), target);
        }
    }

    /**
     * Recursively copies files and directories from a source path to a destination path.
     *
     * @param source      the source path
     * @param destination the destination path
     * @throws IOException if an error occurs during copying
     */
    protected void copyRecursive(Path source, Path destination) throws IOException {
        if (Files.isDirectory(source)) {
            Files.createDirectories(destination);

            try (DirectoryStream<Path> entries = Files.newDirectoryStream(source)) {
                for (Path entry : entries) {
                    copyRecursive(entry, destination.resolve(entry.getFileName()));
                }
            }
        } else {
            copyFile(source, destination);
        }
    }

    /**
     * Copies a single file and updates progress based on the bytes copied.
     *
     * @param source      the source file path
     * @param destination the target file path
     * @throws IOException if an error occurs during I/O
     */
    protected void copyFile(Path source, Path destination) throws IOException {
        try (InputStream in = Files.newInputStream(source); OutputStream out = Files.newOutputStream(destination)) {

            byte[] buffer = new byte[1048576];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                progress += bytesRead;
                updateProgress();
            }
        }
    }

    /**
     * Copies the file or directory next to the existing one, using a numbered suffix to avoid overwriting.
     *
     * @param source      the source path
     * @param destination the original (conflicting) destination path
     * @throws IOException if an error occurs during copying
     */
    protected void copyNextTo(Path source, Path destination) throws IOException {
        int index = 1;
        String extension = FilesUtils.getExtensionWithDot(destination.toFile());
        Path parent = destination.getParent();
        Path newDestination = destination;

        while (Files.exists(newDestination)) {
            String newFileName = FilesUtils.getFileName(destination.toFile()) + " (" + Translator.translate("utils.copy") + " " + index + ")" + extension;
            newDestination = parent.resolve(newFileName);
            index++;
        }

        copyRecursive(source, newDestination);
    }

    /**
     * Replaces the existing file or directory by deleting it and copying the new one in its place.
     *
     * @param source      the source path
     * @param destination the target path to replace
     * @throws Exception if an error occurs during deletion or copying
     */
    protected void copyReplace(Path source, Path destination) throws Exception {
        FileDeleteTask.deleteInCurrentThread(destination);
        copyRecursive(source, destination);
    }

    /**
     * Sets whether the operation should be treated as a cut (move) rather than a copy.
     *
     * @param cut {@code true} to enable cut mode
     */
    public static void setCut(boolean cut) {
        isCut = cut;
    }

    /**
     * Checks if the current operation is a cut.
     *
     * @return {@code true} if in cut mode, {@code false} otherwise
     */
    public static boolean isCut() {
        return isCut;
    }
}
