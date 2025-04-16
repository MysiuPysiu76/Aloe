package com.example.aloe.window.interior;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.utils.Translator;

import java.io.File;

public class DirectoryWindow extends SingleInteriorWindow {

    public DirectoryWindow() {
        super("window.interior.directory.create-folder", "window.interior.directory.name", Translator.translate("window.interior.directory.placeholder"), "window.interior.directory.create");

        this.setOnConfirm(e -> {
            File newFile = new File(CurrentDirectory.get(), input.getText().trim());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            hideOverlay();
        });
    }
}