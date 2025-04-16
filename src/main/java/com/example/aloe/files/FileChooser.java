package com.example.aloe.files;

import com.example.aloe.Main;
import com.example.aloe.utils.Translator;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class FileChooser {

    public static File chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Translator.translate("window.other.chose-directory"));
        directoryChooser.setInitialDirectory(CurrentDirectory.get());
        return directoryChooser.showDialog(Main.scene.getWindow());
    }

    public static File chooseFile() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle(Translator.translate("window.other.choose-file"));
        fileChooser.setInitialDirectory(CurrentDirectory.get());
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(Main.scene.getWindow());
    }
}