package com.example.aloe;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SingleInteriorWindow extends InteriorWindow {

    protected TextField input;

    public SingleInteriorWindow(String title, String description, String placeholder, String confirmButtonText) {
        super();
        input = getInput(placeholder, Translator.translate(description));
        Label error = getInfoLabel(null);
        confirmButton = getConfirmButton(Translator.translate(confirmButtonText));

        input.textProperty().addListener(observable -> Main.validateFileName(error, confirmButton, Main.validateFileName(input.getText())));

        this.getChildren().addAll(getTitleLabel(Translator.translate(title)),
                getInfoLabel(Translator.translate(description)),
                input,
                getBottomPanel(error, WindowComponents.getSpacer(), getCancelButton(), confirmButton));
    }
}