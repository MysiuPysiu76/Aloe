package com.example.aloe;

import com.example.aloe.files.FileChooser;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileDuplicateTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.window.interior.CompressWindow;

import java.io.File;
import java.util.List;

class MultiFileBoxContextMenu extends ExtendedContextMenu {
    public MultiFileBoxContextMenu(List<File> files) {
        super();

        ExtendedMenuItem copy = new ExtendedMenuItem(Translator.translate("context-menu.copy"), e -> ClipboardManager.copyFilesToClipboard(files));
        ExtendedMenuItem cut = new ExtendedMenuItem(Translator.translate("context-menu.cut"), e -> ClipboardManager.cutFilesToClipboard(files));
        ExtendedMenuItem duplicate = new ExtendedMenuItem(Translator.translate("context-menu.duplicate"), e -> new FileDuplicateTask(files, true));
        ExtendedMenuItem moveTo = new ExtendedMenuItem(Translator.translate("context-menu.move-to"), e -> new FileMoveTask(files, FileChooser.chooseDirectory(), true));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem(Translator.translate("context-menu.move-to-parent"), e -> new FileMoveTask(files, files.getFirst().getParentFile().getParentFile(), true));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem(Translator.translate("context-menu.move-to-trash"), e -> new FileMoveTask(files, new File(System.getProperty("user.home"), ".trash"), true));
        ExtendedMenuItem compress = new ExtendedMenuItem(Translator.translate("context-menu.compress"), e -> new CompressWindow(files));
        ExtendedMenuItem delete = new ExtendedMenuItem(Translator.translate("context-menu.delete"), e -> new FileDeleteTask(files, true));

        this.getItems().addAll(copy, cut, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }
}