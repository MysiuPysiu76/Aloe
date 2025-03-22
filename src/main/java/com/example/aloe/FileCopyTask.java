package com.example.aloe;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileCopyTask extends Task<Void> {
    private final List<File> files;
    private final Path destination;

    public FileCopyTask(List<File> files) {
        this.files = files;
        this.destination = FilesOperations.getCurrentDirectory().toPath();
    }

    public FileCopyTask(File file) {
        this.files = List.of(file);
        this.destination = FilesOperations.getCurrentDirectory().toPath();
    }

    @Override
    protected Void call() throws Exception {
        for (File source : files) {
            if (!source.exists()) continue;

            Path target = destination.resolve(source.getName());

            if (Files.exists(target)) {
                handleExistingFile(source, target);
            } else {
                copyRecursive(source.toPath(), target);
            }
        }
        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }

    private void handleExistingFile(File source, Path target) {
        Platform.runLater(() -> {
            FileOperation operation = new FileOperation(FileOperation.OperationType.COPY, source, destination.toFile());
            FileDecision decision = source.isFile() ?
                    WindowService.addFileDecisionAskToExistFileWindow(operation) :
                    WindowService.addDirectoryDecisionAskToExistFileWindow(operation);

            try {
                switch (decision) {
                    case COMBINE -> copyRecursive(source.toPath(), target);
                    case NEXT_TO -> copyNextTo(source.toPath(), target);
                    case REPLACE -> copyReplace(source.toPath(), target);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void copyRecursive(Path source, Path destination) throws IOException {
        if (Files.isDirectory(source)) {
            Files.createDirectories(destination);

            try (DirectoryStream<Path> entries = Files.newDirectoryStream(source)) {
                for (Path entry : entries) {
                    copyRecursive(entry, destination.resolve(entry.getFileName()));
                }
            }
        } else {
            copyFile(source, destination);
        }
    }

    private void copyFile(Path source, Path destination) throws IOException {
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(destination)) {
            byte[] buffer = new byte[1048576];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void copyNextTo(Path source, Path destination) throws IOException {
        int index = 1;
        String extension = FilesOperations.getExtensionWithDot(destination.toFile());
        Path parent = destination.getParent();
        Path newDestination = destination;

        while (Files.exists(newDestination)) {
            String newFileName = FilesOperations.getFileName(destination.toFile()) + " (" + Translator.translate("utils.copy") + " " + index + ")" + extension;
            newDestination = parent.resolve(newFileName);
            index++;
        }

        copyRecursive(source, newDestination);
    }

    private void copyReplace(Path source, Path destination) throws IOException {
        FilesOperations.deleteFile(destination.toFile());
        copyRecursive(source, destination);
    }
}