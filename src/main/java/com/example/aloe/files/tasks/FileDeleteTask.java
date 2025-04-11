package com.example.aloe.files.tasks;

import com.example.aloe.Main;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileDeleteTask extends FilesTask {

    public FileDeleteTask(List<File> files, boolean autoStart) {
        this.files = files;

        if (autoStart) runTask();
    }

    public FileDeleteTask(File file, boolean autoStart) {
        this.files = List.of(file);

        if (autoStart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("deleting", "from", new File(files.getFirst().getParent()).getName());

        for (File file : files) {
            if (file.exists()) {
                long fileLength = file.length();
                deleteRecursive(file.toPath());
                progress += fileLength;
            }
            updateProgress();
        }

        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }

    public static void deleteInCurrentThread(Path path) throws Exception {
        new FileDeleteTask(List.of(), false).deleteRecursive(path);
    }

    public static void deleteInCurrentThread(File file) throws Exception {
        new FileDeleteTask(List.of(), false).deleteRecursive(file.toPath());
    }

    public static void deleteInCurrentThread(List<File> files) throws Exception {
        for (File file : files) {
            deleteInCurrentThread(file.toPath());
        }
    }

    private void deleteRecursive(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteRecursive(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}
