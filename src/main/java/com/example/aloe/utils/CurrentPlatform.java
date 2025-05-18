package com.example.aloe.utils;

import java.util.Locale;

/**
 * Utility class for detecting the current operating system and architecture.
 *
 * <p>This class provides methods to identify the platform (Windows, macOS, Linux) and distinguish
 * between 32-bit and 64-bit architectures. The results can be used to configure behavior
 * specific to the underlying system.</p>
 *
 * <p>Example platform strings returned:</p>
 * <ul>
 *     <li>{@code "windows-64"}</li>
 *     <li>{@code "windows-32"}</li>
 *     <li>{@code "osx-64"}</li>
 *     <li>{@code "linux-64"}</li>
 *     <li>{@code "linux-32"}</li>
 * </ul>
 *
 * @since 2.0.9
 */
public class CurrentPlatform {

    private static final String OS = System.getProperty("os.name").toLowerCase(Locale.US);
    private static final String ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);

    /**
     * Detects the current platform and returns a string identifying the OS and architecture.
     *
     * @return a string like {@code "windows-64"}, {@code "linux-32"}
     * @throws UnsupportedOperationException if the platform is not recognized
     */
    public static String detectPlatformAndArchitecture() {
        if (isWindows()) {
            return is64Bit() ? "windows-64" : "windows-32";
        } else if (isMac()) {
            return "osx-64";
        } else if (isLinux()) {
            return is64Bit() ? "linux-64" : "linux-32";
        }

        throw new UnsupportedOperationException("Unsupported platform: " + OS);
    }

    /**
     * Detects only the current operating system name.
     *
     * @return the platform name: {@code "Windows"}, {@code "MacOS"}, or {@code "Linux"}
     * @throws UnsupportedOperationException if the platform is not recognized
     */
    public static String detectPlatform() {
        if (isWindows()) {
            return "Windows";
        } else if (isMac()) {
            return "MacOS";
        } else if (isLinux()) {
            return "Linux";
        }

        throw new UnsupportedOperationException("Unsupported platform: " + OS);
    }

    /**
     * Checks whether the current platform is Windows.
     *
     * @return {@code true} if running on Windows, {@code false} otherwise
     */
    public static boolean isWindows() {
        return OS.contains("win");
    }

    /**
     * Checks whether the current platform is macOS.
     *
     * @return {@code true} if running on macOS, {@code false} otherwise
     */
    public static boolean isMac() {
        return OS.contains("mac") || OS.contains("darwin");
    }

    /**
     * Checks whether the current platform is Linux or Unix-like.
     *
     * @return {@code true} if running on Linux/Unix, {@code false} otherwise
     */
    public static boolean isLinux() {
        return OS.contains("nux") || OS.contains("nix");
    }

    /**
     * Checks whether the current architecture is 64-bit.
     *
     * @return {@code true} if 64-bit architecture, {@code false} if 32-bit
     */
    public static boolean is64Bit() {
        return ARCH.contains("64");
    }
}
