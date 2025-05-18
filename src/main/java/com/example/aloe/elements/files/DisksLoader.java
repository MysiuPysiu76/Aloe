package com.example.aloe.elements.files;

import javafx.event.Event;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.List;

class DisksLoader {

    static void loadDisks() {
        VBox content = new VBox();
        content.setSpacing(3);
        content.setOnContextMenuRequested(Event::consume);

        FileSystem fs = new SystemInfo().getOperatingSystem().getFileSystem();
        List<OSFileStore> fileStores = fs.getFileStores();

        for (OSFileStore store : fileStores) {
            content.getChildren().add(new HorizontalFileBox(store));
        }

        FilesPane.set(content);
    }
}