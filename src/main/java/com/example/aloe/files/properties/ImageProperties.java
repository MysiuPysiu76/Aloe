package com.example.aloe.files.properties;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.example.aloe.utils.Translator;
import com.example.aloe.files.FilesUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public record ImageProperties(@NotNull File file) {

    public String getType() {
        return FilesUtils.getExtension(file).toUpperCase();
    }

    public String getWidth() {
        try {
            BufferedImage image = ImageIO.read(file);
            return image != null ? String.valueOf(image.getWidth()) + "px" : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHeight() {
        try {
            BufferedImage image = ImageIO.read(file);
            return image != null ? String.valueOf(image.getHeight()) + "px" : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAlpha() {
        try {
            BufferedImage image = ImageIO.read(file);
            return (image != null && image.getColorModel().hasAlpha()) ? Translator.translate("utils.yes") : Translator.translate("utils.no");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCamera() {
        return getExifTag(ExifIFD0Directory.TAG_MODEL);
    }

    public String getOrientation() {
        return decodeOrientation(Integer.parseInt(getExifTag(ExifIFD0Directory.TAG_ORIENTATION) == null ? String.valueOf(-1) : getExifTag(ExifIFD0Directory.TAG_ORIENTATION)));
    }

    private String decodeOrientation(Integer number) {
        return switch (number) {
            case 1 -> Translator.translate("window.properties.image.orientation.normal");
            case 2 -> Translator.translate("window.properties.image.orientation.mirror-horizontal");
            case 3 -> Translator.translate("window.properties.image.orientation.rotate-180");
            case 4 -> Translator.translate("window.properties.image.orientation.mirror-vertical");
            case 5 -> Translator.translate("window.properties.image.orientation.rotate-90-right-mirror");
            case 6 -> Translator.translate("window.properties.image.orientation.rotate-90-right");
            case 7 -> Translator.translate("window.properties.image.orientation.rotate-90-left-mirror");
            case 8 -> Translator.translate("window.properties.image.orientation.rotate-90-left");
            default -> null;
        };
    }

    public String getSoftware() {
        return getExifTag(ExifIFD0Directory.TAG_SOFTWARE);
    }

    public String getISO() {
        return getExifTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
    }

    public String getFNumber() {
        return getExifTag(ExifSubIFDDirectory.TAG_FNUMBER);
    }

    public String getShootingMode() {
        Integer mode = getExifInteger();
        if (mode == null) {
            return null;
        } else {
            return decodeShootingMode(mode);
        }
    }

    private String decodeShootingMode(Integer mode) {
        return switch (mode) {
            case 1 -> Translator.translate("window.properties.image.camera-mode.manual");
            case 2 -> Translator.translate("window.properties.image.camera-mode.auto");
            case 3 -> Translator.translate("window.properties.image.camera-mode.aperture-priority");
            case 4 -> Translator.translate("window.properties.image.camera-mode.shutter-priority");
            case 5 -> Translator.translate("window.properties.image.camera-mode.creative-mode");
            case 6 -> Translator.translate("window.properties.image.camera-mode.action-mode");
            case 7 -> Translator.translate("window.properties.image.camera-mode.portrait");
            case 8 -> Translator.translate("window.properties.image.camera-mode.landscape");
            default -> Translator.translate("window.properties.image.camera-mode.other");
        };
    }

    public String getCompression() {
        String compression = getExifTag(ExifIFD0Directory.TAG_COMPRESSION);
        return (compression == null || compression.isEmpty()) ? null : compression;
    }

    public String getGPSWidth() {
        GpsDirectory gpsDir = getGpsDirectory();
        return gpsDir != null ? String.valueOf(gpsDir.getGeoLocation().getLatitude()) : null;
    }

    public String getGPSHeight() {
        GpsDirectory gpsDir = getGpsDirectory();
        return gpsDir != null ? String.valueOf(gpsDir.getGeoLocation().getLongitude()) : null;
    }

    public String getDate() {
        String dateTimeOriginal = getExifDate(this.file);
        if (dateTimeOriginal != null) {
            try {
                SimpleDateFormat exifFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date date = exifFormat.parse(dateTimeOriginal);
                return formatDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getTime() {
        String dateTimeOriginal = getExifDate(this.file);
        if (dateTimeOriginal != null) {
            try {
                SimpleDateFormat exifFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date date = exifFormat.parse(dateTimeOriginal);
                return formatTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getExifDate(File photoFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(photoFile);

            ExifSubIFDDirectory subDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (subDirectory != null) {
                String dateTimeOriginal = subDirectory.getString(36867);
                if (dateTimeOriginal != null) {
                    return dateTimeOriginal;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Metadata getMetadata() {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            return null;
        }
    }

    private String getExifTag(int tag) {
        Metadata metadata = getMetadata();
        if (metadata == null) return null;

        ExifIFD0Directory dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (dir != null && dir.containsTag(tag)) return dir.getString(tag);
        if (subDir != null && subDir.containsTag(tag)) return subDir.getString(tag);

        return null;
    }

    private Integer getExifInteger() {
        Metadata metadata = getMetadata();
        if (metadata == null) return null;

        ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        return (subDir != null) ? subDir.getInteger(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXPOSURE_PROGRAM) : null;
    }

    private GpsDirectory getGpsDirectory() {
        Metadata metadata = getMetadata();
        return metadata != null ? metadata.getFirstDirectoryOfType(GpsDirectory.class) : null;
    }

    private static String formatDate(Date date) {
        return (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
    }

    private static String formatTime(Date date) {
        return (date != null) ? new SimpleDateFormat("HH:mm:ss").format(date) : null;
    }

    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.image.type"));
        names.add(Translator.translate("window.properties.image.width"));
        names.add(Translator.translate("window.properties.image.height"));
        names.add(Translator.translate("window.properties.image.has-alpha"));
        names.add(Translator.translate("window.properties.image.camera-mode"));
        names.add(Translator.translate("window.properties.image.camera"));
        names.add(Translator.translate("window.properties.image.orientation"));
        names.add(Translator.translate("window.properties.image.software"));
        names.add(Translator.translate("window.properties.image.iso"));
        names.add(Translator.translate("window.properties.image.f-number"));
        names.add(Translator.translate("window.properties.image.compression"));
        names.add(Translator.translate("window.properties.image.gps-width"));
        names.add(Translator.translate("window.properties.image.gps-height"));
        names.add(Translator.translate("window.properties.image.gps-date"));
        names.add(Translator.translate("window.properties.image.gps-time"));
        return names;
    }

    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getType());
        values.add(getWidth());
        values.add(getHeight());
        values.add(getAlpha());
        values.add(getShootingMode());
        values.add(getCamera());
        values.add(getOrientation());
        values.add(getSoftware());
        values.add(getISO());
        values.add(getFNumber());
        values.add(getCompression());
        values.add(getGPSWidth());
        values.add(getGPSHeight());
        values.add(getDate());
        values.add(getTime());
        return values;
    }

    public Map<String, String> getProperties() {
        List<String> names = getPropertiesNames();
        List<String> values = getPropertiesValues();

        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(names.size(), values.size()); i++) {
            if (values.get(i) != null) {
            map.put(names.get(i), values.get(i));
            }
        }
        return map;
    }
}

