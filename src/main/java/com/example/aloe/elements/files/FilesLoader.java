package com.example.aloe.elements.files;

import com.example.aloe.Main;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.settings.Settings;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;

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

        if (Settings.getSetting("files", "view").equals("list")) {
            ListView<String> list = new ListView<>();
            list.getItems().addAll(files.stream().map(File::getName).toList());

            list.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    String selectedItem = list.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        FilesLoader.load(new File(CurrentDirectory.get(), selectedItem));
                    }
                }
            });

            list.setOnContextMenuRequested(e -> {
                String selectedItem = list.getSelectionModel().getSelectedItem();
                FileBoxContextMenu menu = new FileBoxContextMenu(new File(CurrentDirectory.get(), selectedItem));
                menu.show(list, e.getScreenX(), e.getScreenY());
                e.consume();
            });

            Main.filesPane.setContent(list);
        } else {
            FlowPane grid = new FlowPane();
            grid.setVgap(4);
            grid.setHgap(4);

            for (File file : files) {
                FileBox box = new FileBox(file);
                box.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                        FilesOpener.open(box.getFile());
                    } else if (e.isControlDown()) {
                        box.setSelected();
                    } else {
                        FileBox.removeSelection();
                    }
                });
                grid.getChildren().add(box);
            }
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