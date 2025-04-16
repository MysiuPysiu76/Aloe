package com.example.aloe.window.interior;

import com.example.aloe.utils.Translator;
import com.example.aloe.WindowComponents;
import com.example.aloe.utils.Validator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SingleInteriorWindow extends InteriorWindow {

    protected TextField input;

    public SingleInteriorWindow(String title, String description, String placeholder, String confirmButtonText) {
        super();
        input = getInput(placeholder, Translator.translate(description));
        input.setStyle("-fx-border-color: #62d0de;");
        Label error = getInfoLabel(null);
        confirmButton = getConfirmButton(Translator.translate(confirmButtonText));
        input.textProperty().addListener(observable -> Validator.validateFileName(error, confirmButton, Validator.validateFileName(input.getText())));
        Platform.runLater(input::requestFocus);

        this.getChildren().addAll(getTitleLabel(Translator.translate(title)),
                getInfoLabel(Translator.translate(description)),
                input,
                getBottomPanel(error, WindowComponents.getSpacer(), getCancelButton(), confirmButton));
    }
}