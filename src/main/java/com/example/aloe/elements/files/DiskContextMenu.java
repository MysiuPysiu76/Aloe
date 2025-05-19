package com.example.aloe.elements.files;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.components.ExtendedMenuItem;
import com.example.aloe.elements.menu.Menu;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.CurrentPlatform;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.PropertiesWindow;
import oshi.software.os.OSFileStore;

import java.io.File;

public class DiskContextMenu extends ExtendedContextMenu {

    public DiskContextMenu(OSFileStore store) {
        super();

        ExtendedMenuItem open = new ExtendedMenuItem(Translator.translate("context-menu.open"), e -> FilesOpener.open(new File(store.getMount())));
        ExtendedMenuItem copyLocation = new ExtendedMenuItem("context-menu.copy-location", e -> ClipboardManager.copyTextToClipboard(store.getMount()));
        ExtendedMenuItem addToMenu = new ExtendedMenuItem("context-menu.add-to-menu", e -> Menu.addItemToMenu(store.getMount(), store.getName().equals("/") && CurrentPlatform.isLinux() ? "Linux" : store.getName(), "HDD_O"));
        ExtendedMenuItem properties = new ExtendedMenuItem("context-menu.properties", e -> new PropertiesWindow(store));

        this.getItems().addAll(open, copyLocation, addToMenu, properties);
    }
}
