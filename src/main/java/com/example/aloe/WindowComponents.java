package com.example.aloe;

import com.example.aloe.utils.Translator;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

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
}