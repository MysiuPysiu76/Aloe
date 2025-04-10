package com.example.aloe.files.tasks;

import com.example.aloe.Main;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileMoveTask extends FilesTask {

    private File destination;

    public FileMoveTask(File file, File destination, boolean autostart) {
        this.files = List.of(file);
        this.destination = destination;

        if (autostart) runTask();
    }

    public FileMoveTask(List<File> files, File destination, boolean autostart) {
        this.files = files;
        this.destination = destination;

        if (autostart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        if (destination != null) {
            for (File file : files) {
                File newFile = new File(destination, file.getName());

                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path destinationFile = newFile.toPath().resolve(file.relativize(file));
                        Files.move(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path newDir = newFile.toPath().resolve(file.toPath().relativize(dir));
                        if (!Files.exists(newDir)) {
                            Files.createDirectories(newDir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }

        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }
}