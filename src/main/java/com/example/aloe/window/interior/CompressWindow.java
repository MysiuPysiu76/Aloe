package com.example.aloe.window.interior;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import com.example.aloe.files.archive.ArchiveHandler;
import com.example.aloe.files.archive.ArchiveParameters;
import com.example.aloe.files.archive.ArchiveType;
import com.example.aloe.utils.Validator;
import javafx.application.Platform;
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

/**
 * {@code CompressWindow} is a specialized internal window that allows users to compress selected files
 * into an archive. It provides UI components for specifying the archive name, selecting the archive type,
 * and optionally entering a password for ZIP encryption.
 *
 * <p>This class extends {@link InteriorWindow} and utilizes shared UI styling and layout logic.
 * It dynamically validates the input and handles the archive creation process using {@link ArchiveHandler}.</p>
 *
 * <p>The supported archive types are filtered to exclude unsupported formats (e.g., RAR), and the
 * password field is only enabled when ZIP is selected as the format.</p>
 *
 * @see ArchiveHandler
 * @see ArchiveType
 * @see InteriorWindow
 * @since 2.1.6
 */
public class CompressWindow extends InteriorWindow {

    /**
     * Constructs a new {@code CompressWindow} for compressing the specified list of files.
     * Initializes and arranges all input components, sets up validation, and defines the archive creation logic.
     *
     * @param files the list of files to be archived
     */
    public CompressWindow(List<File> files) {
        super();

        TextField fileName = getInput(
                Translator.translate("window.interior.archive"),
                Translator.translate("window.interior.archive.name"));
        fileName.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));

        TextField password = getInput(null, Translator.translate("window.interior.archive.password"));
        password.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));

        Label error = getInfoLabel(null);

        ComboBox<ArchiveType> archiveType = new ComboBox<>();
        archiveType.setValue(ArchiveType.ZIP);
        List<ArchiveType> filteredList = Arrays.stream(ArchiveType.values())
                .filter(type -> type != ArchiveType.RAR)
                .toList();
        archiveType.setItems(FXCollections.observableArrayList(filteredList));
        HBox.setHgrow(fileName, Priority.ALWAYS);

        HBox nameBox = new HBox(fileName, archiveType);
        nameBox.setSpacing(7);
        nameBox.setAlignment(Pos.CENTER);

        confirmButton = getConfirmButton(Translator.translate("window.interior.archive.create"));

        fileName.textProperty().addListener(observable ->
                Validator.validateFileName(
                        error,
                        confirmButton,
                        Validator.validateFileName(fileName.getText() + archiveType.getValue().getExtension()))
        );

        archiveType.valueProperty().addListener((observable, oldValue, newValue) -> {
            password.setDisable(newValue != ArchiveType.ZIP);
            Validator.validateFileName(
                    error,
                    confirmButton,
                    Validator.validateFileName(fileName.getText() + archiveType.getValue().getExtension()));
        });

        Platform.runLater(fileName::requestFocus);

        this.getChildren().addAll(
                getTitleLabel(Translator.translate("window.interior.archive.title")),
                getInfoLabel(Translator.translate("window.interior.archive.name")),
                nameBox,
                getInfoLabel(Translator.translate("window.interior.archive.password")),
                password,
                getBottomPanel(error, new HBoxSpacer(), getCancelButton(), confirmButton)
        );

        this.setOnConfirm(event -> {
            hideOverlay();
            ArchiveHandler.compress(new ArchiveParameters(
                    files,
                    archiveType.getValue(),
                    fileName.getText() + archiveType.getValue().getExtension(),
                    (password.getText() == null || password.getText().isEmpty()) ? null : password.getText()
            ));
            hideOverlay();
        });
    }
}
