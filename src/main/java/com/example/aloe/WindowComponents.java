package com.example.aloe;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowComponents {
    public static Stage getInternalStage(double width, double height) {
        Stage stage = new Stage();
        stage.setWidth(width);
        stage.setHeight(height);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        return stage;
    }

    public static Label getTitle(String text) {
        Label label = new Label(text);
        label.setPadding(new Insets(15, 10, 10, 10));
        label.setStyle("-fx-font-size: 17px");
        return label;
    }

    public static Button getConfirmButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        return button;
    }

    public static Button getCancelButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        return button;
    }

    public static TextField getTextField(String text, String prompt, double width) {
        TextField input = new TextField(text);
        input.setStyle("-fx-font-size: 14px");
        input.setMinWidth(width);
        input.setMaxWidth(width);
        input.setPadding(new Insets(7, 10, 7, 10));
        input.setPromptText(prompt);
        return input;
    }
}