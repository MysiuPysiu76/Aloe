package com.example.aloe.files.tasks;

import com.example.aloe.*;
import com.example.aloe.files.FileDecision;
import com.example.aloe.files.FilesUtils;
import javafx.application.Platform;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileCopyTask extends FilesTask {

    private Path destination;

    private static boolean isCut = false;

    FileCopyTask() {
        this.files = null;
        this.destination = null;
        this.totalSize = 0;
    }

    public FileCopyTask(List<File> files, boolean autoStart) {
        if (isCut) {
            new FileCutTask(files, autoStart);
            return;
        }

        this.files = files;
        this.destination = FilesOperations.getCurrentDirectory().toPath();
        this.totalSize = FilesUtils.calculateFileSize(files);

        if (autoStart) runTask();
    }

    public FileCopyTask(List<File> files, File destination, boolean autoStart) {
        this(files, false);
        this.destination = destination.toPath();

        if (autoStart) runTask();
    }

    public FileCopyTask(File file, boolean autoStart) {
        if (isCut) {
            new FileCutTask(file, autoStart);
            return;
        }

        this.files = List.of(file);
        this.destination = FilesOperations.getCurrentDirectory().toPath();
        this.totalSize = FilesUtils.calculateFileSize(file);

        if (autoStart) runTask();
    }

    public FileCopyTask(File file, File destination, boolean autoStart) {
        this(file, false);
        this.destination = destination.toPath();

        if (autoStart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("copying", "to", destination.getFileName().toString());

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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void copyRecursive(Path source, Path destination) throws IOException {
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

    protected void copyFile(Path source, Path destination) throws IOException {
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(destination)) {

            byte[] buffer = new byte[1048576];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                progress += bytesRead;
                updateProgress();
            }
        }
    }

    protected void copyNextTo(Path source, Path destination) throws IOException {
        int index = 1;
        String extension = FilesUtils.getExtensionWithDot(destination.toFile());
        Path parent = destination.getParent();
        Path newDestination = destination;

        while (Files.exists(newDestination)) {
            String newFileName = FilesUtils.getFileName(destination.toFile()) + " (" + Translator.translate("utils.copy") + " " + index + ")" + extension;
            newDestination = parent.resolve(newFileName);
            index++;
        }

        Path finalNewDestination = newDestination;
        Platform.runLater(() -> {
            try {
                copyRecursive(source, finalNewDestination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void copyReplace(Path source, Path destination) throws Exception {
        FileDeleteTask.deleteInCurrentThread(destination);
        copyRecursive(source, destination);
    }

    public static void setCut(boolean cut) {
        isCut = cut;
    }

    public static boolean isCut() {
        return isCut;
    }
}