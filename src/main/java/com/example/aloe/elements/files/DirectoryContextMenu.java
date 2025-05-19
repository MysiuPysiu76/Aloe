package com.example.aloe.elements.files;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.tasks.FileCopyTask;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.PropertiesWindow;
import com.example.aloe.window.interior.DirectoryWindow;
import com.example.aloe.window.interior.FileWindow;
import javafx.scene.input.Clipboard;

public class DirectoryContextMenu extends ExtendedContextMenu {

    public DirectoryContextMenu() {
        super();

        ExtendedMenuItem newDirectory = new ExtendedMenuItem(Translator.translate("context-menu.new-folder"), e -> new DirectoryWindow());
        ExtendedMenuItem newFile = new ExtendedMenuItem(Translator.translate("context-menu.new-file"), e -> new FileWindow());
        ExtendedMenuItem paste = new ExtendedMenuItem(Translator.translate("context-menu.paste"), e -> new FileCopyTask(Clipboard.getSystemClipboard().getFiles(), true));
        ExtendedMenuItem selectAll = new ExtendedMenuItem(Translator.translate("context-menu.select-all"), e -> FileBox.selectAllFiles());
        ExtendedMenuItem properties = new ExtendedMenuItem(Translator.translate("context-menu.properties"), e -> new PropertiesWindow(CurrentDirectory.get()));

        this.getItems().addAll(newDirectory, newFile, paste, selectAll, properties);
    }
}