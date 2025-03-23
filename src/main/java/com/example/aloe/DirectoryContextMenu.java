package com.example.aloe;

import com.example.aloe.window.PropertiesWindow;
import com.example.aloe.window.interior.DirectoryWindow;
import com.example.aloe.window.interior.FileWindow;

class DirectoryContextMenu extends ExtendedContextMenu {
    public DirectoryContextMenu() {
        super();

        ExtendedMenuItem newDirectory = new ExtendedMenuItem(Translator.translate("context-menu.new-folder"), e -> new DirectoryWindow());
        ExtendedMenuItem newFile = new ExtendedMenuItem(Translator.translate("context-menu.new-file"), e -> new FileWindow());
        ExtendedMenuItem paste = new ExtendedMenuItem(Translator.translate("context-menu.paste"), e -> FilesOperations.pasteFilesFromClipboard());
        ExtendedMenuItem selectAll = new ExtendedMenuItem(Translator.translate("context-menu.select-all"), e -> new Main().selectAllFiles());
        ExtendedMenuItem properties = new ExtendedMenuItem(Translator.translate("context-menu.properties"), e -> new PropertiesWindow(FilesOperations.getCurrentDirectory()));

        this.getItems().addAll(newDirectory, newFile, paste, selectAll, properties);
    }
}