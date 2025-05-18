package com.example.aloe.files;

import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.tasks.FileOpenerTask;

import java.io.File;

/**
 * The {@code FilesOpener} class is responsible for delegating the logic
 * of opening files and directories.
 * <p>
 * This utility determines whether the provided {@link File} is a regular file
 * or a directory, and delegates the handling appropriately:
 * <ul>
 *   <li>If the file is a regular file, it creates and runs a {@link FileOpenerTask}.</li>
 *   <li>If the file is a directory, it passes it to the {@link FilesLoader} to load its contents.</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>{@code
 * File file = new File("path/to/file/or/directory");
 * FilesOpener.open(file);
 * }</pre>
 *
 * This class is typically used in response to user interactions, such as clicks in a file browser UI.
 *
 * @since 1.7.7
 */
public class FilesOpener {

    /**
     * Opens the given file or directory by delegating to the appropriate handler.
     *
     * @param file the file or directory to open
     *  <ul>
     *      <li>If it is a file, a {@link FileOpenerTask} is executed.</li>
     *      <li>If it is a directory, {@link FilesLoader#load(File)} is called to load its contents.</li>
     *  </ul>
     */
    public static void open(File file) {
        if (file.isFile() && !file.toString().equalsIgnoreCase("%disks%")) {
            new FileOpenerTask(file, true);
        } else {
            FilesLoader.load(file);
        }
    }
}