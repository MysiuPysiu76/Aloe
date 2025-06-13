package com.example.aloe.elements.files;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.Settings;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;

public class FileImage {

    public static Image from(File file) {
        if (file.isDirectory()) {
            return new Image(getImageStream("folder"));
        } else {
            switch (FilesUtils.getExtension(file.getName()).toLowerCase()) {
                case "jpg", "jpeg", "png", "gif" -> {
                    if (Boolean.TRUE.equals(Settings.getSetting("files", "display-thumbnails"))) { return new Image(new File(CurrentDirectory.get(), file.getName()).toURI().toString()); }
                    else { return new Image(getImageStream("image")); } }
                case "webp", "heif", "raw" -> { return new Image(getImageStream("image")); }
                case "mp4", "mkv", "ts", "mov" -> { return new Image(getImageStream("video")); }
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
}
