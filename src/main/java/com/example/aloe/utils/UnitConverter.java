package com.example.aloe.utils;

import com.example.aloe.settings.Settings;

/**
 * The {@code UnitConverter} class provides utility methods for converting
 * file sizes from bytes into a human-readable format using either binary (base-1024)
 * or decimal (base-1000) units.
 * <p>
 * The unit system (binary vs. decimal) is determined dynamically based on
 * a user setting {@code files.use-binary-units}.
 * <ul>
 *     <li>Binary units: B, KiB, MiB, GiB, TiB, PiB</li>
 *     <li>Decimal units: B, KB, MB, GB, TB, PB</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 *     File file = new File("file.txt");
 *     String readableSize = UnitConverter.convert(file.length());
 *     System.out.println(readableSize); // e.g., "1.5 MB" or "1.4 MiB"
 * </pre>
 *
 * @since 1.7.3
 */
public class UnitConverter {

    /**
     * Converts a size in bytes to a human-readable string using either binary (base-1024)
     * or decimal (base-1000) units, depending on the application settings.
     *
     * @param size the size in bytes
     * @return the formatted size string (e.g., {@code "1.2 MB"}, {@code "3.4 GiB"})
     */
    public static String convert(long size) {
        if (Boolean.TRUE.equals(Settings.getSetting("files", "use-binary-units"))) {
            return convertBytesToGiB(size);
        } else {
            return convertBytesToGB(size);
        }
    }

    /**
     * Converts a size in bytes to a human-readable string using binary units
     * (base-1024), such as KiB, MiB, GiB.
     *
     * @param size the size in bytes
     * @return the formatted size string with binary units
     */
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

    /**
     * Converts a size in bytes to a human-readable string using decimal units
     * (base-1000), such as KB, MB, GB.
     *
     * @param size the size in bytes
     * @return the formatted size string with decimal units
     */
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
