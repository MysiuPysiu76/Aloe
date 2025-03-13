package com.example.aloe;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SingleInteriorWindow extends InteriorWindow {

    protected TextField input;
    protected Button confirmButton;

    public SingleInteriorWindow(String title, String description, String placeholder, String confirmButtonText) {
        input = getInput(Translator.translate(placeholder), Translator.translate(description));
        Label error = getInfoLabel(null);
        confirmButton = getConfirmButton(Translator.translate(confirmButtonText));

        input.textProperty().addListener(observable -> Main.validateFileName(error, confirmButton, Main.validateFileName(input.getText())));

        this.getChildren().addAll(getTitleLabel(Translator.translate(title)),
                getInfoLabel(Translator.translate(description)),
                input,
                getBottomPanel(error, WindowComponents.getSpacer(), getCancelButton(), confirmButton));
    }

    protected void setOnConfirm(EventHandler<? super MouseEvent> eventHandler) {
        confirmButton.setOnMouseClicked(eventHandler);
    }
}