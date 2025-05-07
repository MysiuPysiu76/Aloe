package com.example.aloe.utils;

import com.example.aloe.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility class providing general-purpose helper methods used throughout the application.
 * <p>
 * This class includes methods for:
 * <ul>
 *     <li>Getting the index of a key in a {@link Map}</li>
 *     <li>Checking for the existence of a file inside the application resources (including inside JAR files)</li>
 *     <li>Calculating percentage values</li>
 * </ul>
 * </p>
 *
 * @since 2.1.0
 */
public class Utils {

    /**
     * Returns the index of the given key in the order of keys from the provided {@link Map}.
     *
     * @param map the map whose keys will be searched
     * @param key the key to find
     * @return the zero-based index of the key if found, or -1 if not present
     */
    public static int getKeyIndex(Map<String, ?> map, String key) {
        int index = 0;
        for (String k : map.keySet()) {
            if (k.equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    /**
     * Checks if a file exists within the application resources, including inside a JAR archive.
     *
     * @param path     the relative path within the resources (should end with a slash if pointing to a directory)
     * @param fileName the name of the file to search for
     * @return {@code true} if the file exists within the specified path in the resources, {@code false} otherwise
     */
    public static boolean isFileExistsInResources(String path, String fileName) {
        ClassLoader classLoader = Settings.class.getClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource != null) {
            try {
                if (resource.getProtocol().equals("jar")) {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();
                            if (entryName.equals(path + fileName)) {
                                return true;
                            }
                        }
                    }
                } else {
                    File dir = new File(resource.getPath());
                    if (dir.exists() && dir.isDirectory()) {
                        File targetFile = new File(dir, fileName);
                        return targetFile.exists();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Calculates the percentage of a current value relative to a maximum value.
     *
     * @param current the current progress or value
     * @param max     the maximum possible value
     * @return a rounded percentage (0–100). Returns 0 if {@code max} is 0 to avoid division by zero.
     */
    public static double calculatePercentage(double current, double max) {
        if (max == 0) return 0;
        return Math.round((current / max) * 100);
    }
}
