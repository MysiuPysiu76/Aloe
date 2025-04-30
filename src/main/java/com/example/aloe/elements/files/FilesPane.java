package com.example.aloe.elements.files;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class FilesPane extends ScrollPane {

    private static FilesPane filesPane;
    private static DirectoryContextMenu menu = new DirectoryContextMenu();

    private FilesPane() {
        filesPane = this;
        this.setFitToWidth(true);
        this.setPadding(new Insets(7, 7, 17, 7));
        this.setOnContextMenuRequested(e -> {
            menu.show(this, e.getScreenX(), e.getScreenY());
            FileBox.removeSelection();
        });
        this.setOnMouseClicked(e -> {
            FilesPane.hideMenu();
            FileBox.removeSelection();
        });
    }

    public static void set(Node content) {
        filesPane.setContent(content);
    }

    public static FilesPane get() {
        if (filesPane == null) new FilesPane();
        return filesPane;
    }

    public static void hideMenu() {
        menu.hide();
    }

    public static void resetPosition() {
        filesPane.setVvalue(0);
    }
}
