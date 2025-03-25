package com.example.aloe.files.tasks;

import javafx.concurrent.Task;

abstract class FilesTask extends Task<Void> {

    protected void runTask() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
}