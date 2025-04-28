package com.example.aloe.utils;

import com.example.aloe.settings.Settings;

public class UnitConverter {

    public static String convert(long size) {
        if (Boolean.TRUE.equals(Settings.getSetting("files", "use-binary-units"))) {
            return convertBytesToGiB(size);
        } else {
            return convertBytesToGB(size);
        }
    }

    private static String convertBytesToGiB(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KiB ", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MiB ", size / (1024.0 * 1024.0));
        } else if (size < 1024L * 1024 * 1024 * 1024) {
            return String.format("%.1f GiB ", size / (1024.0 * 1024.0 * 1024.0));
        } else if (size < 1024L * 1024 * 1024 * 1024 * 1024) {
            return String.format("%.1f TiB ", size / (1024 * 1024.0 * 1024.0 * 1024));
        } else {
            return String.format("%.1f PiB ", size / (1024 * 1024.0 * 1024.0 * 1024 * 1024));
        }
    }

    private static String convertBytesToGB(long size) {
        if (size < 1000) {
            return size + " B";
        } else if (size < 1000 * 1000) {
            return String.format("%.1f KB ", size / 1000.0);
        } else if (size < 1000 * 1000 * 1000) {
            return String.format("%.1f MB ", size / (1000.0 * 1000.0));
        } else if (size < 1000L * 1000 * 1000 * 1000) {
            return String.format("%.1f GB ", size / (1000.0 * 1000.0 * 1000.0));
        } else if (size < 1000L * 1000 * 1000 * 1000 * 1000) {
            return String.format("%.1f TB ", size / (1000 * 1000.0 * 1000.0 * 1000));
        } else {
            return String.format("%.1f PB ", size / (1000 * 1000.0 * 1000.0 * 1000 * 1000));
        }
    }
}