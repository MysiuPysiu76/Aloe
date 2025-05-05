package com.example.aloe.files;

import java.io.File;
import java.util.List;

/**
 * Utility class providing static methods for common file-related operations.
 * <p>
 * This class includes methods to calculate file sizes, extract file extensions and names,
 * and determine whether a file is a known archive type.
 * </p>
 *
 * <p>All methods are {@code static} and the class is not meant to be instantiated.</p>
 *
 * @since 1.9.7
 */
public class FilesUtils {

    /**
     * Recursively calculates the total size of a file or directory.
     * If the file is a regular file, returns its length.
     * If it's a directory, calculates the total size of all contained files and subdirectories.
     *
     * @param file the file or directory
     * @return total size in bytes, or {@code 0} if the file does not exist
     */
    public static long calculateFileSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        }

        long totalSize = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                totalSize += calculateFileSize(f);
            }
        }
        return totalSize;
    }

    /**
     * Calculates the combined size of multiple files or directories.
     * Uses {@link #calculateFileSize(File)} for each element in the list.
     *
     * @param files list of files and/or directories
     * @return total combined size in bytes
     */
    public static long calculateFileSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += calculateFileSize(file);
        }
        return totalSize;
    }

    /**
     * Retrieves the extension of a file from a {@link File} object.
     *
     * @param file the file
     * @return the file extension without the dot, or an empty string if none
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * Retrieves the extension of a file with the leading dot (e.g. ".txt").
     *
     * @param file the file
     * @return the file extension with a leading dot, or just "." if no extension is found
     */
    public static String getExtensionWithDot(File file) {
        return "." + getExtension(file.getName());
    }

    /**
     * Retrieves the extension from a filename string.
     * Handles compound extensions like ".tar.gz" appropriately.
     *
     * @param fileName the name of the file
     * @return the extension without the dot, or an empty string if not found
     */
    public static String getExtension(String fileName) {
        if (fileName.endsWith(".tar.gz")) return "tar.gz";

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Retrieves the base name of the file (without extension).
     * If the file is a directory, its name is returned as-is.
     *
     * @param file the file
     * @return file name without the extension, or directory name
     */
    public static String getFileName(File file) {
        if (file.isDirectory()) return file.getName();

        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex == -1) {
            return file.getName();
        }
        return file.getName().substring(0, lastDotIndex);
    }

    /**
     * Determines whether a file is a known archive type (e.g. .zip, .tar, .rar, .7z, .jar, .tar.gz).
     * The check is case-sensitive.
     *
     * @param file the file to check
     * @return {@code true} if the file is an archive, {@code false} otherwise
     */
    public static boolean isFileArchive(File file) {
        String fileName = file.getName();
        return file.isFile() && (
                fileName.endsWith(".zip") ||
                        fileName.endsWith(".tar") ||
                        fileName.endsWith(".tar.gz") ||
                        fileName.endsWith(".rar") ||
                        fileName.endsWith(".7z") ||
                        fileName.endsWith(".jar")
        );
    }
}
