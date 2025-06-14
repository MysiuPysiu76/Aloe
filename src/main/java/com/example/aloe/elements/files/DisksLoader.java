package com.example.aloe.elements.files;

import javafx.event.Event;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.List;

class DisksLoader {

    static void load() {
        VBox content = createContentContainer();
        List<OSFileStore> fileStores = getFileStores();

        for (OSFileStore store : fileStores) {
            content.getChildren().add(new HorizontalFileBox(store));
        }

        FilesPane.set(content);
    }

    private static VBox createContentContainer() {
        VBox box = new VBox(3);
        box.setOnContextMenuRequested(Event::consume);
        return box;
    }

    private static List<OSFileStore> getFileStores() {
        FileSystem fs = new SystemInfo().getOperatingSystem().getFileSystem();
        return fs.getFileStores();
    }
}
