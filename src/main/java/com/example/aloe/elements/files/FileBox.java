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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileBox extends Pane {

    private static MultiFileBoxContextMenu multiFileBoxContextMenu;

    protected final double scale;
    protected File file;
    protected OSFileStore store;
    private boolean isSelected = false;

    FileBox() {
        this.scale = Settings.getSetting("files", "file-box-size");
        this.getStyleClass().add("file-box");
        this.setOnDragAndDrop();
        this.setOnClick();
    }

    FileBox(File file) {
        this();
        this.file = file;
        this.setFileContextMenu();
    }

    FileBox(OSFileStore store) {
        this();
        this.store = store;
        this.file = new File(store.getMount());
        this.setDiskContextMenu();
    }

    private static InputStream getImageStream(String image) {
        return FileBox.class.getResourceAsStream("/assets/icons/" + image + ".png");
    }

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

    public File getFile() {
        return this.file;
    }

    protected VBox getImageBox(double size, Insets padding) {
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        icon.setImage(FileImage.from(this.file));
        return getvBox(size, padding, icon);
    }

    protected VBox getImageBox(double size, Insets padding, String image) {
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        icon.setImage(new Image(getImageStream(image)));
        return getvBox(size, padding, icon);
    }

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

    protected Label getName() {
        return store != null ? getDiskName() : getFileName();
    }

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

    private Label getFileName() {
        Label label = getNameLabel();
        label.setText(this.file.getName());
        label.setTooltip(new Tooltip(this.file.getName()));
        return label;
    }

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

    protected Label getName(String text) {
        Label label = getName();
        label.setText(text);
        return label;
    }

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

    private void setDiskContextMenu() {
        DiskContextMenu diskContextMenu = new DiskContextMenu(this.store);
        this.setOnContextMenuRequested(e -> {
            FilesPane.hideMenu();
            diskContextMenu.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        });
    }

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

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            SelectedFileBoxes.add(this);
        } else {
            SelectedFileBoxes.remove(this);
        }
    }

    private void setSelectedStyle() {
        this.getStyleClass().add("selected");
    }

    public void removeSelectedStyle() {
        this.getStyleClass().remove("selected");
    }

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
