package com.example.aloe.window.interior;

import java.util.concurrent.CompletableFuture;

public class PasswordPromptWindow extends SingleInteriorWindow {

    public PasswordPromptWindow(CompletableFuture<Void> completableFuture, StringBuilder password) {
        super("window.interior.archive.extract.password-required", "window.interior.archive.extract.enter-password", null, "button.extract");

        this.setOnConfirm(event -> {
            password.append(input.getText());
            completableFuture.complete(null);
            hideOverlay();
        });
    }
}