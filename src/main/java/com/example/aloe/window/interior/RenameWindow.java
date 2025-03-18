package com.example.aloe.window.interior;

import com.example.aloe.FilesOperations;

import java.io.File;

public class RenameWindow extends SingleInteriorWindow {

    public RenameWindow(File file) {
        super("window.interior.rename." + (file.isDirectory() ? "directory" : "file"), "window.interior." + (file.isDirectory() ? "directory" : "file") + ".name", file.getName(), "window.interior.rename");

        this.setOnConfirm(event -> {
            File newFile = new File(FilesOperations.getCurrentDirectory(), this.input.getText().trim());
            file.renameTo(newFile);
            hideOverlay();
        });
    }
}