package com.example.aloe.elements.files;

import com.example.aloe.files.CurrentDirectory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.io.File;

public class FilesPane extends ScrollPane {

    private static FilesPane instance;
    private static final DirectoryContextMenu contextMenu = new DirectoryContextMenu();

    private FilesPane() {
        configurePane();
    }

    private void configurePane() {
        setFitToWidth(true);
        setPadding(new Insets(7, 7, 17, 7));
        getStyleClass().add("files-pane");

        setOnContextMenuRequested(event -> {
            if (!isDisksView()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
            SelectedFileBoxes.removeSelection();
        });

        setOnMouseClicked(event -> {
            hideMenu();
            SelectedFileBoxes.removeSelection();
        });
    }

    private boolean isDisksView() {
        return CurrentDirectory.get().equals(new File("%disks%"));
    }

    public static FilesPane get() {
        if (instance == null) {
            instance = new FilesPane();
        }
        return instance;
    }

    public static void set(Node content) {
        get().setContent(content);
    }

    public static void hideMenu() {
        contextMenu.hide();
    }

    public static void resetPosition() {
        get().setVvalue(0);
    }
}
