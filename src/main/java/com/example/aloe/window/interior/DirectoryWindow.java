package com.example.aloe.window.interior;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.utils.Translator;

import java.io.File;

/**
 * {@code DirectoryWindow} is an internal window that allows the user to create a new directory
 * within the current working directory. It presents a simple input interface for entering
 * the folder name, and executes the directory creation upon confirmation.
 *
 * <p>This class extends {@link SingleInteriorWindow}, utilizing its minimal UI structure
 * to prompt for a single input and display translated labels and placeholders.</p>
 *
 * <p>The window automatically prevents overwriting existing folders and ensures that
 * the directory is only created if it does not already exist.</p>
 *
 * @see SingleInteriorWindow
 * @since 2.2.3
 */
public class DirectoryWindow extends SingleInteriorWindow {

    /**
     * Constructs a {@code DirectoryWindow} that prompts the user to enter a name for a new directory.
     * The UI is initialized with localized strings using predefined translation keys.
     * Upon confirmation, the directory is created if it does not already exist.
     */
    public DirectoryWindow() {
        super(
                "window.interior.directory.create-folder",
                "window.interior.directory.name",
                Translator.translate("window.interior.directory.placeholder"),
                "window.interior.directory.create"
        );

        this.setOnConfirm(e -> {
            File newFile = new File(CurrentDirectory.get(), input.getText().trim());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            hideOverlay();
        });
    }
}
