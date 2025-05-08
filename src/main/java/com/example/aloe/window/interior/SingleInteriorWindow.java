package com.example.aloe.window.interior;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import com.example.aloe.utils.Validator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * {@code SingleInteriorWindow} is a simplified version of an internal window that displays
 * a single text input field along with descriptive labels and validation.
 * It is typically used when only one user input is required, such as entering a name,
 * renaming a file, or specifying a single configuration value.
 *
 * <p>This class extends {@link InteriorWindow} and uses its styling and layout methods to maintain a
 * consistent UI. It includes input validation and visual error messaging, and it is designed
 * to be extended or instantiated directly for quick input prompts.</p>
 *
 * @see InteriorWindow
 * @see Validator
 */
public class SingleInteriorWindow extends InteriorWindow {

    /**
     * The main input field used for collecting the user's input.
     */
    protected TextField input;

    /**
     * Constructs a {@code SingleInteriorWindow} with the specified UI text and configuration.
     * Initializes the input field, confirmation button, and validation logic.
     *
     * @param title              the translation key for the window title
     * @param description        the translation key for the input description label
     * @param placeholder        the placeholder text for the input field
     * @param confirmButtonText  the translation key for the confirm button label
     */
    public SingleInteriorWindow(String title, String description, String placeholder, String confirmButtonText) {
        super();

        input = getInput(placeholder, Translator.translate(description));
        input.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));

        Label error = getInfoLabel(null);

        confirmButton = getConfirmButton(Translator.translate(confirmButtonText));

        input.textProperty().addListener(observable ->
                Validator.validateFileName(
                        error,
                        confirmButton,
                        Validator.validateFileName(input.getText())
                )
        );

        Platform.runLater(input::requestFocus);

        this.getChildren().addAll(
                getTitleLabel(Translator.translate(title)),
                getInfoLabel(Translator.translate(description)),
                input,
                getBottomPanel(error, new HBoxSpacer(), getCancelButton(), confirmButton)
        );
    }
}
