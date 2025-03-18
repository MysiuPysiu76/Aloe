package com.example.aloe.window.interior;

import com.example.aloe.FilesOperations;
import com.example.aloe.Translator;

import java.io.File;

public class DirectoryWindow extends SingleInteriorWindow {

    public DirectoryWindow() {
        super("window.interior.directory.create-folder", "window.interior.directory.name", Translator.translate("window.interior.directory.placeholder"), "window.interior.directory.create");

        this.setOnConfirm(e -> {
            File newFile = new File(FilesOperations.getCurrentDirectory(), input.getText().trim());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            hideOverlay();
        });
    }
}