package com.example.aloe.utils.ffmpeg;

import com.example.aloe.utils.CurrentPlatform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class responsible for downloading and extracting FFmpeg and FFprobe
 * binaries for the current platform.
 * <p>
 * Downloads are based on the latest version metadata provided by ffbinaries.com.
 * Binaries are extracted into a local {@code ./ffmpeg} directory.
 * </p>
 *
 * @see FFmpegChecker
 * @since 1.9.2
 */
public class FFmpegDownloader {

    private static final String METADATA_URL = "https://ffbinaries.com/api/v1/version/latest";

    /**
     * Downloads and extracts the latest FFmpeg and FFprobe binaries for the current platform.
     *
     * @throws IOException if downloading or extracting fails
     */
    public static void download() throws IOException {
        String platform = CurrentPlatform.detectPlatform();
        JsonNode metadata = fetchLatestMetadata();
        JsonNode binNode = metadata.path("bin");

        if (binNode.isMissingNode() || binNode.isNull()) {
            throw new IOException("No ffmpeg node available");
        }

        JsonNode platformNode = binNode.path(platform);
        if (platformNode.isMissingNode() || platformNode.isNull()) {
            throw new IOException("No data for platform: " + platform);
        }

        String[] components = {"ffmpeg", "ffprobe"};
        Path installDir = Paths.get(System.getProperty("user.dir"), "ffmpeg");
        Files.createDirectories(installDir);

        for (String component : components) {
            downloadAndExtractComponent(platformNode, component, installDir);
        }
    }

    /**
     * Downloads and extracts a specific component (either {@code ffmpeg} or {@code ffprobe})
     * from the metadata provided by ffbinaries for the given platform.
     *
     * @param platformNode the JSON node for the detected platform containing download URLs
     * @param component    the component name, e.g., "ffmpeg" or "ffprobe"
     * @param installDir   the directory where the binaries should be installed
     * @throws IOException if the component cannot be downloaded or extracted
     */
    private static void downloadAndExtractComponent(JsonNode platformNode, String component, Path installDir) throws IOException {
        JsonNode urlNode = platformNode.path(component);
        if (urlNode.isMissingNode() || urlNode.isNull()) {
            System.out.printf("No component: %s for platform: %s%n", component, CurrentPlatform.detectPlatform());
            return;
        }

        String downloadUrl = urlNode.asText();
        Path zipFile = installDir.resolve(component + ".zip");

        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, zipFile, StandardCopyOption.REPLACE_EXISTING);
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path outPath = installDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
                    if (!CurrentPlatform.isWindows()) {
                        outPath.toFile().setExecutable(true);
                    }
                }
                zis.closeEntry();
            }
        }

        Files.deleteIfExists(zipFile);
    }

    /**
     * Fetches the latest FFmpeg version metadata from ffbinaries.com.
     *
     * @return JSON node containing the metadata
     * @throws IOException if the connection or parsing fails
     */
    private static JsonNode fetchLatestMetadata() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(METADATA_URL).openConnection();
        conn.setRequestMethod("GET");
        try (InputStream in = conn.getInputStream()) {
            return new ObjectMapper().readTree(in);
        }
    }
}
