package com.example.aloe.files;

import java.io.File;

/**
 * Utility class that manages a globally accessible reference to the current working directory.
 * <p>
 * If no directory is explicitly set, it defaults to the user's home directory.
 * This class uses a static field to retain state between method calls.
 * </p>
 *
 * @since 1.9.8
 */
public class CurrentDirectory {

    /**
     * The current directory being used by the application.
     * If {@code null}, the user's home directory is returned as default.
     */
    private static File currentDirectory = null;

    /**
     * Returns the current working directory.
     * If no directory has been set, this method returns the user's home directory.
     *
     * @return the currently set directory, or the user's home directory if none is set
     */
    public static File get() {
        if (currentDirectory == null) {
            currentDirectory = new File(System.getProperty("user.home"));
        }
        return currentDirectory;
    }

    /**
     * Sets the current working directory.
     * This value will be returned on subsequent calls to {@link #get()}.
     *
     * @param directory the new current directory to use
     */
    public static void set(File directory) {
        currentDirectory = directory;
    }
}
