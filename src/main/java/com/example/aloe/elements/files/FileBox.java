package com.example.aloe.elements.files;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.CurrentPlatform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import oshi.software.os.OSFileStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a visual component for a file or disk in the file manager interface.
 * <p>
 * A {@code FileBox} can display file or disk information with an icon and label,
 * support selection, context menus, drag-and-drop operations, and respond to user clicks.
 * </p>
 *
 * <p>This class supports:
 * <ul>
 *     <li>Display of both file system files and mounted disks</li>
 *     <li>Context menus based on selection and file type</li>
 *     <li>Single or multiple selection</li>
 *     <li>Drag-and-drop operations for file moving</li>
 *     <li>Custom styling and scaling according to user settings</li>
 * </ul>
 * </p>
 *
 * @since 2.8.4
 */
public class FileBox extends Pane {

    /**
     * Context menu for multiple selected files.
     */
    private static MultiFileBoxContextMenu multiFileBoxContextMenu;

    /**
     * Scaling factor from user settings to resize file boxes.
     */
    protected final double scale;

    /**
     * The file or directory represented by this FileBox.
     */
    protected File file;

    /**
     * Disk store object, used when displaying mounted disks.
     */
    protected OSFileStore store;

    /**
     * Indicates whether this file box is currently selected.
     */
    private boolean isSelected = false;

    /**
     * Constructs a base FileBox and applies styling and mouse listeners.
     */
    FileBox() {
        this.scale = Settings.getSetting("files", "file-box-size");
        this.getStyleClass().add("file-box");
        this.setOnDragAndDrop();
        this.setOnClick();
    }

    /**
     * Constructs a FileBox for a given file.
     *
     * @param file the file or directory this FileBox represents
     */
    FileBox(File file) {
        this();
        this.file = file;
        this.setFileContextMenu();
    }

    /**
     * Constructs a FileBox for a mounted disk volume.
     *
     * @param store the OSFileStore representing the disk
     */
    FileBox(OSFileStore store) {
        this();
        this.store = store;
        this.file = new File(store.getMount());
        this.setDiskContextMenu();
    }

    /**
     * Selects all FileBox elements in the current view pane.
     */
    public static void selectAllFiles() {
        SelectedFileBoxes.removeSelection();
        Stream stream;
         if (Settings.getSetting("files", "view").equals("list")) {
             VBox list = (VBox) FilesPane.get().getContent();
             stream = list.getChildren().stream();
         } else {
             FlowPane grid = (FlowPane) FilesPane.get().getContent();
             stream = grid.getChildren().stream();
         }
         stream.filter(node -> node instanceof FileBox).forEach(node -> ((FileBox) node).setSelected());
    }

    /**
     * Returns the file associated with this FileBox.
     *
     * @return the file object
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Builds a vertical container with an image icon.
     *
     * @param size    size of the icon
     * @param padding padding around the icon
     * @return VBox containing the icon
     */
    protected VBox getImageBox(double size, Insets padding) {
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        icon.setImage(FileImage.from(this.file));
        return getvBox(size, padding, icon);
    }

    /**
     * Builds a vertical container with a custom image icon.
     *
     * @param size    size of the icon
     * @param padding padding around the icon
     * @param image   image name for icon
     * @return VBox containing the icon
     */
    protected VBox getImageBox(double size, Insets padding, String image) {
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        icon.setImage(new Image(FileImage.getImageStream(image)));
        return getvBox(size, padding, icon);
    }

    /**
     * Helper to generate a VBox containing an icon with proper scaling.
     *
     * @param size  icon size
     * @param padding padding for layout
     * @param icon  the ImageView to wrap
     * @return configured VBox
     */
    @NotNull
    private VBox getvBox(double size, Insets padding, ImageView icon) {
        icon.setFitHeight(size * scale);
        icon.setFitWidth(size * scale);
        VBox.setMargin(icon, padding);
        HBox.setMargin(icon, padding);

        VBox box = new VBox(icon);
        box.setAlignment(Pos.BOTTOM_CENTER);
        box.setMinHeight(size * scale);
        return box;
    }

    /**
     * Returns the name label for this FileBox, using either disk or file name.
     *
     * @return label with file or disk name
     */
    protected Label getName() {
        return store != null ? getDiskName() : getFileName();
    }

    /**
     * Generates a label for a disk name, applying system-specific naming.
     *
     * @return label representing the disk
     */
    private Label getDiskName() {
        Label label = getNameLabel();
        String name = new File(store.getMount()).getName();
        boolean isRoot = FilesUtils.isRoot(new File(this.store.getMount()));
        if (isRoot && CurrentPlatform.isLinux()) name = "Linux";
        if (isRoot  && CurrentPlatform.isWindows()) name = "Windows";
        if (isRoot && CurrentPlatform.isMac()) name = "MacOS";
        label.setText(name);
        label.setTooltip(new Tooltip(this.store.getMount()));
        return label;
    }

