package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.utils.UnitConverter;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;

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
import java.util.*;

/**
 * A class that provides various properties of a file or directory, such as name, size, type,
 * creation and modification time, and more.
 * <p>
 * This class implements the {@link Properties} interface and provides localized descriptions
 * using a {@link Translator}. It can be used to extract and display metadata about a file or
 * directory in a user-friendly format.
 *
 * @param file file whose properties are to be read
 * @see Properties
 * @since 1.9.4
 */
public record FileProperties(@NotNull File file) implements Properties {

    /**
     * Returns the name of the file or directory.
     *
     * @return the file name
     */
    public String getName() {
        return this.file.getName();
    }

    /**
     * Returns the absolute path of the file or directory.
     *
     * @return the file path
     */
    public String getPath() {
        return this.file.getPath();
    }

    /**
     * Returns the MIME type of the file. If the file is a directory,
     * a localized string representing a folder is returned.
     *
     * @return the MIME type or folder label
     */
    public String getType() {
        if (this.file.isFile()) {
            try {
                return new Tika().detect(this.file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Translator.translate("window.properties.folder");
        }
    }

    /**
     * Returns the size of the file or the total size of contents in the directory,
     * formatted with units and in bytes.
     *
     * @return formatted file or directory size
     */
    public String getSize() {
        if (this.file.isDirectory()) {
            long directorySize = FilesUtils.calculateFileSize(this.file);
            return UnitConverter.convert(directorySize) + " (" + directorySize + Translator.translate("units.bytes") + ")";
        } else {
            return UnitConverter.convert(file.length()) + " (" + file.length() + Translator.translate("units.bytes") + ")";
        }
    }

    /**
     * Returns a short, human-readable size string or item count for directories.
     *
     * @return short size or item count string
     */
    public String getShortSize() {
        if (this.file.isFile()) {
            return UnitConverter.convert(this.file.length());
        } else {
            return String.format("%s%s", getItemsCount(), (getItemsCount().equals("1") ? Translator.translate("window.properties.item") : Translator.translate("window.properties.items")));
        }
    }

    /**
     * Returns the parent directory path of the file or directory.
     *
     * @return the parent path
     */
    public String getParent() {
        return this.file.getParent();
    }

    /**
     * Returns the number of items in the directory.
     *
     * @return item count as string
     */
    public String getItemsCount() {
        try {
            return String.valueOf(Files.list(Path.of(this.file.getPath())).count());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the creation time of the file or directory formatted as "dd-MM-yyyy HH:mm:ss".
     *
     * @return formatted creation time
     */
    public String getCreationTime() {
        try {
            FileTime creationTime = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class).creationTime();
            return OffsetDateTime.parse(creationTime.toString()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the last modification time of the file or directory formatted as "dd-MM-yyyy HH:mm:ss".
     *
     * @return formatted modification time
     */
    public String getModifiedTime() {
        LocalDateTime modifiedDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(this.file.lastModified()),
                ZoneId.systemDefault()
        );
        return modifiedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    /**
     * Returns the amount of free disk space available on the partition where the file resides.
     *
     * @return formatted free space
     */
    public String getFreeSpace() {
        return UnitConverter.convert(this.file.getFreeSpace());
    }

    /**
     * Returns a list of localized property names to be displayed in the UI.
     *
     * @return list of property names
     */
    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.file-name"));
        names.add(Translator.translate("window.properties.file-path"));
        names.add(Translator.translate("window.properties.media.type"));
        names.add(Translator.translate("window.properties.file-size"));
        names.add(Translator.translate("window.properties.file-parent"));
        if (this.file.isDirectory()) {
            names.add(Translator.translate("window.properties.folder-contents"));
        }
        names.add(Translator.translate("window.properties.file-created"));
        names.add(Translator.translate("window.properties.file-modified"));
        names.add(Translator.translate("window.properties.free-space"));
        return names;
    }

    /**
     * Returns a list of property values corresponding to the names returned by {@link #getPropertiesNames()}.
     *
     * @return list of property values
     */
    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getName());
        values.add(getPath());
        values.add(getType());
        values.add(Translator.translate("window.properties.calculating"));
        values.add(getParent());
        if (this.file.isDirectory()) {
            values.add(getItemsCount() + Translator.translate("window.properties.items"));
        }
        values.add(getCreationTime());
        values.add(getModifiedTime());
        values.add(Translator.translate("window.properties.calculating"));
        return values;
    }

    /**
     * Returns a map of property names and corresponding values.
     * Useful for displaying or exporting file metadata.
     *
     * @return map of file properties
     */
    @Override
    public Map<String, String> getProperties() {
        List<String> names = getPropertiesNames();
        List<String> values = getPropertiesValues();

        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(names.size(), values.size()); i++) {
            map.put(names.get(i), values.get(i));
        }
        return map;
    }
}
