package com.example.aloe.elements.files;

import com.example.aloe.Main;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.Settings;
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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FileBox extends Pane {

    protected final File file;
    protected final double scale;
    private boolean isSelected = false;

    private static MultiFileBoxContextMenu multiFileBoxContextMenu;
    private static final Set<FileBox> selectedFileBoxes = new LinkedHashSet<>();

     FileBox(File file) {
        this.file = file;
        this.scale = Settings.getSetting("files", "file-box-size");

        this.getStyleClass().add("file-box");
        this.setContextMenu();
        this.setOnDragAndDrop();
        this.setOnClick();
    }

    public static Image getImage(File file) {
        if (file.isDirectory()) {
            return new Image(getImageStream("folder"));
        } else {
            switch (FilesUtils.getExtension(file.getName()).toLowerCase()) {
                case "jpg", "jpeg", "png", "gif" -> {
                    if (Boolean.TRUE.equals(Settings.getSetting("files", "display-thumbnails"))) { return new Image(new File(CurrentDirectory.get(), file.getName()).toURI().toString()); }
                    else { return new Image(getImageStream("image")); } }
                case "webp", "heif", "raw" -> { return new Image(getImageStream("image")); }
                case "mp4", "mkv", "ts" -> { return new Image(getImageStream("video")); }
                case "mp3", "ogg" -> { return new Image(getImageStream("music")); }
                case "epub", "mobi" -> { return new Image(getImageStream("book")); }
                case "pdf" -> { return new Image(getImageStream("pdf")); }
                case "exe", "msi", "deb", "rpm", "snap", "flatpak", "flatpakref", "dmg", "apk" -> { return new Image(getImageStream("installer")); }
                case "torrent" -> { return new Image(getImageStream("torrent")); }
                case "tar", "tar.gz" -> { return new Image(getImageStream("tar")); }
                case "zip", "7z" -> { return new Image(getImageStream("zip")); }
                case "rar" -> { return new Image(getImageStream("rar")); }
                case "sh", "bat" -> { return new Image(getImageStream("terminal")); }
                case "jar" -> { return new Image(getImageStream("jar")); }
                case "iso" -> { return new Image(getImageStream("cd")); }
                default -> { return new Image(getImageStream("file")); }
            }
        }
    }

    private static InputStream getImageStream(String image) {
        return FileBox.class.getResourceAsStream("/assets/icons/" + image + ".png");
    }

    public static void removeSelection() {
        selectedFileBoxes.forEach(FileBox::removeSelectedStyle);
        selectedFileBoxes.clear();
    }

    public static List<File> getSelectedFiles() {
        return selectedFileBoxes.stream().map(FileBox::getFile).toList();
    }

    public static void selectAllFiles() {
        Stream stream;
         if (Settings.getSetting("files", "view").equals("list")) {
             VBox list = (VBox) Main.filesPane.getContent();
             stream = list.getChildren().stream();
         } else {
             FlowPane grid = (FlowPane) Main.filesPane.getContent();
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
        icon.setImage(getImage(this.file));
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
        Label label = new Label(this.file.getName());
        label.setWrapText(true);
        label.setMaxWidth(90 * scale);
        label.setAlignment(Pos.TOP_CENTER);
        label.setTooltip(new Tooltip(this.file.getName()));
        label.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");
        return label;
    }

    private void setContextMenu() {
        FileBoxContextMenu fileBoxContextMenu = new FileBoxContextMenu(this.file);
        this.setOnContextMenuRequested(e -> {
            if (this.isSelected() && selectedFileBoxes.size() == 1) {
                fileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else if (this.isSelected()) {
                multiFileBoxContextMenu = new MultiFileBoxContextMenu(getSelectedFiles());
                multiFileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                FileBox.removeSelection();
                this.setSelected();
                fileBoxContextMenu.show(this, e.getScreenX(), e.getScreenY());
                Main.directoryMenu.hide();
            }
            e.consume();
        });
    }

    private void setOnClick() {
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1 && !e.isControlDown()) {
                FileBox.removeSelection();
                this.setSelected();
            } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                FilesOpener.open(this.getFile());
            } else if (e.isControlDown()) {
                this.setSelected();
            } else {
                FileBox.removeSelection();
            }
            e.consume();
        });
    }

    public void setSelected() {
        if (this.isSelected()) {
            selectedFileBoxes.remove(this);
            this.removeSelectedStyle();
        } else {
            selectedFileBoxes.add(this);
            this.setSelectedStyle();
        }
        this.isSelected = !isSelected;
    }

    private void setSelectedStyle() {
        this.getStyleClass().add("selected");
    }

    public void removeSelectedStyle() {
        this.getStyleClass().remove("selected");
    }

    public boolean isSelected() {
        return selectedFileBoxes.contains(this);
    }

    private void setOnDragAndDrop() {
        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            List<String> fileNamesToDrag = new ArrayList<>();
            if (selectedFileBoxes.contains(this)) {
                for (Pane selectedFile : selectedFileBoxes) {
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