    /**
     * Generates a label for a file or folder name.
     *
     * @return label representing the file
     */
    private Label getFileName() {
        Label label = getNameLabel();
        label.setText(this.file.getName());
        label.setTooltip(new Tooltip(this.file.getName()));
        return label;
    }

    /**
     * Prepares a reusable label for file or disk names.
     *
     * @return base label styled for display
     */
    private Label getNameLabel() {
        Label label = new Label();
        label.setWrapText(true);
        label.setMaxWidth(90 * scale);
        label.setMaxHeight(35);
        label.setAlignment(Pos.TOP_CENTER);
        label.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");
        label.getStyleClass().add("text");
        return label;
    }

    /**
     * Returns the label for this FileBox with custom override text.
     *
     * @param text custom text to display
     * @return label with overridden text
     */
    protected Label getName(String text) {
        Label label = getName();
        label.setText(text);
        return label;
    }

    /**
     * Sets up the context menu for this file box, depending on selection state.
     */
    private void setFileContextMenu() {
        FileBoxContextMenu fileBoxContextMenu = new FileBoxContextMenu(this.file);
        this.setOnContextMenuRequested(e -> {
            FilesPane.hideMenu();
            if (SelectedFileBoxes.isSelected(this) && SelectedFileBoxes.getSelectedFiles().size() == 1) {
                fileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else if (SelectedFileBoxes.isSelected(this)) {
                multiFileBoxContextMenu = new MultiFileBoxContextMenu(SelectedFileBoxes.getSelectedFiles());
                multiFileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                SelectedFileBoxes.removeSelection();
                this.setSelected();
                fileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
            }
            e.consume();
        });
    }

    /**
     * Sets up a context menu specific to a disk volume.
     */
    private void setDiskContextMenu() {
        DiskContextMenu diskContextMenu = new DiskContextMenu(this.store);
        this.setOnContextMenuRequested(e -> {
            FilesPane.hideMenu();
            diskContextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        });
    }

    /**
     * Defines mouse click behavior for the FileBox (selection, opening, multi-select).
     */
    private void setOnClick() {
        this.setOnMouseClicked(e -> {
            FilesPane.hideMenu();
            int clicks = Settings.getSetting("files", "use-double-click").equals(Boolean.TRUE) ? 2 : 1;
            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.isControlDown()) {
                    this.setSelected();
                } else if (e.getClickCount() == clicks) {
                    FilesOpener.open(this.getFile());
                } else if (e.getClickCount() == 1) {
                    SelectedFileBoxes.removeSelection();
                    this.setSelected();
                } else {
                    SelectedFileBoxes.removeSelection();
                }
            }
            e.consume();
        });
    }

    /**
     * Toggles the selection state of this FileBox.
     * Adds or removes visual styling and updates selection list.
     */
    public void setSelected() {
        if (SelectedFileBoxes.isSelected(this)) {
            SelectedFileBoxes.remove(this);
            this.removeSelectedStyle();
        } else {
            SelectedFileBoxes.add(this);
            this.setSelectedStyle();
        }
        this.isSelected = !isSelected;
    }

    /**
     * Explicitly sets the selection state of this FileBox.
     *
     * @param isSelected whether the file is selected
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            SelectedFileBoxes.add(this);
        } else {
            SelectedFileBoxes.remove(this);
        }
    }

    /**
     * Applies visual styling for a selected file.
     */
    private void setSelectedStyle() {
        this.getStyleClass().add("selected");
    }

    /**
     * Removes the visual selection style from this FileBox.
     */
    public void removeSelectedStyle() {
        this.getStyleClass().remove("selected");
    }

    /**
     * Configures drag-and-drop behavior for moving files between directories.
     */
    private void setOnDragAndDrop() {
        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            List<String> fileNamesToDrag = new ArrayList<>();
            if (SelectedFileBoxes.isSelected(this)) {
                for (Pane selectedFile : SelectedFileBoxes.getSelectedFileBoxes()) {
                    Label fileNameLabel = (Label) selectedFile.getChildren().get(1);
                    fileNamesToDrag.add(fileNameLabel.getText());
                }
            } else {
                fileNamesToDrag.add(this.file.getName());
            }

            content.putString(String.join(",", fileNamesToDrag));
            db.setContent(content);
            event.consume();
        });

        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString() && this.file.isDirectory()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String[] draggedFileNames = db.getString().split(",");
                File targetDirectory = new File(CurrentDirectory.get(), this.file.getName());
                if (targetDirectory.isDirectory()) {
                    for (String draggedFileName : draggedFileNames) {
                        File draggedFile = new File(CurrentDirectory.get(), draggedFileName);
                        if (draggedFile.exists()) {
                            draggedFile.renameTo(new File(targetDirectory, draggedFile.getName()));
                        }
                    }
                    FilesLoader.refresh();
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
