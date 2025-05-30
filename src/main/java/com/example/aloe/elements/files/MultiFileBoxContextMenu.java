package com.example.aloe.elements.files;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.files.FileChooser;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileDuplicateTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.window.interior.CompressWindow;

import java.io.File;
import java.util.List;

/**
 * A context menu designed for operations on multiple selected files within the Aloe application.
 * <p>
 * This menu appears when the user selects multiple files and provides batch file operation options
 * such as copying, cutting, duplicating, compressing, deleting, or moving the files.
 * </p>
 *
 * @since 2.7.0
 */
public class MultiFileBoxContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a context menu with batch operations for a list of selected files.
     *
     * @param files The list of files the user has selected.
     */
    public MultiFileBoxContextMenu(List<File> files) {
        super();

        ExtendedMenuItem copy = new ExtendedMenuItem("context-menu.copy", e -> ClipboardManager.copyFilesToClipboard(files));
        ExtendedMenuItem cut = new ExtendedMenuItem("context-menu.cut", e -> ClipboardManager.cutFilesToClipboard(files));
        ExtendedMenuItem duplicate = new ExtendedMenuItem("context-menu.duplicate", e -> new FileDuplicateTask(files, true));
        ExtendedMenuItem moveTo = new ExtendedMenuItem("context-menu.move-to", e -> new FileMoveTask(files, FileChooser.chooseDirectory(), true));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem("context-menu.move-to-parent", e -> new FileMoveTask(files, files.getFirst().getParentFile().getParentFile(), true));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem("context-menu.move-to-trash", e -> new FileMoveTask(files, new File(Settings.getSetting("files", "trash").toString()), true));
        ExtendedMenuItem compress = new ExtendedMenuItem("context-menu.compress", e -> new CompressWindow(files));
        ExtendedMenuItem delete = new ExtendedMenuItem("context-menu.delete", e -> new FileDeleteTask(files, true));

        this.getItems().addAll(copy, cut, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }
}