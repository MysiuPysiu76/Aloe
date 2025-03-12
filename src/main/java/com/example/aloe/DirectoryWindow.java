package com.example.aloe;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;

public class DirectoryWindow extends InteriorWindow {
    public DirectoryWindow() {
        TextField input = getInput(Translator.translate("window.interior.directory.placeholder"), Translator.translate("window.interior.directory.name"));
        Label error = getInfoLabel(null);
        Button confirm = getConfirmButton(Translator.translate("window.interior.directory.create"));

        confirm.setOnMouseClicked(event -> {
            File newFile = new File(FilesOperations.getCurrentDirectory(), input.getText().trim());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            new Main().refreshCurrentDirectory();
            Main.hideDarkeningPlate();
        });

        input.textProperty().addListener(observable -> Main.validateFileName(error, confirm, Main.validateFileName(input.getText())));

        this.getChildren().addAll(getTitleLabel(Translator.translate("window.interior.directory.create-folder")),
                getInfoLabel(Translator.translate("window.interior.directory.name")),
                input,
                getBottomPanel(error, WindowComponents.getSpacer(), getCancelButton(), confirm));
    }
}