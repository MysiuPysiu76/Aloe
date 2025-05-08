package com.example.aloe.window.interior;

import java.util.concurrent.CompletableFuture;

/**
 * {@code PasswordPromptWindow} is a simple internal window used to prompt the user
 * for a password, typically when extracting password-protected archives.
 *
 * <p>This class extends {@link SingleInteriorWindow}, using its single-input layout,
 * and communicates the entered password asynchronously using a {@link CompletableFuture}.
 * The password is stored in a shared {@link StringBuilder} passed from the caller.</p>
 *
 * <p>The window displays a localized message indicating that a password is required,
 * and allows the user to enter and submit the password using a confirm button
 * labeled appropriately for extraction actions.</p>
 *
 * @see SingleInteriorWindow
 * @since 2.2.1
 */
public class PasswordPromptWindow extends SingleInteriorWindow {

    /**
     * Constructs a {@code PasswordPromptWindow} that captures a password input from the user
     * and completes a given {@link CompletableFuture} once the input is submitted.
     *
     * @param completableFuture a future that is completed when the user confirms the input;
     *                          used for asynchronous flow control
     * @param password          a {@link StringBuilder} instance to which the entered password is appended
     */
    public PasswordPromptWindow(CompletableFuture<Void> completableFuture, StringBuilder password) {
        super(
                "window.interior.archive.extract.password-required",
                "window.interior.archive.extract.enter-password",
                null,
                "button.extract"
        );

        this.setOnConfirm(event -> {
            password.append(input.getText());
            completableFuture.complete(null);
            hideOverlay();
        });
    }
}
