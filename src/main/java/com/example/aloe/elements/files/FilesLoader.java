package com.example.aloe.elements.files;

import com.example.aloe.Main;
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
        if (!directory.equals(CurrentDirectory.get())) {
            DirectoryHistory.addDirectory(directory);
            CurrentDirectory.set(directory);
        }

        List<File> files = getSortedFiles(getFiles(directory.listFiles()));
        Main.filesPane.setVvalue(0);

        if (Settings.getSetting("files", "view").equals("list")) {
            VBox list = new VBox();
            list.setAlignment(Pos.TOP_CENTER);
            list.setFillWidth(true);
            list.getChildren().add(HorizontalFileBox.getInfoPanel());

            files.forEach(file -> list.getChildren().add(new HorizontalFileBox(file)));
            Main.filesPane.setContent(list);
        } else {
            FlowPane grid = new FlowPane();
            grid.setVgap(4);
            grid.setHgap(4);

            files.forEach(file -> grid.getChildren().add(new VerticalFileBox(file)));
            Main.filesPane.setContent(grid);
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
        return files.stream()
                .sorted((file1, file2) -> {
                    if (directoriesFirst) {
                        if (file1.isDirectory() && !file2.isDirectory()) return -1;
                        if (!file1.isDirectory() && file2.isDirectory()) return 1;
                    }
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }).toList();
    }

    public static void loadParent() {
        if (CurrentDirectory.get().getParent() != null) {
            FilesLoader.load(CurrentDirectory.get().getParentFile());
        }
    }
}