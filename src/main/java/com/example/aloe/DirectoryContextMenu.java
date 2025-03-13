package com.example.aloe;

class DirectoryContextMenu extends ExtendedContextMenu {
    public DirectoryContextMenu() {
        super();

        ExtendedMenuItem newDirectory = new ExtendedMenuItem(Translator.translate("context-menu.new-folder"), e -> Main.createDirectory());
        ExtendedMenuItem newFile = new ExtendedMenuItem(Translator.translate("context-menu.new-file"), e -> Main.createFile());
        ExtendedMenuItem paste = new ExtendedMenuItem(Translator.translate("context-menu.paste"), e -> FilesOperations.pasteFilesFromClipboard());
        ExtendedMenuItem selectAll = new ExtendedMenuItem(Translator.translate("context-menu.select-all"), e -> new Main().selectAllFiles());
        ExtendedMenuItem properties = new ExtendedMenuItem(Translator.translate("context-menu.properties"), e -> new PropertiesWindow(FilesOperations.getCurrentDirectory()));

        this.getItems().addAll(newDirectory, newFile, paste, selectAll, properties);
    }
}