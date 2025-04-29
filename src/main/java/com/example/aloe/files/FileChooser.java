package com.example.aloe.files;

import com.example.aloe.Main;
import com.example.aloe.utils.Translator;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * The {@code FileChooser} class provides utility methods for opening
 * file and directory chooser dialogs in the application.
 * <p>
 * It uses JavaFX {@link javafx.stage.FileChooser} and {@link javafx.stage.DirectoryChooser}
 * to let users select files or folders from the file system.
 * The initial directory is set to the current working directory tracked by {@link CurrentDirectory}.
 * <p>
 * Dialog titles are translated using the {@link Translator} class to support multiple languages.
 *
 * @since 1.7.7
 */
public class FileChooser {

    /**
     * Opens a directory chooser dialog allowing the user to select a folder.
     *
     * @return the selected directory as a {@link File}, or {@code null} if the user cancels the operation
     */
    public static File chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Translator.translate("window.other.chose-directory"));
        directoryChooser.setInitialDirectory(CurrentDirectory.get());
        return directoryChooser.showDialog(Main.scene.getWindow());
    }

    /**
     * Opens a file chooser dialog allowing the user to select a file.
     * <p>
     * Only one file can be selected at a time. The dialog allows all file types.
     *
     * @return the selected file as a {@link File}, or {@code null} if the user cancels the operation
     */
    public static File chooseFile() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle(Translator.translate("window.other.choose-file"));
        fileChooser.setInitialDirectory(CurrentDirectory.get());
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(Main.scene.getWindow());
    }
}