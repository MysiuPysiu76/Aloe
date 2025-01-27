package com.example.aloe;

import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertiesWindow extends Stage {

    public PropertiesWindow(File file) {
        this.setMinHeight(380);
        this.setMinWidth(330);

        VBox root = new VBox();
        ImageView icon = getIcon(file, false);
        icon.setFitHeight(75);
        icon.setFitWidth(75);
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(30, 10, 10, 2));

        List<String> names = getFilePropertiesNames();
        List<String> values = getFilePropertiesValues(file);

        if (file.isFile()) {
            this.setTitle(Translator.translate("window.properties.file-properties"));
            try {
                values.add(2, new Tika().detect(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.setTitle(Translator.translate("window.properties.folder-properties"));
            values.add(2, Translator.translate("window.properties.folder"));
            names.add(5, Translator.translate("window.properties.folder-contents"));
            try {
                values.add(5, Files.list(Path.of(file.getPath())).count() + Translator.translate("window.properties.items"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        GridPane fileData = new GridPane();
        VBox.setMargin(fileData, new Insets(20, 0, 0, 0));

        for (int i = 0; i < names.size(); i++) {
            Label name = new Label(names.get(i));
            name.setAlignment(Pos.CENTER_RIGHT);
            name.setPadding(new Insets(4, 10, 4, 0));
            name.getStyleClass().add("name");
            name.setMinWidth(110);
            name.setMaxWidth(110);
            Label value = new Label(values.get(i));
            fileData.add(name, 0, i);
            fileData.add(value, 1, i);
        }
        root.getChildren().addAll(iconWrapper, fileData);
        Scene scene = new Scene(root, 330, 390);
        this.setScene(scene);
        this.showAndWait();
    }

    private ArrayList<String> getFilePropertiesNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.file-name"));
        names.add(Translator.translate("window.properties.file-path"));
        names.add(Translator.translate("window.properties.file-type"));
        names.add(Translator.translate("window.properties.file-size"));
        names.add(Translator.translate("window.properties.file-parent"));
        names.add(Translator.translate("window.properties.file-created"));
        names.add(Translator.translate("window.properties.file-modified"));
        names.add(Translator.translate("window.properties.free-space"));
        return names;
    }

    private List<String> getFilePropertiesValues(File file) {
        List<String> values = new ArrayList<>();
        values.add(file.getName());
        values.add(file.getPath());
        values.add(getFileSize(file));
        values.add(file.getParent());
        values.add(getCreationTime(file));
        values.add(getModifiedTime(file));
        values.add(Utils.convertBytesByUnit(file.getFreeSpace()));
        return values;
    }

    private String getFileSize(File file) {
        if (file.isDirectory()) {
            long directorySize = FilesOperations.calculateDirectorySize(file);
            return Utils.convertBytesByUnit(directorySize) + " (" + directorySize + Translator.translate("units.bytes") + ")";
        } else {
            return Utils.convertBytesByUnit(file.length()) + " (" + file.length() + Translator.translate("units.bytes") + ")";
        }
    }

    private String getCreationTime(File file) {
        try {
            FileTime creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();
            return OffsetDateTime.parse(creationTime.toString()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getModifiedTime(File file) {
        LocalDateTime modifiedDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(file.lastModified()),
                ZoneId.systemDefault()
        );
        return modifiedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    private ImageView getIcon(File file, boolean useThumbnails) {
        ImageView icon = new ImageView();
        if (file.isDirectory()) {
            icon.setImage(loadIcon("/assets/icons/folder.png"));
        } else {
            icon.setImage(loadIconForFile(file, useThumbnails));
        }
        return icon;
    }

    private Image loadIcon(String path) {
        return new Image(Objects.requireNonNull(PropertiesWindow.class.getResourceAsStream(path)));
    }

    private Image loadIconForFile(File file, boolean useThumbnails) {
        return switch (FilesOperations.getExtension(file).toLowerCase()) {
            case "jpg", "jpeg", "png", "gif" ->
                    useThumbnails && Boolean.TRUE.equals(SettingsManager.getSetting("files", "display-thumbnails")) ? new Image(new File(FilesOperations.getCurrentDirectory(), file.getName()).toURI().toString()) : loadIcon("/assets/icons/image.png");
            case "mp4" -> loadIcon("/assets/icons/video.png");
            case "mp3", "ogg" -> loadIcon("/assets/icons/music.png");
            case "iso" -> loadIcon("/assets/icons/cd.png");
            default -> loadIcon("/assets/icons/file.png");
        };
    }
}