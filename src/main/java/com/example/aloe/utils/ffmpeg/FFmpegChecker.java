package com.example.aloe.utils.ffmpeg;

import com.example.aloe.utils.CurrentPlatform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides utility methods for checking the availability and local presence of FFmpeg and FFprobe.
 * <p>
 * This class allows verification of whether FFmpeg is installed and accessible via system path,
 * and whether the binaries exist in a specific local directory.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * if (FFmpegChecker.isAvailable()) {
 *     // FFmpeg is available in system PATH
 * }
 *
 * if (FFmpegChecker.isDownloaded()) {
 *     // FFmpeg and FFprobe binaries are present locally
 * }
 * </pre>
 *
 * @see FFmpegDownloader
 * @since 1.9.1
 */
public class FFmpegChecker {

    /**
     * Checks whether FFmpeg is available on the system by attempting to execute {@code ffmpeg -version}.
     *
     * @return {@code true} if FFmpeg is found in the system's PATH and the process exits successfully; {@code false} otherwise.
     */
    public static boolean isAvailable() {
        try {
            Process process = new ProcessBuilder("ffmpeg", "-version").redirectErrorStream(true).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether FFmpeg and FFprobe binaries are present in the {@code ./ffmpeg} directory.
     * The method adapts to different platforms (e.g., Windows requires {@code .exe} files).
     *
     * @return {@code true} if both FFmpeg and FFprobe binaries are present and executable; {@code false} otherwise.
     */
    public static boolean isDownloaded() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        String ffmpegName = isWindows ? "ffmpeg.exe" : "ffmpeg";
        String ffprobeName = isWindows ? "ffprobe.exe" : "ffprobe";
        Path ffmpegPath = Paths.get("ffmpeg", ffmpegName);
        Path ffprobePath = Paths.get("ffmpeg", ffprobeName);

        return isExecutable(ffmpegPath) && isExecutable(ffprobePath);
    }

    /**
     * Verifies whether the given file path points to an existing, regular, readable, and executable file.
     *
     * @param path the file path to check
     * @return {@code true} if the file is executable or the OS is Windows; {@code false} otherwise
     */
    private static boolean isExecutable(Path path) {
        return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path) && (Files.isExecutable(path) || CurrentPlatform.isWindows());
    }
}
