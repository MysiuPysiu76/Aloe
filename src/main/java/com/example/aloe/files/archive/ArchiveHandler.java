package com.example.aloe.files.archive;

import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.Settings;

import java.io.File;

/**
 * Provides methods for handling file archives, including compression and extraction.
 * <p>
 * This class supports multiple archive formats, such as ZIP, TAR, and TAR.GZ. It delegates the actual
 * operations to format-specific handlers and integrates with application settings for additional behavior,
 * such as optional deletion of the archive after extraction.
 * </p>
 *
 * <h2>Example usage:</h2>
 * <pre>{@code
 *     ArchiveParameters parameters = new ArchiveParameters(List.of(new File("file1.txt"), new File("file2.txt")), ArchiveType.ZIP, "archive.zip", true);
 *     ArchiveHandler.compress(parameters);
 *
 *     ArchiveHandler.decompress(new File("archive.tar"));
 * }</pre>
 *
 * @see ZipArchive
 * @see SevenZipArchive
 * @see JarArchive
 * @see RarArchive
 * @see TarArchive
 * @see TarGzArchive
 * @since 0.8.6
 */
public class ArchiveHandler {

    /**
     * Compresses a list of files into an archive of the specified type.
     * <p>
     * The method uses the provided {@link ArchiveParameters} object to determine the files to compress,
     * the name of the resulting archive, and the type of archive (ZIP, TAR, TAR.GZ).
     * </p>
     *
     * <ul>
     *     <li>{@code files} - List of files to compress.</li>
     *     <li>{@code fileName} - Name of the resulting archive file (without extension).</li>
     *     <li>{@code archiveType} - Type of archive to create (e.g., ZIP, TAR, TAR.GZ).</li>
     * </ul>
     *
     * @param parameters The {@link ArchiveParameters} object containing the compression details:
     * @throws IllegalArgumentException if the {@code files} list is empty or {@code fileName} is blank.
     */
    public static void compress(ArchiveParameters parameters) {
        if (parameters.getFiles().isEmpty()) {
            throw new IllegalArgumentException("Files list cannot be empty");
        }
        if (parameters.getFileName().isEmpty() || parameters.getFileName().isBlank()) {
            throw new IllegalArgumentException("File name cannot be blank");
        }
        switch (parameters.getArchiveType()) {
            case ZIP -> new ZipArchive().compress(parameters);
            case SEVEN_ZIP -> new SevenZipArchive().compress(parameters);
            case JAR -> new JarArchive().compress(parameters);
            case TAR -> new TarArchive().compress(parameters);
            case TAR_GZ -> new TarGzArchive().compress(parameters);
        }
    }

    /**
     * Extracts the contents of an archive file.
     * <p>
     * This method determines the archive type based on the file extension and delegates the extraction process
     * to the appropriate handler. Supported archive formats include:
     * <ul>
     *     <li>ZIP (.zip)</li>
     *     <li>SEVEN_ZIP (.7z)</li>
     *     <li>JAR (.jar)</li>
     *     <li>RAR (.rar)</li>
     *     <li>TAR (.tar)</li>
     *     <li>TAR.GZ (.tar.gz)</li>
     * </ul>
     * If the application setting "delete-archive-after-extract" is enabled, the archive file will be deleted
     * after successful extraction.
     * </p>
     *
     * @param file The archive file to extract. The file type is determined by its extension.
     * @throws IllegalArgumentException if the file does not exist or if its format is unsupported.
     * @see com.example.aloe.files.FilesUtils#getExtension(File)
     */
    public static void extract(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        switch (ArchiveType.fromString(FilesUtils.getExtensionWithDot(file).toLowerCase())) {
            case ZIP -> new ZipArchive().decompress(file);
            case SEVEN_ZIP -> new SevenZipArchive().decompress(file);
            case JAR -> new JarArchive().decompress(file);
            case RAR -> new RarArchive().decompress(file);
            case TAR -> new TarArchive().decompress(file);
            case TAR_GZ -> new TarGzArchive().decompress(file);
        }
        if (Boolean.TRUE.equals(Settings.getSetting("files", "delete-archive-after-extract"))) {
            new FileDeleteTask(file, true);
        }
    }
}