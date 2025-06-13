package com.example.aloe.elements.files;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.elements.menu.Menu;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.CurrentPlatform;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.PropertiesWindow;
import oshi.software.os.OSFileStore;

import java.io.File;

/**
 * Package: com.example.aloe.elements.files
 *
 * This package contains classes related to file system elements within the Aloe application.
 * It provides UI components and logic for interacting with disks, files, and their properties
 * through context menus and other elements.
 */

/**
 * Represents a context menu specifically tailored for disk drives or mounted file systems.
 * <p>
 * This menu provides users with a set of disk-related actions, such as:
 * <ul>
 *     <li>Opening the disk in the system's file manager</li>
 *     <li>Copying the disk's mount location to the clipboard</li>
 *     <li>Adding the disk to a custom menu structure within the application</li>
 *     <li>Viewing disk properties in a dedicated window</li>
 * </ul>
 * </p>
 *
 * <p>
 * The menu dynamically adjusts its behavior based on the current operating system
 * and the disk's mount point or root status.
 * </p>
 *
 * @since 2.6.8
 */
public class DiskContextMenu extends ExtendedContextMenu {

    /**
     * Constructs a context menu for a specific disk (or file store).
     *
     * @param store The file store (disk) for which the context menu is created.
     */
    public DiskContextMenu(OSFileStore store) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem(Translator.translate("context-menu.open"), e -> FilesOpener.open(new File(store.getMount())));
        ExtendedMenuItem copyLocation = new ExtendedMenuItem("context-menu.copy-location", e -> ClipboardManager.copyTextToClipboard(store.getMount()));
        ExtendedMenuItem addToMenu = new ExtendedMenuItem("context-menu.add-to-menu", e -> Menu.addItem(store.getMount(), FilesUtils.isRoot(new File(store.getMount())) && CurrentPlatform.isLinux() ? "Linux" : FilesUtils.isRoot(new File(store.getMount())) && CurrentPlatform.isWindows() ?  "Windows" : FilesUtils.isRoot(new File(store.getMount())) && CurrentPlatform.isMac() ? "MacOS" : store.getName(), "HDD_O"));
        ExtendedMenuItem properties = new ExtendedMenuItem("context-menu.properties", e -> new PropertiesWindow(store));

        this.getItems().addAll(open, copyLocation, addToMenu, properties);
    }
}
