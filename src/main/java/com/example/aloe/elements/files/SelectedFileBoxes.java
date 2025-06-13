package com.example.aloe.elements.files;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class responsible for managing the current selection state of {@link FileBox} instances.
 * <p>
 * This class provides static methods to add, remove, check, and retrieve selected file boxes
 * in the application. It maintains the selection state in a globally accessible context,
 * allowing consistent behavior across file-related UI components.
 * <p>
 * The internal order of selection is preserved using a {@link LinkedHashSet}.
 *
 * <p>Usage example:
 * <pre>{@code
 *   SelectedFileBoxes.add(fileBox);
 *   List<File> files = SelectedFileBoxes.getSelectedFiles();
 * }</pre>
 *
 * @since 2.7.2
 */
public class SelectedFileBoxes {

    /** Holds the currently selected FileBox instances in insertion order. */
    private static final Set<FileBox> selectedFileBoxes = new LinkedHashSet<>();

    /**
     * Adds the specified {@link FileBox} to the selection set.
     *
     * @param fb the FileBox to add
     */
    public static void add(FileBox fb) {
        selectedFileBoxes.add(fb);
    }

    /**
     * Removes the specified {@link FileBox} from the selection set.
     *
     * @param fb the FileBox to remove
     */
    public static void remove(FileBox fb) {
        selectedFileBoxes.remove(fb);
    }

    /**
     * Clears all current selections and removes selection styling from all selected FileBoxes.
     */
    public static void removeSelection() {
        selectedFileBoxes.forEach(FileBox::removeSelectedStyle);
        selectedFileBoxes.clear();
    }

    /**
     * Checks whether the specified {@link FileBox} is currently selected.
     *
     * @param fb the FileBox to check
     * @return {@code true} if the FileBox is selected, {@code false} otherwise
     */
    public static boolean isSelected(FileBox fb) {
        return selectedFileBoxes.contains(fb);
    }

    /**
     * Returns a list of {@link File} objects corresponding to the currently selected FileBoxes.
     *
     * @return a list of selected files
     */
    public static List<File> getSelectedFiles() {
        return selectedFileBoxes.stream().map(FileBox::getFile).toList();
    }

    /**
     * Returns an immutable copy of the currently selected {@link FileBox} instances.
     *
     * @return a set of selected FileBoxes
     */
    public static Set<FileBox> getSelectedFileBoxes() {
        return Set.copyOf(selectedFileBoxes);
    }
}
