package com.example.aloe.window.interior;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import com.example.aloe.utils.Validator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SingleInteriorWindow extends InteriorWindow {

    protected TextField input;

    public SingleInteriorWindow(String title, String description, String placeholder, String confirmButtonText) {
        super();
        input = getInput(placeholder, Translator.translate(description));
        input.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));
        Label error = getInfoLabel(null);
        confirmButton = getConfirmButton(Translator.translate(confirmButtonText));
        input.textProperty().addListener(observable -> Validator.validateFileName(error, confirmButton, Validator.validateFileName(input.getText())));
        Platform.runLater(input::requestFocus);

        this.getChildren().addAll(getTitleLabel(Translator.translate(title)),
                getInfoLabel(Translator.translate(description)),
                input,
                getBottomPanel(error, new HBoxSpacer(), getCancelButton(), confirmButton));
    }
}