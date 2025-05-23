package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
import oshi.software.os.OSFileStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents properties of a disk partition (file store) using OSHI's {@link OSFileStore}.
 * Provides various details such as name, mount point, space usage, and configuration options.
 *
 * <p>This record implements the {@link Properties} interface and provides human-readable
 * property names and values, typically for display in a UI.</p>
 *
 * @see Properties
 * @since 2.5.9
 */
public record PartitionProperties(OSFileStore store) implements Properties {

    /**
     * Returns the name of the file store (partition).
     *
     * @return the name of the partition
     */
    public String getName() {
        return store.getName();
    }

    /**
     * Returns the mount point of the partition.
     *
     * @return the mount point path
     */
    public String getMountPoint() {
        return store.getMount();
    }

    /**
     * Returns the volume identifier of the partition.
     *
     * @return the volume name or identifier
     */
    public String getVolume() {
        return store.getVolume();
    }

    /**
     * Returns the file system type of the partition (e.g., ext4, ntfs).
     *
     * @return the type of the file system
     */
    public String getType() {
        return store.getType();
    }

    /**
     * Returns file system-specific options (e.g., rw, nosuid).
     *
     * @return the options string
     */
    public String getOptions() {
        return store.getOptions();
    }

    /**
     * Returns the UUID of the partition, if available.
     *
     * @return the partition UUID
     */
    public String getUUID() {
        return store.getUUID();
    }

    /**
     * Returns the description of the file store (e.g., "Local Disk").
     *
     * @return a human-readable description of the partition
     */
    public String getDescription() {
        return store.getDescription();
    }

    /**
     * Returns the amount of free space available on the partition, formatted for display.
     *
     * @return a string representing free space (e.g., "100 GB")
     */
    public String getFreeSpace() {
        return UnitConverter.convert(store.getFreeSpace());
    }

    /**
     * Returns the logical volume name, if applicable.
     *
     * @return the logical volume name
     */
    public String getLogicalVolume() {
        return store.getLogicalVolume();
    }

    /**
     * Returns the total size of the partition, formatted for display.
     *
     * @return a string representing total size (e.g., "256 GB")
     */
    public String getTotalSpace() {
        return UnitConverter.convert(store.getTotalSpace());
    }

    /**
     * Returns the translated list of property names for display purposes.
     * This includes labels such as "Name", "Mount Point", "Type", etc.
     *
     * @return a list of translated property names
     */
    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.partition.name"));
        names.add(Translator.translate("window.properties.partition.mount"));
        names.add(Translator.translate("window.properties.partition.type"));
        names.add(Translator.translate("window.properties.partition.free-space"));
        names.add(Translator.translate("window.properties.partition.total-space"));
        names.add(Translator.translate("window.properties.partition.options"));
        names.add(Translator.translate("window.properties.partition.uuid"));
        return names;
    }

    /**
     * Returns the corresponding values for the translated property names.
     * The order matches the list returned by {@link #getPropertiesNames()}.
     *
     * @return a list of property values
     */
    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getVolume());
        values.add(getMountPoint());
        values.add(getType());
        values.add(getFreeSpace());
        values.add(getTotalSpace());
        values.add(getOptions());
        values.add(getUUID());
        return values;
    }

    /**
     * Returns a map of translated property names to their respective values.
     * Useful for rendering key-value views of the partition's information.
     *
     * @return a map of property names and values
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
