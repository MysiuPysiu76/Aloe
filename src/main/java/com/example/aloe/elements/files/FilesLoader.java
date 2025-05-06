package com.example.aloe.elements.files;

import com.example.aloe.elements.navigation.NavigationPanel;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.Settings;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesLoader {

    public static void load(File directory) {
        if (directory.toPath().toString().equalsIgnoreCase("%trash%"))
            directory = new File(Settings.getSetting("files", "trash").toString());
        if (!directory.equals(CurrentDirectory.get())) {
            DirectoryHistory.addDirectory(directory);
            CurrentDirectory.set(directory);
            if (Settings.getSetting("files", "start-folder").equals("last"))
                Settings.setSetting("files", "start-folder-location", directory.toPath().toString());
        }

        NavigationPanel.updateFilesPath();
        List<File> files = getSortedFiles(getFiles(directory.listFiles()));
        FilesPane.resetPosition();

        if (Settings.getSetting("files", "view").equals("list")) {
            VBox list = new VBox();
            list.setAlignment(Pos.TOP_CENTER);
            list.setFillWidth(true);
            list.getChildren().add(HorizontalFileBox.getInfoPanel());

            files.forEach(file -> list.getChildren().add(new HorizontalFileBox(file)));
            FilesPane.set(list);
        } else {
            FlowPane grid = new FlowPane();
            grid.setVgap(5);
            grid.setHgap(5);

            files.forEach(file -> grid.getChildren().add(new VerticalFileBox(file)));
            FilesPane.set(grid);
        }
    }

    public static void refresh() {
        load(CurrentDirectory.get());
    }

    private static List<File> getFiles(File[] files) {
        boolean showHidden = Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden"));
        return Arrays.stream(files).filter(file -> !file.isHidden() || showHidden).toList();
    }

    private static List<File> getSortedFiles(List<File> files) {
        boolean directoriesFirst = Boolean.TRUE.equals(Settings.getSetting("files", "display-directories-before-files"));
        Sorting sorting = Sorting.safeValueOf(Settings.getSetting("files", "sorting").toString().toUpperCase());
        return files.stream()
                .sorted((file1, file2) -> {
                    if (directoriesFirst) {
                        if (file1.isDirectory() && !file2.isDirectory()) return -1;
                        if (!file1.isDirectory() && file2.isDirectory()) return 1;
                    }

                    switch (sorting) {
                        case NAMEASC -> {
                            return file1.getName().compareToIgnoreCase(file2.getName());
                        }
                        case NAMEDESC -> {
                            return file2.getName().compareToIgnoreCase(file1.getName());
                        }
                        case DATEASC -> {
                            return Long.compare(file1.lastModified(), file2.lastModified());
                        }
                        case DATEDESC -> {
                            return Long.compare(file2.lastModified(), file1.lastModified());
                        }
                        case SIZEASC -> {
                            return Long.compare(file1.length(), file2.length());
                        }
                        case SIZEDESC -> {
                            return Long.compare(file2.length(), file1.length());
                        }
                    }
                    return 0;
                }).toList();
    }

    public static void loadParent() {
        if (CurrentDirectory.get().getParent() != null) {
            FilesLoader.load(CurrentDirectory.get().getParentFile());
        }
    }
}
