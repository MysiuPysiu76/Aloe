package com.example.aloe;

import com.example.aloe.utils.Translator;
import javafx.scene.control.Button;

public class WindowComponents {

    public static Button getConfirmButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px; -fx-background-color: #01496c; -fx-text-fill: #f8fafb;");
        return button;
    }

    public static Button getCancelButton() {
        Button button = new Button(Translator.translate("button.cancel"));
        button.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        return button;
    }

    public static Button getButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        return button;
    }
}