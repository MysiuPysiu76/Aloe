package com.example.aloe;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

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

    public static Button getButton(String text) {
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

    public static Button getBackButton(String key, boolean leftIcon) {
        FontIcon icon;
        if (leftIcon) {
            icon = new FontIcon(FontAwesome.ANGLE_LEFT);
        } else {
            icon = new FontIcon(FontAwesome.ANGLE_RIGHT);
        }
        Button button = new Button(Translator.translate(key).intern(), icon);
        if (!leftIcon) {
            button.setContentDisplay(ContentDisplay.RIGHT);
        }
        icon.setIconSize(22);
        button.setFont(Font.font(14));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        button.setGraphicTextGap(6);
        return button;
    }

    public static Region getSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static Region getVBoxSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}