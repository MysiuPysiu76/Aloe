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
    public static int getKeyIndex(Map<String, ? extends Object> map, String key) {
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
}