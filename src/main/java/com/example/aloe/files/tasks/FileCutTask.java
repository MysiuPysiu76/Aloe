package com.example.aloe.files.tasks;

import com.example.aloe.FilesOperations;
import com.example.aloe.Main;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class FileCutTask extends FileCopyTask {

    private Path destination;

    public FileCutTask(File file, boolean autoStart) {
        this.files = List.of(file);
        this.destination = FilesOperations.getCurrentDirectory().toPath();

        if (autoStart) runTask();
    }

    public FileCutTask(List<File> files, boolean autoStart) {
        this.files = files;
        this.destination = FilesOperations.getCurrentDirectory().toPath();

        if (autoStart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("cutting", "to", destination.getFileName().toString());

        for (File file : files) {
            copyRecursive(file.toPath(), destination.resolve(file.getName()));
            FileDeleteTask.deleteInCurrentThread(file);
            progress += file.length();
            updateProgress();
        }

        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }
}