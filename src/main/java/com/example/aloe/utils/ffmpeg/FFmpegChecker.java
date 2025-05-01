package com.example.aloe.utils.ffmpeg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FFmpegChecker {

    public static boolean isAvailable() {
        try {
            Process process = new ProcessBuilder("ffmpeg", "-version").redirectErrorStream(true).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDownloaded() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        String ffmpegName = isWindows ? "ffmpeg.exe" : "ffmpeg";
        String ffprobeName = isWindows ? "ffprobe.exe" : "ffprobe";
        Path ffmpegPath = Paths.get("ffmpeg", ffmpegName);
        Path ffprobePath = Paths.get("ffmpeg", ffprobeName);

        return isExecutable(ffmpegPath) && isExecutable(ffprobePath);
    }

    private static boolean isExecutable(Path path) {
        return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path) && (Files.isExecutable(path) || isWindows());
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
