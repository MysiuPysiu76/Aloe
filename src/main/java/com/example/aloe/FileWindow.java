package com.example.aloe;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileWindow extends SingleInteriorWindow {

    public FileWindow() {
        super("window.interior.file.create-file", "window.interior.file.name", Translator.translate("window.interior.file.placeholder"), "window.interior.file.create");

        this.setOnConfirm(event -> {
            File newFile = new File(FilesOperations.getCurrentDirectory(), this.input.getText().trim());
            if (!newFile.exists()) {
                try {
                    Files.write(newFile.toPath(), List.of(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            hideOverlay();
        });
    }
}