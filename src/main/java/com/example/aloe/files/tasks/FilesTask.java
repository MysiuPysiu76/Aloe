package com.example.aloe.files.tasks;

import com.example.aloe.Translator;
import com.example.aloe.Utils;
import com.example.aloe.elements.navigation.ProgressManager;
import com.example.aloe.files.FilesUtils;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

abstract class FilesTask extends Task<Void> {

    protected final DoubleProperty progressProperty = new SimpleDoubleProperty(0);
    protected final StringProperty descriptionProperty = new SimpleStringProperty();
    protected List<File> files;
    protected long totalSize;
    protected String totalSizeString;
    protected long progress = 0;

    protected void runTask() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    protected void tryAddToTasksList(String type, String destination, String destinationName) {
        totalSize = FilesUtils.calculateFileSize(files);
        if(!(files.size() == 1 && files.getFirst().length() < 1048576)) {
            String title = Translator.translate("task." + type) + files.size() + Translator.translate("task." + (files.size() == 1 ? "item-" : "items-") + destination) + destinationName;
            ProgressManager.addTask(title, progressProperty, descriptionProperty);
            totalSizeString = Utils.convertBytesByUnit(totalSize);
        }
    }

    protected void updateProgress() {
        Platform.runLater(() -> {
            progressProperty.set(Utils.calculatePercentage(progress, totalSize) / 100);
            descriptionProperty.set(Utils.convertBytesByUnit(progress) + " / " + totalSizeString);
        });
    }
}