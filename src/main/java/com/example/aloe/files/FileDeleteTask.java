package com.example.aloe.files;

import com.example.aloe.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileDeleteTask extends Task<Void> {
    private final List<File> files;

    public FileDeleteTask(List<File> files) {
        this.files = files;
    }

    public FileDeleteTask(File file) {
        this.files = List.of(file);
    }

    @Override
    protected Void call() throws Exception {
        for (File file : files) {
            if (file.exists()) {
                deleteRecursive(file.toPath());
            }
        }

        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }

    public static void delete(File file) {
        Thread thread = new Thread(new FileDeleteTask(file));
        thread.setDaemon(true);
        thread.start();
    }

    public static void delete(List<File> files) {
        Thread thread = new Thread(new FileDeleteTask(files));
        thread.setDaemon(true);
        thread.start();
    }

    public static void deleteInCurrentThread(Path path) throws Exception {
        new FileDeleteTask(List.of()).deleteRecursive(path);
    }

    public static void deleteInCurrentThread(File file) throws Exception {
        new FileDeleteTask(List.of()).deleteRecursive(file.toPath());
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
