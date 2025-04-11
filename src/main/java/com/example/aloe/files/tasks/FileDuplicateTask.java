package com.example.aloe.files.tasks;

import com.example.aloe.Main;
import javafx.application.Platform;

import java.io.File;
import java.util.List;

public class FileDuplicateTask extends FileCopyTask {

    public FileDuplicateTask(File file, boolean autoStart) {
        this.files = List.of(file);

        if (autoStart) runTask();
    }

    public FileDuplicateTask(List<File> files, boolean autoStart) {
        this.files = files;

        if (autoStart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        tryAddToTasksList("duplicating", "to", new File(files.getFirst().getParent()).getName());

        for (File file : files) {
            copyNextTo(file.toPath(), file.toPath());
            updateProgress();
        }

        Platform.runLater(() -> new Main().refreshCurrentDirectory());
        return null;
    }
}