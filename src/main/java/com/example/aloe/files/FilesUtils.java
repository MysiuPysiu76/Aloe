package com.example.aloe.files;

import java.io.File;

public class FilesUtils {

    public static long calculateDirectorySize(File file) {
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
                totalSize += calculateDirectorySize(f);
            }
        }
        return totalSize;
    }

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtensionWithDot(File file) {
        return "." + getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        if (fileName.endsWith(".tar.gz")) return "tar.gz";
        return fileName.substring(lastDotIndex + 1);
    }

    public static String getFileName(File file) {
        if (file.isDirectory()) return file.getName();
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex == -1) {
            return file.getName();
        }
        return file.getName().substring(0, lastDotIndex);
    }
}