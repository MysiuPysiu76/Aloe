package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
import org.jetbrains.annotations.NotNull;
import oshi.hardware.HWDiskStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of system disk-related properties wrapped around an {@link HWDiskStore} object.
 * <p>
 * This record provides access to various disk attributes such as model, size, I/O statistics,
 * and formatted properties for UI display, translated using the {@link Translator}.
 * It implements the {@link Properties} interface to allow consistent property access.
 *
 * @param disk the {@link HWDiskStore} instance from which all disk statistics are derived
 * @since 2.5.8
 */
public record DiskProperties(@NotNull HWDiskStore disk) implements Properties {

    /**
     * Returns the name of the disk device.
     *
     * @return the disk name
     */
    public String getName() {
        return disk.getName();
    }

    /**
     * Returns the model name of the disk.
     *
     * @return the disk model
     */
    public String getModel() {
        return disk.getModel();
    }

    /**
     * Returns the serial number of the disk.
     *
     * @return the disk serial number
     */
    public String getSerial() {
        return disk.getSerial();
    }

    /**
     * Returns the formatted size of the disk using {@link UnitConverter}.
     *
     * @return human-readable disk size (e.g., "500 GB")
     */
    public String getSize() {
        return UnitConverter.convert(disk.getSize());
    }

    /**
     * Returns the total number of read operations performed.
     *
     * @return number of disk reads as a string
     */
    public String getReads() {
        return String.valueOf(disk.getReads());
    }

    /**
     * Returns the total number of bytes read.
     *
     * @return number of bytes read as a string
     */
    public String getReadBytes() {
        return String.valueOf(disk.getReadBytes());
    }

    /**
     * Returns the total number of write operations performed.
     *
     * @return number of disk writes as a string
     */
    public String getWrites() {
        return String.valueOf(disk.getWrites());
    }

    /**
     * Returns the total number of bytes written.
     *
     * @return number of bytes written as a string
     */
    public String getWriteBytes() {
        return String.valueOf(disk.getWriteBytes());
    }

    /**
     * Returns the total transfer time in milliseconds.
     *
     * @return transfer time as a string with "ms" suffix
     */
    public String getTransferTime() {
        return disk.getTransferTime() + " ms";
    }

    /**
     * Returns a list of localized property names (translated UI labels) for this disk.
     *
     * @return list of translated property names
     */
    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.disk.name"));
        names.add(Translator.translate("window.properties.disk.model"));
        names.add(Translator.translate("window.properties.disk.serial"));
        names.add(Translator.translate("window.properties.disk.size"));
        names.add(Translator.translate("window.properties.disk.reads"));
        names.add(Translator.translate("window.properties.disk.writes"));
        names.add(Translator.translate("window.properties.disk.transfer"));
        return names;
    }

    /**
     * Returns a list of property values corresponding to the disk's current state.
     *
     * @return list of string values for each disk property
     */
    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getName());
        values.add(getModel());
        values.add(getSerial());
        values.add(getSize());
        values.add(getReads());
        values.add(getWrites());
        values.add(getTransferTime());
        return values;
    }

    /**
     * Returns a map of disk properties, pairing each translated name with its current value.
     *
     * @return map of translated property names to values
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
