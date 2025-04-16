package com.example.aloe.utils;

import com.example.aloe.files.CurrentDirectory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;

public class Validator {

    public static String validateFileName(String name) {
        if (name.isEmpty()) {
            return Translator.translate("validator.empty-name");
        }
        if (name.isBlank()) {
            return Translator.translate("validator.blank-name");
        }
        if (name.contains("/")) {
            return Translator.translate("validator.contains-slash");
        }
        if (new File(CurrentDirectory.get(), name).exists()) {
            return Translator.translate("validator.used-name");
        }
        return null;
    }

    public static void validateFileName(Label error, Button button, String text) {
        if (text == null || text.isEmpty()) {
            error.setText("");
            button.setDisable(false);
        } else {
            error.setText(text);
            button.setDisable(true);
        }
    }
}