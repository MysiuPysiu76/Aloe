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

public class FilesLoader {

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

    public static void load(File directory) {
        load(directory, true);
    }

    public static void refresh() {
        load(CurrentDirectory.get());
    }

    public static void loadParent() {
        File parent = CurrentDirectory.get().getParentFile();
        if (parent != null) {
            load(parent);
        }
    }

    private static File resolveSpecialDirectory(File directory) {
        if ("%trash%".equalsIgnoreCase(directory.toPath().toString())) {
            return new File(Settings.getSetting("files", "trash").toString());
        }
        return directory;
    }

    private static boolean isDisksView(File directory) {
        return "%disks%".equalsIgnoreCase(directory.toPath().toString());
    }

    private static void rememberLastFolder(File directory) {
        if ("last".equals(Settings.getSetting("files", "start-folder"))) {
            Settings.setSetting("files", "start-folder-location", directory.toPath().toString());
        }
    }

    private static List<File> filterFiles(File[] files) {
        if (files == null) return List.of();
        boolean showHidden = Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden"));
        return Arrays.stream(files)
                .filter(file -> showHidden || !file.isHidden())
                .toList();
    }

    private static List<File> getSortedFiles(List<File> files) {
        boolean directoriesFirst = Boolean.TRUE.equals(Settings.getSetting("files", "display-directories-before-files"));
        Sorting sorting = Sorting.safeValueOf(Settings.getSetting("files", "sorting").toString().toUpperCase());

        return files.stream()
                .sorted((f1, f2) -> compareFiles(f1, f2, directoriesFirst, sorting))
                .toList();
    }

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
