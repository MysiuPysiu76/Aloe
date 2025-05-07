package com.example.aloe.utils;

import java.util.Locale;

/**
 * Utility class for detecting the current operating system and architecture.
 * <p>
 * This class provides methods to identify the platform (Windows, macOS, Linux) and distinguish
 * between 32-bit and 64-bit architectures. The results are used to configure behavior
 * specific to the underlying system.
 * </p>
 *
 * <p>
 * Example platform strings returned:
 * <ul>
 *     <li>{@code "windows-64"}</li>
 *     <li>{@code "windows-32"}</li>
 *     <li>{@code "osx-64"}</li>
 *     <li>{@code "linux-64"}</li>
 *     <li>{@code "linux-32"}</li>
 * </ul>
 * </p>
 *
 * @since 2.0.8
 */
public class CurrentPlatform {

    /**
     * Detects the current platform and returns a string identifying the OS and architecture.
     *
     * @return a string representing the current platform, e.g. {@code "windows-64"}, {@code "linux-32"}
     * @throws UnsupportedOperationException if the platform is not recognized
     */
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

    /**
     * Checks whether the current platform is Windows.
     *
     * @return {@code true} if running on a Windows OS, {@code false} otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.US).contains("win");
    }
}
