package com.example.aloe.elements.files;

import com.example.aloe.files.CurrentDirectory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.io.File;

/**
 * {@code FilesPane} is a singleton class representing the main scrollable container
 * used to display the contents of the current directory in the application's file explorer.
 * It manages context menu behavior, visual styling, and scroll positioning.
 *
 * <p>This pane dynamically updates its content via {@link #set(Node)} and handles
 * context menu interactions based on the currently active directory. It also clears
 * file selections on interaction.</p>
 *
 * <p>Only one instance of this pane exists at a time and it can be accessed via {@link #get()}.</p>
 *
 * @since 2.7.7
 */
public class FilesPane extends ScrollPane {

    /** The singleton instance of FilesPane. */
    private static FilesPane instance;

    /** The context menu associated with directory actions. */
    private static final DirectoryContextMenu contextMenu = new DirectoryContextMenu();

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the pane's layout, style, and input handlers.
     */
    private FilesPane() {
        configurePane();
    }

    /**
     * Initializes visual properties, event handlers, and context menu logic.
     */
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

    /**
     * Checks whether the currently selected directory is the virtual "%disks%" view.
     *
     * @return {@code true} if the current directory is "%disks%", {@code false} otherwise.
     */
    private boolean isDisksView() {
        return CurrentDirectory.get().equals(new File("%disks%"));
    }

    /**
     * Returns the singleton instance of {@code FilesPane}, creating it if necessary.
     *
     * @return the single {@code FilesPane} instance.
     */
    public static FilesPane get() {
        if (instance == null) {
            instance = new FilesPane();
        }
        return instance;
    }

    /**
     * Replaces the current content displayed inside the pane.
     *
     * @param content the new Node to be displayed.
     */
    public static void set(Node content) {
        get().setContent(content);
    }

    /**
     * Hides the context menu, if currently visible.
     */
    public static void hideMenu() {
        contextMenu.hide();
    }

    /**
     * Resets the vertical scroll position of the pane to the top.
     */
    public static void resetPosition() {
        get().setVvalue(0);
    }
}
