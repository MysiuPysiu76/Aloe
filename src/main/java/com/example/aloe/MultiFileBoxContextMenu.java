package com.example.aloe;

import com.example.aloe.files.FileChooser;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileDuplicateTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.settings.SettingsManager;
import com.example.aloe.window.interior.CompressWindow;

import java.io.File;
import java.util.List;

class MultiFileBoxContextMenu extends ExtendedContextMenu {
    public MultiFileBoxContextMenu(List<File> files) {
        super();

        ExtendedMenuItem copy = new ExtendedMenuItem("context-menu.copy", e -> ClipboardManager.copyFilesToClipboard(files));
        ExtendedMenuItem cut = new ExtendedMenuItem("context-menu.cut", e -> ClipboardManager.cutFilesToClipboard(files));
        ExtendedMenuItem duplicate = new ExtendedMenuItem("context-menu.duplicate", e -> new FileDuplicateTask(files, true));
        ExtendedMenuItem moveTo = new ExtendedMenuItem("context-menu.move-to", e -> new FileMoveTask(files, FileChooser.chooseDirectory(), true));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem("context-menu.move-to-parent", e -> new FileMoveTask(files, files.getFirst().getParentFile().getParentFile(), true));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem("context-menu.move-to-trash", e -> new FileMoveTask(files, new File(SettingsManager.getSetting("files", "trash").toString()), true));
        ExtendedMenuItem compress = new ExtendedMenuItem("context-menu.compress", e -> new CompressWindow(files));
        ExtendedMenuItem delete = new ExtendedMenuItem("context-menu.delete", e -> new FileDeleteTask(files, true));

        this.getItems().addAll(copy, cut, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }
}