package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.settings.SettingsManager;

import java.io.File;
import java.util.List;

/**
 * Provides methods for handling file archives, including compression and extraction.
 * <p>
 * This class supports multiple archive formats, such as ZIP, TAR, and TAR.GZ. It delegates the actual
 * operations to format-specific handlers and integrates with application settings for additional behavior.
 * </p>
 *
 * Example usage:
 * <pre>
 *     List<File> files = List.of(new File("file1.txt"), new File("file2.txt"));
 *     ArchiveHandler.compress(files, "archive.zip", true, false, null, ArchiveType.ZIP);
 *
 *     File archive = new File("archive.zip");
 *     ArchiveHandler.extract(archive);
 * </pre>
 *
 * @since 0.8.6
 */
public class ArchiveHandler {

    /**
     * Compresses a list of files into an archive of the specified type.
     *
     * @param files        the list of files to compress
     * @param fileName     the name of the output archive file
     * @param useCompress  whether to use compression
     * @param usePassword  whether to use password protection
     * @param password     the password for the archive (if {@code usePassword} is true)
     * @param archiveType  the type of archive to create (e.g., ZIP, TAR, TAR.GZ)
     * @throws IllegalArgumentException if {@code files} is empty or {@code fileName} is null
     * @see ZipArchiveHandler
     * @see TarArchiveHandler
     * @see TarGzArchiveHandler
     */
    public static void compress(List<File> files, String fileName, boolean useCompress, boolean usePassword, String password, ArchiveType archiveType) {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be blank");
        }
        switch (archiveType) {
            case ZIP -> ZipArchiveHandler.compress(files, fileName, useCompress, usePassword, password);
            case TAR -> TarArchiveHandler.compress(files, fileName);
            case TAR_GZ -> TarGzArchiveHandler.compress(files, fileName);
        }
    }

    /**
     * Extracts the contents of an archive file.
     * <p>
     * This method determines the archive type based on the file extension and delegates the extraction process
     * to the appropriate handler. If the application setting "delete-archive-after-extract" is enabled,
     * the archive will be deleted after extraction.
     * </p>
     *
     * @param file the archive file to extract
     * @throws IllegalArgumentException if {@code file} is not a supported archive format
     * @see FilesOperations#getExtension(File)
     * @see ZipArchiveHandler
     * @see RarArchiveHandler
     * @see TarArchiveHandler
     * @see TarGzArchiveHandler
     */
   public static void extract(File file) {
        switch (FilesOperations.getExtension(file)) {
            case "zip" -> ZipArchiveHandler.extract(file);
            case "rar" -> RarArchiveHandler.extract(file);
            case "tar" -> TarArchiveHandler.extract(file);
            case "tar.gz" -> TarGzArchiveHandler.extract(file);
        }
        if (SettingsManager.getSetting("files", "delete-archive-after-extract")) {
            FilesOperations.deleteFile(file);
        }
    }
}