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

/**
 * A context menu for use within a directory view in the Aloe application.
 * <p>
 * This menu provides typical actions available when right-clicking inside a folder,
 * such as creating new files or folders, pasting copied items, selecting all files,
 * and viewing directory properties.
 * </p>
 *
 * <p>
 * Menu Items:
 * <ul>
 *     <li><b>New Folder:</b> Opens a window to create a new folder</li>
 *     <li><b>New File:</b> Opens a window to create a new file</li>
 *     <li><b>Paste:</b> Pastes files from the clipboard into the current directory</li>
 *     <li><b>Select All:</b> Selects all visible files in the current view</li>
 *     <li><b>Properties:</b> Opens a properties window for the current directory</li>
 * </ul>
 * </p>
 *
 * @since 2.6.9
 */
public class DirectoryContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a context menu with options relevant to directory views.
     */
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
