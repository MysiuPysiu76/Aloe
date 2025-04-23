package com.example.aloe.files;

import com.example.aloe.elements.files.FilesLoader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the history of directories accessed by the user.
 * This class keeps track of the directories opened and allows navigation
 * between previously visited directories.
 *
 * <p>The history is maintained using a list of {@link File} objects,
 * with an internal position tracker indicating the current directory.
 * Users can navigate backward and forward through the directory history.</p>
 * <p>
 * * @since 1.4.4
 */
public class DirectoryHistory {

    /**
     * List storing the history of accessed directories.
     */
    private static final List<File> files = new LinkedList<>();

    /**
     * The current position in the directory history.
     */
    private static int position = -1;

    /**
     * Adds a directory to the history and updates the current position.
     *
     * @param file the directory to be added to history
     */
    public static void addDirectory(File file) {
        files.add(file);
        position = files.size() - 1;
    }

    /**
     * Loads the previously accessed directory if available.
     *
     * <p>If there is a previous directory in history, the position is
     * decremented and the corresponding directory contents are loaded.</p>
     */
    public static void loadPreviousDirectory() {
        if (position > 0) {
            position--;
            FilesLoader.load(files.get(position));
        }
    }

    /**
     * Loads the next directory in the history if available.
     *
     * <p>If there is a forward entry in history, the position is
     * incremented and the corresponding directory contents are loaded.</p>
     */
    public static void loadNextDirectory() {
        if (position < files.size() - 1) {
            position++;
            FilesLoader.load(files.get(position));
        }
    }
}