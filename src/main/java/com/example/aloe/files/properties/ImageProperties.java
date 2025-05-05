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

/**
 * A {@code Properties} implementation that extracts and provides metadata properties from an image file.
 * <p>
 * This class uses EXIF metadata and standard image file attributes to present image-related information
 * such as dimensions, camera settings, GPS coordinates, orientation, and others.
 * </p>
 * <p>
 * It supports various image formats and reads metadata using the {@code metadata-extractor} library.
 * </p>
 *
 * @param file the image file whose properties are to be read
 *
 * @see Properties
 * @since 1.9.5
 */
public record ImageProperties(@NotNull File file) implements Properties {

    /**
     * Returns the image file type based on its extension.
     *
     * @return the file extension in uppercase (e.g., "JPG", "PNG")
     */
    public String getType() {
        return FilesUtils.getExtension(file).toUpperCase();
    }

    /**
     * Returns the width of the image in pixels.
     *
     * @return width as a string with "px" suffix, or {@code null} if unreadable
     */
    public String getWidth() {
        try {
            BufferedImage image = ImageIO.read(file);
            return image != null ? image.getWidth() + "px" : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the height of the image in pixels.
     *
     * @return height as a string with "px" suffix, or {@code null} if unreadable
     */
    public String getHeight() {
        try {
            BufferedImage image = ImageIO.read(file);
            return image != null ? image.getHeight() + "px" : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether the image has an alpha (transparency) channel.
     *
     * @return localized "yes" or "no", or {@code null} if unreadable
     */
    public String getAlpha() {
        try {
            BufferedImage image = ImageIO.read(file);
            return (image != null && image.getColorModel().hasAlpha()) ? Translator.translate("utils.yes") : Translator.translate("utils.no");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the camera model that took the photo, extracted from EXIF data.
     *
     * @return camera model or {@code null} if not available
     */
    public String getCamera() {
        return getExifTag(ExifIFD0Directory.TAG_MODEL);
    }

    /**
     * Returns the image orientation in a human-readable form based on EXIF metadata.
     *
     * @return localized description of orientation or {@code null} if unknown
     */
    public String getOrientation() {
        return decodeOrientation(Integer.parseInt(getExifTag(ExifIFD0Directory.TAG_ORIENTATION) == null ? "-1" : getExifTag(ExifIFD0Directory.TAG_ORIENTATION)));
    }

    /**
     * Converts orientation numeric code to localized description.
     *
     * @param number EXIF orientation value
     * @return localized string describing the orientation
     */
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

    /**
     * Returns the software name used to process the image.
     *
     * @return software name or {@code null}
     */
    public String getSoftware() {
        return getExifTag(ExifIFD0Directory.TAG_SOFTWARE);
    }

    /**
     * Returns the ISO sensitivity value from EXIF metadata.
     *
     * @return ISO value or {@code null}
     */
    public String getISO() {
        return getExifTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
    }

    /**
     * Returns the F-number (aperture) used to capture the image.
     *
     * @return F-number or {@code null}
     */
    public String getFNumber() {
        return getExifTag(ExifSubIFDDirectory.TAG_FNUMBER);
    }

    /**
     * Returns a localized string describing the camera shooting mode.
     *
     * @return shooting mode or {@code null} if not available
     */
    public String getShootingMode() {
        Integer mode = getExifInteger();
        if (mode == null) return null;
        return decodeShootingMode(mode);
    }

    /**
     * Maps shooting mode EXIF code to localized string.
     *
     * @param mode numeric shooting mode
     * @return localized string for camera mode
     */
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

    /**
     * Returns the compression method used in the image, if available.
     *
     * @return compression type or {@code null}
     */
    public String getCompression() {
        String compression = getExifTag(ExifIFD0Directory.TAG_COMPRESSION);
        return (compression == null || compression.isEmpty()) ? null : compression;
    }

    /**
     * Returns the GPS latitude coordinate of the image.
     *
     * @return latitude as string or {@code null}
     */
    public String getGPSWidth() {
        GpsDirectory gpsDir = getGpsDirectory();
        return gpsDir != null ? String.valueOf(gpsDir.getGeoLocation().getLatitude()) : null;
    }

    /**
     * Returns the GPS longitude coordinate of the image.
     *
     * @return longitude as string or {@code null}
     */
    public String getGPSHeight() {
        GpsDirectory gpsDir = getGpsDirectory();
        return gpsDir != null ? String.valueOf(gpsDir.getGeoLocation().getLongitude()) : null;
    }

    /**
     * Returns the capture date of the image in {@code yyyy-MM-dd} format.
     *
     * @return formatted date or {@code null}
     */
    public String getDate() {
        String dateTimeOriginal = getExifDate(this.file);
        if (dateTimeOriginal != null) {
            try {
                Date date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(dateTimeOriginal);
                return formatDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns the capture time of the image in {@code HH:mm:ss} format.
     *
     * @return formatted time or {@code null}
     */
    public String getTime() {
        String dateTimeOriginal = getExifDate(this.file);
        if (dateTimeOriginal != null) {
            try {
                Date date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(dateTimeOriginal);
                return formatTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Extracts original EXIF datetime string.
     *
     * @param photoFile the image file
     * @return EXIF datetime string or {@code null}
     */
    private static String getExifDate(File photoFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(photoFile);
            ExifSubIFDDirectory subDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            return subDirectory != null ? subDirectory.getString(36867) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads metadata from the image file.
     *
     * @return parsed {@code Metadata} object or {@code null}
     */
    private Metadata getMetadata() {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Reads a specific EXIF tag's string value from metadata.
     *
     * @param tag the EXIF tag identifier
     * @return tag value or {@code null}
     */
    private String getExifTag(int tag) {
        Metadata metadata = getMetadata();
        if (metadata == null) return null;

        ExifIFD0Directory dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (dir != null && dir.containsTag(tag)) return dir.getString(tag);
        if (subDir != null && subDir.containsTag(tag)) return subDir.getString(tag);
        return null;
    }

    /**
     * Reads an EXIF integer tag for shooting mode.
     *
     * @return shooting mode as integer or {@code null}
     */
    private Integer getExifInteger() {
        Metadata metadata = getMetadata();
        if (metadata == null) return null;

        ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        return (subDir != null) ? subDir.getInteger(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXPOSURE_PROGRAM) : null;
    }

    /**
     * Gets the GPS directory from image metadata.
     *
     * @return {@code GpsDirectory} or {@code null}
     */
    private GpsDirectory getGpsDirectory() {
        Metadata metadata = getMetadata();
        return metadata != null ? metadata.getFirstDirectoryOfType(GpsDirectory.class) : null;
    }

    /**
     * Formats a {@code Date} object into {@code yyyy-MM-dd} format.
     *
     * @param date the date to format
     * @return formatted string or {@code null}
     */
    private static String formatDate(Date date) {
        return (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
    }

    /**
     * Formats a {@code Date} object into {@code HH:mm:ss} format.
     *
     * @param date the date to format
     * @return formatted time or {@code null}
     */
    private static String formatTime(Date date) {
        return (date != null) ? new SimpleDateFormat("HH:mm:ss").format(date) : null;
    }

    /**
     * Returns a list of localized property names to be displayed in the UI.
     *
     * @return list of property names
     */
    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.media.type"));
        names.add(Translator.translate("window.properties.media.width"));
        names.add(Translator.translate("window.properties.media.height"));
        names.add(Translator.translate("window.properties.image.has-alpha"));
        names.add(Translator.translate("window.properties.image.camera-mode"));
        names.add(Translator.translate("window.properties.media.camera"));
        names.add(Translator.translate("window.properties.image.orientation"));
        names.add(Translator.translate("window.properties.image.software"));
        names.add(Translator.translate("window.properties.image.iso"));
        names.add(Translator.translate("window.properties.image.f-number"));
        names.add(Translator.translate("window.properties.image.compression"));
        names.add(Translator.translate("window.properties.media.gps-width"));
        names.add(Translator.translate("window.properties.media.gps-height"));
        names.add(Translator.translate("window.properties.media.gps-date"));
        names.add(Translator.translate("window.properties.media.gps-time"));
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
            if (values.get(i) != null) {
            map.put(names.get(i), values.get(i));
            }
        }
        return map;
    }
}
