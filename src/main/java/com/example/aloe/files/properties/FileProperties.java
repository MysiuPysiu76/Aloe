package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.Utils;
import com.example.aloe.files.FilesUtils;
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

public record FileProperties(@NotNull File file) {

    public String getName() {
        return this.file.getName();
    }

    public String getPath() {
        return this.file.getPath();
    }

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

    public String getSize() {
        if (this.file.isDirectory()) {
            long directorySize = FilesUtils.calculateFileSize(this.file);
            return Utils.convertBytesByUnit(directorySize) + " (" + directorySize + Translator.translate("units.bytes") + ")";
        } else {
            return Utils.convertBytesByUnit(file.length()) + " (" + file.length() + Translator.translate("units.bytes") + ")";
        }
    }

    public String getParent() {
        return this.file.getParent();
    }

    public String getItemsCount() {
        try {
            return String.valueOf(Files.list(Path.of(this.file.getPath())).count());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCreationTime() {
        try {
            FileTime creationTime = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class).creationTime();
            return OffsetDateTime.parse(creationTime.toString()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getModifiedTime() {
        LocalDateTime modifiedDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(this.file.lastModified()),
                ZoneId.systemDefault()
        );
        return modifiedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public String getFreeSpace() {
        return Utils.convertBytesByUnit(this.file.getFreeSpace());
    }

    public List<String> getFilePropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.file-name"));
        names.add(Translator.translate("window.properties.file-path"));
        names.add(Translator.translate("window.properties.file-type"));
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

    public List<String> getFilePropertiesValues() {
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

    public Map<String, String> getProperties() {
        List<String> names = getFilePropertiesNames();
        List<String> values = getFilePropertiesValues();

        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(names.size(), values.size()); i++) {
            map.put(names.get(i), values.get(i));
        }
        return map;
    }
}