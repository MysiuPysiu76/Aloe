package com.example.aloe.window.interior;

import com.example.aloe.Main;
import com.example.aloe.Translator;
import com.example.aloe.WindowComponents;
import com.example.aloe.archive.ArchiveHandler;
import com.example.aloe.archive.ArchiveParameters;
import com.example.aloe.archive.ArchiveType;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CompressWindow extends InteriorWindow {

    public CompressWindow(List<File> files) {
        super();

        TextField fileName = getInput(Translator.translate("window.interior.archive"), Translator.translate("window.interior.archive.name"));
        TextField password = getInput(null, Translator.translate("window.interior.archive.password"));
        Label error = getInfoLabel(null);

        ComboBox<ArchiveType> archiveType = new ComboBox<>();
        archiveType.setValue(ArchiveType.ZIP);
        List<ArchiveType> filteredList = Arrays.stream(ArchiveType.values()).filter(type -> type != ArchiveType.RAR).toList();
        archiveType.setItems(FXCollections.observableArrayList(filteredList));
        HBox.setHgrow(fileName, Priority.ALWAYS);

        HBox nameBox = new HBox(fileName, archiveType);
        nameBox.setSpacing(7);
        nameBox.setAlignment(Pos.CENTER);

        confirmButton = getConfirmButton(Translator.translate("window.interior.archive.create"));

        fileName.textProperty().addListener(observable -> Main.validateFileName(error, confirmButton, Main.validateFileName(fileName.getText() + archiveType.getValue().getExtension())));

        archiveType.valueProperty().addListener((observable, oldValue, newValue) -> {
            password.setDisable(newValue != ArchiveType.ZIP);
            Main.validateFileName(error, confirmButton, Main.validateFileName(fileName.getText() + archiveType.getValue().getExtension()));
        });

        this.getChildren().addAll(getTitleLabel(Translator.translate("window.interior.archive.title")),
                getInfoLabel(Translator.translate("window.interior.archive.name")),
                nameBox,
                getInfoLabel(Translator.translate("window.interior.archive.password")),
                password,
                getBottomPanel(error, WindowComponents.getSpacer(), getCancelButton(), confirmButton));

        this.setOnConfirm(event -> {
            hideOverlay();
            ArchiveHandler.compress(new ArchiveParameters(files, archiveType.getValue(), fileName.getText() + archiveType.getValue().getExtension(), (password.getText() == null) ? null : password.getText().isEmpty() ? null : password.getText() ));
            hideOverlay();
        });
    }
}