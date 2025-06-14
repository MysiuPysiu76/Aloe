package com.example.aloe.elements.files;

import com.example.aloe.elements.navigation.NavigationPanel;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code FilesLoader} class is responsible for loading and displaying the content of directories
 * within the Aloe file explorer. It manages directory navigation, file filtering, sorting, and the
 * presentation of files either in list or grid views.
 * <p>
 * This class handles special pseudo-directories (e.g. "%trash%", "%disks%") and updates related UI components
 * such as the navigation panel and the files display pane.
 * </p>
 * <p>
 * It supports features such as remembering the last accessed folder, filtering hidden files according
 * to user settings, sorting files based on different criteria, and showing an informative message
 * when directories are empty.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * FilesLoader.load(new File("/home/user/Documents"));
 * }
 * </pre>
 * </p>
 *
 * @since 2.7.8
 */
public class FilesLoader {

    /**
     * Loads the content of the specified directory and displays it in the UI.
     * Optionally adds the directory to navigation history.
     *
     * @param directory   the directory to load
     * @param addToHistory whether to add this directory to the navigation history
     */
    public static void load(File directory, boolean addToHistory) {
        directory = resolveSpecialDirectory(directory);

        if (addToHistory) {
            DirectoryHistory.addDirectory(directory);
        }

        if (!directory.equals(CurrentDirectory.get())) {
            CurrentDirectory.set(directory);
            rememberLastFolder(directory);
        }

        if (isDisksView(directory)) {
            DisksLoader.load();
            NavigationPanel.updateFilesPath();
            return;
        }

        NavigationPanel.updateFilesPath();
        FilesPane.resetPosition();
        FilesPane.get().setFitToHeight(false);

        List<File> files = getSortedFiles(filterFiles(directory.listFiles()));

        if (files.isEmpty()) {
            FilesPane.set(createEmptyFolderMessage());
            FilesPane.get().setFitToHeight(true);
            return;
        }

        displayFiles(files);
    }

    /**
     * Loads the content of the specified directory and adds it to the navigation history.
     *
     * @param directory the directory to load
     */
    public static void load(File directory) {
        load(directory, true);
    }

    /**
     * Refreshes the current directory view by reloading its contents.
     */
    public static void refresh() {
        load(CurrentDirectory.get());
    }

    /**
     * Loads the parent directory of the current directory, if available.
     */
    public static void loadParent() {
        File parent = CurrentDirectory.get().getParentFile();
        if (parent != null) {
            load(parent);
        }
    }

    /**
     * Resolves special pseudo-directories like "%trash%" into their actual paths configured in settings.
     *
     * @param directory the directory to resolve
     * @return resolved directory, or the original if not special
     */
    private static File resolveSpecialDirectory(File directory) {
        if ("%trash%".equalsIgnoreCase(directory.toPath().toString())) {
            return new File(Settings.getSetting("files", "trash").toString());
        }
        return directory;
    }

    /**
     * Checks whether the given directory is the special "%disks%" view.
     *
     * @param directory the directory to check
     * @return {@code true} if the directory corresponds to "%disks%", {@code false} otherwise
     */
    private static boolean isDisksView(File directory) {
        return "%disks%".equalsIgnoreCase(directory.toPath().toString());
    }

    /**
     * Remembers the last accessed folder by saving it in application settings,
     * if the user preference for the start folder is set to "last".
     *
     * @param directory the directory to remember
     */
    private static void rememberLastFolder(File directory) {
        if ("last".equals(Settings.getSetting("files", "start-folder"))) {
            Settings.setSetting("files", "start-folder-location", directory.toPath().toString());
        }
    }

    /**
     * Filters the files array according to the user settings, such as whether to show hidden files.
     *
     * @param files the array of files to filter (can be null)
     * @return a list of files filtered according to user preferences
     */
    private static List<File> filterFiles(File[] files) {
        if (files == null) return List.of();
        boolean showHidden = Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden"));
        return Arrays.stream(files)
                .filter(file -> showHidden || !file.isHidden())
                .toList();
    }

    /**
     * Sorts the list of files based on user settings, including sorting criteria and
     * whether directories should be displayed before files.
     *
     * @param files the list of files to sort
     * @return a new list of sorted files
     */
    private static List<File> getSortedFiles(List<File> files) {
        boolean directoriesFirst = Boolean.TRUE.equals(Settings.getSetting("files", "display-directories-before-files"));
        Sorting sorting = Sorting.safeValueOf(Settings.getSetting("files", "sorting").toString().toUpperCase());

        return files.stream()
                .sorted((f1, f2) -> compareFiles(f1, f2, directoriesFirst, sorting))
                .toList();
    }

    /**
     * Compares two files for sorting purposes based on whether directories come first and
     * the selected sorting order.
     *
     * @param f1             the first file to compare
     * @param f2             the second file to compare
     * @param directoriesFirst whether directories should be sorted before files
     * @param sorting        the sorting criteria to apply
     * @return negative integer, zero, or positive integer as the first file is less than,
     * equal to, or greater than the second
     */
    private static int compareFiles(File f1, File f2, boolean directoriesFirst, Sorting sorting) {
        if (directoriesFirst) {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
        }

        return switch (sorting) {
            case NAMEASC -> f1.getName().compareToIgnoreCase(f2.getName());
            case NAMEDESC -> f2.getName().compareToIgnoreCase(f1.getName());
            case DATEASC -> Long.compare(f1.lastModified(), f2.lastModified());
            case DATEDESC -> Long.compare(f2.lastModified(), f1.lastModified());
            case SIZEASC -> Long.compare(f1.length(), f2.length());
            case SIZEDESC -> Long.compare(f2.length(), f1.length());
        };
    }

    /**
     * Displays the list of files in the UI either as a list or grid depending on user settings.
     *
     * @param files the list of files to display
     */
    private static void displayFiles(List<File> files) {
        boolean listView = "list".equals(Settings.getSetting("files", "view"));
        if (listView) {
            VBox list = new VBox();
            list.setAlignment(Pos.TOP_CENTER);
            list.setFillWidth(true);
            list.getStyleClass().add("transparent");
            list.getChildren().add(HorizontalFileBox.getInfoPanel());
            files.forEach(file -> list.getChildren().add(new HorizontalFileBox(file)));
            FilesPane.set(list);
        } else {
            FlowPane grid = new FlowPane(5, 5);
            files.forEach(file -> grid.getChildren().add(new VerticalFileBox(file)));
            FilesPane.set(grid);
        }
    }

    /**
     * Creates a UI component that displays a message indicating that the current folder is empty.
     *
     * @return a VBox containing the empty folder icon and message
     */
    private static VBox createEmptyFolderMessage() {
        FontIcon icon = FontIcon.of(FontAwesome.FOLDER_OPEN_O);
        icon.setIconSize(65);
        icon.getStyleClass().add("font-icon");

        Label title = new Label(Translator.translate("window.empty-folder"));
        title.getStyleClass().add("text");
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-padding: 15px;");

        VBox box = new VBox(icon, title);
        box.setAlignment(Pos.CENTER);
        box.setFillWidth(true);
        return box;
    }
}
