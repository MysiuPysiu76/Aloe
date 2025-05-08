package com.example.aloe.window.interior;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.utils.Translator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * {@code FileWindow} is an internal window that allows the user to create a new text file
 * in the current working directory. It presents a simple input field for entering the file name
 * and a confirmation button to trigger file creation.
 *
 * <p>This class extends {@link SingleInteriorWindow}, leveraging its minimalistic layout for user input.
 * It integrates with {@link CurrentDirectory} to determine the location where the new file will be created.</p>
 *
 * <p>If the specified file does not already exist, it will be created with an empty initial line.</p>
 *
 * @see SingleInteriorWindow
 * @since 2.2.2
 */
public class FileWindow extends SingleInteriorWindow {

    /**
     * Constructs a {@code FileWindow} that prompts the user to enter a name for a new file.
     * The UI is automatically translated using predefined translation keys.
     * Upon confirmation, the file is created in the current directory if it does not already exist.
     */
    public FileWindow() {
        super(
                "window.interior.file.create-file",
                "window.interior.file.name",
                Translator.translate("window.interior.file.placeholder"),
                "window.interior.file.create"
        );

        this.setOnConfirm(event -> {
            File newFile = new File(CurrentDirectory.get(), this.input.getText().trim());
            if (!newFile.exists()) {
                try {
                    Files.write(
                            newFile.toPath(),
                            List.of(""),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            hideOverlay();
        });
    }
}
