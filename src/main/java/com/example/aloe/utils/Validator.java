package com.example.aloe.utils;

import com.example.aloe.files.CurrentDirectory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;

/**
 * The {@code Validator} class provides utility methods for validating file names
 * and updating JavaFX UI elements based on validation results.
 * <p>
 * It ensures that file names are not empty, not blank, do not contain illegal characters,
 * and are not already in use within the current working directory.
 * <p>
 * Error messages are localized using the {@link Translator} class.
 *
 * <p>Example usage:
 * <pre>
 *     String validationMessage = Validator.validateFileName("example.txt");
 *     if (validationMessage != null) {
 *         System.out.println("Invalid file name: " + validationMessage);
 *     }
 * </pre>
 *
 * <p>Example of validating input and updating UI controls:
 * <pre>
 *     Validator.validateFileName(errorLabel, createButton, validationMessage);
 * </pre>
 *
 * @since 1.7.4
 */
public class Validator {

    /**
     * Validates a file name string by checking for common errors:
     * <ul>
     *     <li>Empty name</li>
     *     <li>Blank name (only whitespace)</li>
     *     <li>Illegal character '/'</li>
     *     <li>File name already exists in the current directory</li>
     * </ul>
     * If any validation error occurs, a localized error message is returned;
     * otherwise, {@code null} is returned indicating the name is valid.
     *
     * @param name the file name to validate
     * @return the validation error message if invalid; {@code null} if the file name is valid
     */
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

    /**
     * Updates a {@link Label} and a {@link Button} based on the file name validation result.
     * <p>
     * If the validation text is {@code null} or empty, the button is enabled and the error label is cleared.
     * Otherwise, the error message is shown in the label, and the button is disabled.
     *
     * @param error  the {@link Label} where the error message will be displayed
     * @param button the {@link Button} that will be enabled or disabled
     * @param text   the validation error message; {@code null} or empty if valid
     */
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
