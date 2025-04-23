package com.example.aloe.files.tasks;

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
            openFile();
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