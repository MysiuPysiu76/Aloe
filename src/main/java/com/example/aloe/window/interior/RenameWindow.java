package com.example.aloe.window.interior;

import com.example.aloe.files.CurrentDirectory;

import java.io.File;

/**
 * {@code RenameWindow} is a specialized internal window for renaming a file or directory.
 * It provides a text input field pre-filled with the current name of the selected file or directory,
 * and a confirmation button that triggers the renaming operation.
 *
 * <p>This class extends {@link SingleInteriorWindow} to reuse a simplified input-based UI layout.
 * The rename operation is performed in the current working directory obtained from {@link CurrentDirectory}.</p>
 *
 * <p>The displayed labels and buttons are dynamically translated using keys based on the type of the file system item (file or directory).</p>
 *
 * @see SingleInteriorWindow
 * @since 2.2.0
 */
public class RenameWindow extends SingleInteriorWindow {

    /**
     * Constructs a {@code RenameWindow} that allows the user to rename the specified file or directory.
     * The UI adapts based on whether the input represents a file or a directory.
     *
     * @param file the file or directory to be renamed
     */
    public RenameWindow(File file) {
        super(
                "window.interior.rename." + (file.isDirectory() ? "directory" : "file"),
                "window.interior." + (file.isDirectory() ? "directory" : "file") + ".name",
                file.getName(),
                "window.interior.rename"
        );

        this.setOnConfirm(event -> {
            File newFile = new File(CurrentDirectory.get(), this.input.getText().trim());
            file.renameTo(newFile);
            hideOverlay();
        });
    }
}
