package com.example.aloe.elements.files;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.archive.ArchiveHandler;
import com.example.aloe.files.FileChooser;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileDuplicateTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.elements.menu.Menu;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.window.PropertiesWindow;
import com.example.aloe.window.interior.CompressWindow;
import com.example.aloe.window.interior.RenameWindow;

import java.io.File;
import java.util.List;

public class FileBoxContextMenu extends ExtendedContextMenu {
    public FileBoxContextMenu(File file) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem("context-menu.open", e -> FilesOpener.open(file));
        ExtendedMenuItem cut = new ExtendedMenuItem("context-menu.cut", e -> ClipboardManager.cutFilesToClipboard(List.of(file)));
        ExtendedMenuItem copy = new ExtendedMenuItem("context-menu.copy", e -> ClipboardManager.copyFilesToClipboard(List.of(file)));
        ExtendedMenuItem rename = new ExtendedMenuItem("context-menu.rename", e -> new RenameWindow(file));
        ExtendedMenuItem duplicate = new ExtendedMenuItem("context-menu.duplicate", e -> new FileDuplicateTask(file, true));
        ExtendedMenuItem moveTo = new ExtendedMenuItem("context-menu.move-to", e -> new FileMoveTask(file, FileChooser.chooseDirectory(), true));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem("context-menu.move-to-parent", e -> new FileMoveTask(file, file.getParentFile().getParentFile(), true));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem("context-menu.move-to-trash", e -> new FileMoveTask(file, new File(Settings.getSetting("files", "trash").toString()), true));
        ExtendedMenuItem archive = FilesUtils.isFileArchive(file) ?
                new ExtendedMenuItem("context-menu.extract",e -> { ArchiveHandler.extract(file); FilesLoader.refresh(); }) :
                new ExtendedMenuItem("context-menu.compress", e -> new CompressWindow(List.of(file)));
        ExtendedMenuItem delete = new ExtendedMenuItem("context-menu.delete", e -> new FileDeleteTask(file, true));
        ExtendedMenuItem properties = new ExtendedMenuItem("context-menu.properties", e -> new PropertiesWindow(file));

        this.getItems().addAll(open, cut, copy, rename, duplicate, moveTo, moveToParent, moveToTrash, archive, delete, properties);

        if (Boolean.TRUE.equals(Settings.getSetting("files", "use-copy-location"))) {
            ExtendedMenuItem copyLocation = new ExtendedMenuItem("context-menu.copy-location", e -> ClipboardManager.copyTextToClipboard(file.getPath()));
            this.getItems().add(3, copyLocation);
        }

        if (file.isDirectory()) {
            ExtendedMenuItem addToMenu = new ExtendedMenuItem("context-menu.add-to-menu", e -> Menu.addItemToMenu(file.getPath(), file.getName(), "FOLDER_OPEN_O"));
            this.getItems().add(9, addToMenu);
        }
    }
}