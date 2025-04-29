package com.example.aloe.files.tasks;

import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.files.archive.ArchiveHandler;
import com.example.aloe.settings.Settings;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileOpenerTask extends FilesTask {

    private File file;

    public FileOpenerTask(File file, boolean autoStart) {
        this.file = file;

        if (autoStart) runTask();
    }

    @Override
    protected Void call() throws Exception {
        if (file.isFile()) {
            if (FilesUtils.isFileArchive(file) && Boolean.TRUE.equals(Settings.getSetting("files", "extract-on-click"))) {

                Platform.runLater(() -> {
                    ArchiveHandler.extract(file);
                    FilesLoader.refresh();
                });
            } else {
                openFile();
            }
        }
        return null;
    }

    private void openFile() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
