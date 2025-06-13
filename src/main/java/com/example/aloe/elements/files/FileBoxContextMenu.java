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

/**
 * Represents the context menu for a file or directory within the file box UI component.
 * <p>
 * This class extends {@link ExtendedContextMenu} and provides a set of common file-related operations
 * such as open, cut, copy, rename, duplicate, move, delete, compress, extract, and view properties.
 * Additional options are conditionally added based on file type and application settings.
 * </p>
 *
 * <p>
 * The following context menu actions are included:
 * <ul>
 *     <li><strong>Open</strong> - Opens the selected file or directory.</li>
 *     <li><strong>Cut / Copy</strong> - Copies or cuts the file to the system clipboard.</li>
 *     <li><strong>Copy Location</strong> - (Optional) Copies the full file path to the clipboard.</li>
 *     <li><strong>Rename</strong> - Opens a window to rename the file.</li>
 *     <li><strong>Duplicate</strong> - Creates a duplicate of the file.</li>
 *     <li><strong>Move To</strong> - Prompts the user to choose a target directory to move the file.</li>
 *     <li><strong>Move To Parent</strong> - Moves the file one directory level up.</li>
 *     <li><strong>Move To Trash</strong> - Moves the file to the application's designated trash folder.</li>
 *     <li><strong>Compress / Extract</strong> - Compresses files or extracts contents if the file is an archive.</li>
 *     <li><strong>Delete</strong> - Permanently deletes the file.</li>
 *     <li><strong>Properties</strong> - Opens a properties window displaying file metadata.</li>
 *     <li><strong>Add to Menu</strong> - (Directories only) Adds the directory as a shortcut in the application menu.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Additional items are conditionally added based on:
 * <ul>
 *     <li>{@code files.use-copy-location} setting - Enables "Copy Location" option.</li>
 *     <li>If the selected file is a directory - Enables "Add to Menu" option.</li>
 * </ul>
 * </p>
 *
 * @since 2.5.2
 */
public class FileBoxContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a new {@code FileBoxContextMenu} for the specified file or directory.
     * <p>
     * Initializes the context menu with various file operation options tailored to the given
     * {@link File} instance. Menu items are conditionally added based on the file type (file or directory)
     * and current application settings.
     * </p>
     *
     * @param file the {@link File} object (file or directory) for which the context menu is being created
     */
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
            ExtendedMenuItem addToMenu = new ExtendedMenuItem("context-menu.add-to-menu", e -> Menu.addItem(file.getPath(), file.getName(), "FOLDER_OPEN_O"));
            this.getItems().add(9, addToMenu);
        }
    }
}
