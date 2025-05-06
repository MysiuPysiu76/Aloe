package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.files.archive.ArchiveHandler;
import com.example.aloe.settings.Settings;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A task that opens a file using the system's default application or extracts it if it is an archive.
 * <p>
 * This class extends {@link FilesTask} and performs a simple operation: either opening a file or,
 * if it's an archive and the user has enabled auto-extraction, extracting it automatically.
 * The task runs on a background thread to avoid blocking the JavaFX UI.
 * </p>
 *
 * <p>
 * Features:
 * <ul>
 *     <li>Supports automatic execution upon instantiation (via {@code autoStart} flag).</li>
 *     <li>Automatically extracts archives if the "extract-on-click" setting is enabled.</li>
 *     <li>Uses {@link java.awt.Desktop} to open files in their associated default applications.</li>
 * </ul>
 * </p>
 *
 * @see FilesTask
 * @since 2.0.1
 */
public class FileOpenerTask extends FilesTask {

    /**
     * The file to be opened or extracted.
     */
    private File file;

    /**
     * Constructs a {@code FileOpenerTask} for the given file.
     *
     * @param file      the file to be opened or extracted
     * @param autoStart if {@code true}, the task starts automatically upon creation
     */
    public FileOpenerTask(File file, boolean autoStart) {
        this.file = file;

        if (autoStart) runTask();
    }

    /**
     * Executes the task logic: determines whether to extract or open the file.
     * <p>
     * If the file is an archive and the user setting {@code extract-on-click} is enabled,
     * it is extracted using {@link ArchiveHandler}, and the file list is refreshed.
     * Otherwise, the file is opened using the system's default application.
     * </p>
     *
     * @return {@code null} upon completion
     * @throws Exception if an error occurs during the operation
     */
    @Override
    protected Void call() throws Exception {
        if (file.isFile()) {
            if (FilesUtils.isFileArchive(file) && Boolean.TRUE.equals(Settings.getSetting("files", "extract-on-click"))) {
                Platform.runLater(() -> {
                    ArchiveHandler.extract(file);
                    FilesLoader.refresh();
                });
            } else {
                openFile();
            }
        }
        return null;
    }

    /**
     * Opens the file using the system's default application, if supported.
     * Catches and prints any {@link IOException} that may occur.
     */
    private void openFile() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
