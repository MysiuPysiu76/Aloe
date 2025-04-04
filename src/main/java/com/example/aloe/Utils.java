package com.example.aloe;

import com.example.aloe.settings.SettingsManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
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

    public static boolean isFileExistsInResources(String path, String fileName) {
        ClassLoader classLoader = SettingsManager.class.getClassLoader();
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

    public static String convertBytesByUnit(long size) {
        if (Boolean.TRUE.equals(SettingsManager.getSetting("files", "use-binary-units"))) {
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