package com.example.aloe.elements.files;

import javafx.event.Event;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.List;

/**
 * The {@code DisksLoader} class is responsible for loading and displaying
 * all available disk drives (file stores) on the current operating system.
 * <p>
 * It utilizes the OSHI library to retrieve system file stores and displays
 * each as a horizontal file box within the Aloe file explorer interface.
 * </p>
 * <p>
 * This class is used to provide a visual representation of disks when
 * the user selects the special "%disks%" directory view.
 * </p>
 *
 * @since 2.7.8
 */
class DisksLoader {

    /**
     * Loads all available disk drives and displays them inside the files pane.
     * Each disk is represented as a {@link HorizontalFileBox} component.
     */
    static void load() {
        VBox content = createContentContainer();
        List<OSFileStore> fileStores = getFileStores();

        for (OSFileStore store : fileStores) {
            content.getChildren().add(new HorizontalFileBox(store));
        }

        FilesPane.set(content);
    }

    /**
     * Creates and configures a vertical container (VBox) for holding the disk UI components.
     * It consumes context menu events to prevent the default context menu from showing.
     *
     * @return a configured VBox container for disks display
     */
    private static VBox createContentContainer() {
        VBox box = new VBox(3);
        box.setOnContextMenuRequested(Event::consume);
        return box;
    }

    /**
     * Retrieves the list of file stores (disks/partitions) available on the current operating system
     * using the OSHI (Operating System and Hardware Information) library.
     *
     * @return a list of {@link OSFileStore} representing the system's file stores
     */
    private static List<OSFileStore> getFileStores() {
        FileSystem fs = new SystemInfo().getOperatingSystem().getFileSystem();
        return fs.getFileStores();
    }
}
