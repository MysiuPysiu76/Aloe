package com.example.aloe.utils.ffmpeg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FFmpegDownloader {
    private static final String METADATA_URL = "https://ffbinaries.com/api/v1/version/latest";

    public static void download() throws IOException {
        String platform = detectPlatform();
        JsonNode meta = fetchLatestMetadata();

        JsonNode binNode = meta.path("bin");
        if (binNode.isMissingNode() || binNode.isNull()) {
            throw new IOException("No ffmpeg node available");
        }

        JsonNode platformNode = binNode.path(platform);
        if (platformNode.isMissingNode() || platformNode.isNull()) {
            throw new IOException("No data for platform: " + platform);
        }

        String[] components = {"ffmpeg", "ffprobe"};
        Path installDir = Paths.get(System.getProperty("user.dir"), "ffmpeg");
        if (!Files.exists(installDir)) {
            Files.createDirectories(installDir);
        }

        for (String component : components) {
            JsonNode urlNode = platformNode.path(component);
            if (urlNode.isMissingNode() || urlNode.isNull()) {
                System.out.println("No component: " + component + " for platform: " + platform);
                continue;
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
                        if (!System.getProperty("os.name").toLowerCase(Locale.US).contains("win")) {
                            outPath.toFile().setExecutable(true);
                        }
                    }
                    zis.closeEntry();
                }
            }
            Files.deleteIfExists(zipFile);
        }
    }

    private static String detectPlatform() {
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

    private static JsonNode fetchLatestMetadata() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(METADATA_URL).openConnection();
        conn.setRequestMethod("GET");
        try (InputStream in = conn.getInputStream()) {
            return new ObjectMapper().readTree(in);
        }
    }
}
