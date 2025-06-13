package com.example.aloe.elements.files;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.Settings;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;

/**
 * Provides a utility method for determining and loading appropriate icons or thumbnails
 * for files and directories based on their type or extension.
 * <p>
 * The {@code FileImage} class maps common file types (e.g. images, videos, archives)
 * to corresponding icon resources. For image files, if the user setting {@code display-thumbnails}
 * is enabled, the actual image file is used as a thumbnail instead of a generic icon.
 * <p>
 * This class supports extensions such as:
 * <ul>
 *   <li>Image files: {@code jpg, jpeg, png, gif, webp, heif, raw}</li>
 *   <li>Video files: {@code mp4, mkv, ts, mov}</li>
 *   <li>Audio files: {@code mp3, ogg}</li>
 *   <li>Documents: {@code pdf, epub, mobi}</li>
 *   <li>Archives: {@code zip, 7z, rar, tar, tar.gz}</li>
 *   <li>Installers: {@code exe, msi, deb, rpm, snap, flatpak, dmg, apk}</li>
 *   <li>Others: {@code torrent, jar, sh, bat, iso}</li>
 * </ul>
 *
 * <p>
 * This utility is typically used in file browser UIs to visually distinguish file types.
 *
 * @see Image
 * @since 2.7.2
 */
public class FileImage {

    /**
     * Returns a JavaFX {@link Image} to visually represent the given file.
     * For directories, a folder icon is returned. For known file types,
     * a representative icon is selected based on the file extension.
     * For image files, a thumbnail preview may be shown if enabled in settings.
     *
     * @param file the file or directory for which to retrieve an icon
     * @return an {@link Image} suitable for representing the given file
     */
    public static Image from(File file) {
        if (file.isDirectory()) {
            return new Image(getImageStream("folder"));
        } else {
            switch (FilesUtils.getExtension(file.getName()).toLowerCase()) {
                case "jpg", "jpeg", "png", "gif" -> {
                    if (Boolean.TRUE.equals(Settings.getSetting("files", "display-thumbnails"))) {
                        return new Image(new File(CurrentDirectory.get(), file.getName()).toURI().toString());
                    } else {
                        return new Image(getImageStream("image"));
                    }
                }
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

    /**
     * Loads an icon image stream from the internal resource path.
     * The icons are stored under {@code /assets/icons/} with a ".png" extension.
     *
     * @param image the base name of the icon file (e.g. "folder", "video", "pdf")
     * @return an {@link InputStream} to the image resource
     */
    static InputStream getImageStream(String image) {
        return FileBox.class.getResourceAsStream("/assets/icons/" + image + ".png");
    }
}
