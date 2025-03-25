package com.example.aloe;

import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileDuplicateTask;

import java.io.File;
import java.util.List;

class MultiFileBoxContextMenu extends ExtendedContextMenu {
    public MultiFileBoxContextMenu(List<File> files) {
        super();

        ExtendedMenuItem copy = new ExtendedMenuItem(Translator.translate("context-menu.copy"), e -> new Main().copySelectedFiles());
        ExtendedMenuItem cut = new ExtendedMenuItem(Translator.translate("context-menu.cut"), e -> new Main().cutSelectedFiles());
        ExtendedMenuItem duplicate = new ExtendedMenuItem(Translator.translate("context-menu.duplicate"), e -> new FileDuplicateTask(files, true));
        ExtendedMenuItem moveTo = new ExtendedMenuItem(Translator.translate("context-menu.move-to"), e -> FilesOperations.moveFileTo(files));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem(Translator.translate("context-menu.move-to-parent"), e -> FilesOperations.moveFileToParent(files));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem(Translator.translate("context-menu.move-to-trash"), e -> FilesOperations.moveFileToTrash(files));
        ExtendedMenuItem compress = new ExtendedMenuItem(Translator.translate("context-menu.compress"), e -> new Main().openCreateArchiveWindow(files));
        ExtendedMenuItem delete = new ExtendedMenuItem(Translator.translate("context-menu.delete"), e -> new FileDeleteTask(files, true));

        this.getItems().addAll(copy, cut, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }
}