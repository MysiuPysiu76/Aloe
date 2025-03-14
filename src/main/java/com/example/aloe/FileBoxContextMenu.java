package com.example.aloe;

import com.example.aloe.archive.ArchiveHandler;
import com.example.aloe.menu.MenuManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileBoxContextMenu extends ExtendedContextMenu {
    public FileBoxContextMenu(File file) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem(Translator.translate("context-menu.open"), e -> Main.openFileInOptions(file));
        ExtendedMenuItem cut = new ExtendedMenuItem(Translator.translate("context-menu.cut"), e -> FilesOperations.cutFile(file));
        ExtendedMenuItem copy = new ExtendedMenuItem(Translator.translate("context-menu.copy"), e -> FilesOperations.copyFile(file));
        ExtendedMenuItem rename = new ExtendedMenuItem(Translator.translate("context-menu.rename"), e -> Main.renameFile(file));
        ExtendedMenuItem duplicate = new ExtendedMenuItem(Translator.translate("context-menu.duplicate"), e -> FilesOperations.duplicateFiles(new ArrayList<>(List.of(file))));
        ExtendedMenuItem moveTo = new ExtendedMenuItem(Translator.translate("context-menu.move-to"), e -> FilesOperations.moveFileTo(new ArrayList<>(List.of(file))));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem(Translator.translate("context-menu.move-to-parent"), e -> FilesOperations.moveFileToParent(file));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem(Translator.translate("context-menu.move-to-trash"), e -> FilesOperations.moveFileToTrash(file));
        ExtendedMenuItem archive = Utils.isFileArchive(file) ?
                new ExtendedMenuItem(Translator.translate("context-menu.extract"),e -> { ArchiveHandler.extract(file); new Main().refreshCurrentDirectory(); }) :
                new ExtendedMenuItem(Translator.translate("context-menu.compress"), e -> { Main.openCreateArchiveWindow(new ArrayList<>(List.of(file))); new Main().refreshCurrentDirectory(); });
        ExtendedMenuItem delete = new ExtendedMenuItem(Translator.translate("context-menu.delete"), e -> FilesOperations.deleteFile(file));
        ExtendedMenuItem properties =   new ExtendedMenuItem(Translator.translate("context-menu.properties"), e -> new PropertiesWindow(file));

        this.getItems().addAll(open, cut, copy, rename, duplicate, moveTo, moveToParent, moveToTrash, archive, delete, properties);

        if (file.isDirectory()) {
            ExtendedMenuItem addToMenu = new ExtendedMenuItem(Translator.translate("context-menu.add-to-menu"), e -> MenuManager.addItemToMenu(file.getPath(), file.getName(), "FOLDER_OPEN_O"));
            this.getItems().add(9, addToMenu);
        }
    }
}