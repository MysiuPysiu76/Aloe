package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The {@code RarArchive} class provides methods for handling RAR archive operations.
 * <p>
 * This implementation supports decompression of RAR files but does not support compression.
 * </p>
 *
 * <h2>Example usage:</h2>
 * <pre>
 * {@code
 * RarArchive rarArchive = new RarArchive();
 * rarArchive.decompress(new File("example.rar"));
 * }
 * </pre>
 *
 * <p><b>Limitations:</b></p>
 * <ul>
 *     <li>Compression of RAR files is not supported and will throw an {@link UnsupportedOperationException}.</li>
 *     <li>Only decompression of standard RAR files is supported.</li>
 * </ul>
 *
 * @since 0.8.8
 */
class RarArchive implements com.example.aloe.archive.Archive {

    /**
     * Throws {@link UnsupportedOperationException}, as RAR compression is not supported.
     *
     * @param parameters the archive parameters.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        throw new UnsupportedOperationException("RAR compression is not supported yet.");
    }

    /**
     * Extracts the contents of the given RAR archive file into a directory.
     * <p>
     * Creates a new directory in the current working directory with the same name as the RAR file (excluding the extension).
     * Files and directories are extracted according to the archive's structure.
     * </p>
     *
     * @param file the RAR file to be extracted. Must not be {@code null}.
     * @throws RuntimeException if an {@link IOException} or {@link com.github.junrar.exception.RarException} occurs during extraction.
     */
    @Override
    public void decompress(File file) {
        File outputDirectory = createOutputDirectory(file);

        try (Archive archive = new Archive(file)) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                processFileHeader(archive, fileHeader, outputDirectory);
            }
        } catch (RarException | IOException e) {
            handleExtractionException(e);
        }
    }

    /**
     * Creates an output directory for the extracted contents based on the RAR file name.
     *
     * @param file the RAR file being decompressed.
     * @return the created output directory.
     */
    private File createOutputDirectory(File file) {
        File output = new File(FilesOperations.getCurrentDirectory(), file.getName().replace(".rar", ""));
        if (!output.exists() && !output.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + output.getAbsolutePath());
        }
        return output;
    }

    /**
     * Processes a file header from the archive, creating directories or extracting files as necessary.
     *
     * @param archive         the archive being extracted.
     * @param fileHeader      the file header representing a file or directory in the archive.
     * @param outputDirectory the base directory for the extracted files.
     * @throws IOException  if an I/O error occurs during extraction.
     * @throws RarException if an error occurs while reading the archive.
     */
    private void processFileHeader(Archive archive, FileHeader fileHeader, File outputDirectory) throws IOException, RarException {
        File outputFile = new File(outputDirectory, fileHeader.getFileNameString().trim());
        if (fileHeader.isDirectory()) {
            if (!outputFile.exists() && !outputFile.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + outputFile.getAbsolutePath());
            }
        } else {
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                archive.extractFile(fileHeader, fos);
            }
        }
    }

    /**
     * Handles exceptions that occur during the extraction process.
     *
     * @param e the exception that occurred.
     */
    private void handleExtractionException(Exception e) {
        throw new RuntimeException("An error occurred during RAR extraction: " + e.getMessage(), e);
    }
}
