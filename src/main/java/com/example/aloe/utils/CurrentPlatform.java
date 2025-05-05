package com.example.aloe.utils;

import java.util.Locale;

public class CurrentPlatform {

    public static String detectPlatform() {
        String os = System.getProperty("os.name").toLowerCase(Locale.US);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.US);

        if (os.contains("win")) {
            return arch.contains("64") ? "windows-64" : "windows-32";
        } else if (os.contains("mac") || os.contains("darwin")) {
            return "osx-64";
        } else if (os.contains("nux") || os.contains("nix")) {
            return arch.contains("64") ? "linux-64" : "linux-32";
        }

        throw new UnsupportedOperationException("Unsupported platform: " + os);
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.US).contains("win");
    }
}