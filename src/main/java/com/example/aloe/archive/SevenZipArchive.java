package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;

/**
 * Handles compression and decompression of files using the 7z (7-Zip) archive format.
 * Implements the {@link Archive} interface.
 *
 * <p>Some fragments of code are based on examples from:
 * <a href="https://memorynotfound.com/java-7z-seven-zip-example-compress-decompress-file/">here</a></p>
 *
 * @see ZipArchive
 * @since 0.9.9
 */
class SevenZipArchive implements Archive {

    /**
     * Compresses a list of files into a single 7z archive.
     *
     * @param parameters The parameters for compression, including the list of files and the output archive name.
     * @throws RuntimeException If an I/O error occurs during compression.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        File archiveFile = new File(FilesOperations.getCurrentDirectory(), parameters.getFileName() + ".7z");

        try (SevenZOutputFile out = new SevenZOutputFile(archiveFile)) {
            for (File file : parameters.getFiles()) {
                addFileToArchive(out, file, "");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error during compression", e);
        }
    }

    /**
     * Decompresses a 7z archive into a directory named after the archive (without the extension).
     *
     * @param file The 7z archive file to decompress.
     * @throws RuntimeException If an I/O error occurs during decompression or if directories cannot be created.
     */
    @Override
    public void decompress(File file) {
        String archiveName = extractArchiveName(file);
        File outputDirectory = createOutputDirectory(archiveName);
        try (SevenZFile sevenZFile = new SevenZFile(file)) {
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    extractFile(sevenZFile, outputDirectory, entry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error during decompression", e);
        }
    }

    /**
     * Adds a file or directory to the 7z archive.
     *
     * @param out  The {@link SevenZOutputFile} to write to.
     * @param file The file or directory to add to the archive.
     * @param dir  The current directory path in the archive.
     * @throws RuntimeException If an I/O error occurs or the file type is invalid.
     */
    private void addFileToArchive(SevenZOutputFile out, File file, String dir) {
        String entryName = dir.isEmpty() ? file.getName() : dir + "/" + file.getName();
        try {
            if (file.isFile()) {
                writeFileToArchive(out, file, entryName);
            } else if (file.isDirectory()) {
                writeDirectoryToArchive(out, file, entryName);
            } else {
                throw new IllegalArgumentException("Invalid file type: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error adding file to archive: " + file, e);
        }
    }

    /**
     * Writes a single file to the 7z archive.
     *
     * @param out       The {@link SevenZOutputFile} to write to.
     * @param file      The file to write.
     * @param entryName The name of the entry in the archive.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    private void writeFileToArchive(SevenZOutputFile out, File file, String entryName) throws IOException {
        SevenZArchiveEntry entry = out.createArchiveEntry(file, entryName);
        out.putArchiveEntry(entry);

        try (FileInputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.closeArchiveEntry();
    }

    /**
     * Writes a directory and its contents to the 7z archive.
     *
     * @param out       The {@link SevenZOutputFile} to write to.
     * @param directory The directory to write.
     * @param entryName The name of the directory entry in the archive.
     * @throws IOException If an I/O error occurs while processing the directory or its contents.
     */
    private void writeDirectoryToArchive(SevenZOutputFile out, File directory, String entryName) throws IOException {
        SevenZArchiveEntry entry = out.createArchiveEntry(directory, entryName + "/");
        out.putArchiveEntry(entry);
        out.closeArchiveEntry();

        File[] children = directory.listFiles();
        if (children != null) {
            for (File child : children) {
                addFileToArchive(out, child, entryName);
            }
        }
    }

    /**
     * Extracts the name of the archive (without the file extension).
     *
     * @param file The archive file.
     * @return The name of the archive without its extension.
     */
    private String extractArchiveName(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    /**
     * Creates the output directory for decompression.
     *
     * @param archiveName The name of the archive, which will be used as the directory name.
     * @return The created directory.
     * @throws RuntimeException If the directory cannot be created.
     */
    private File createOutputDirectory(String archiveName) {
        File outputDirectory = new File(FilesOperations.getCurrentDirectory(), archiveName);
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDirectory);
        }
        return outputDirectory;
    }

    /**
     * Extracts a single file from the 7z archive and writes it to the output directory.
     *
     * @param sevenZFile      The {@link SevenZFile} to read from.
     * @param outputDirectory The directory to extract the file to.
     * @param entry           The entry in the archive to extract.
     * @throws IOException If an I/O error occurs during extraction.
     */
    private void extractFile(SevenZFile sevenZFile, File outputDirectory, SevenZArchiveEntry entry) throws IOException {
        File outputFile = new File(outputDirectory, entry.getName());
        File parent = outputFile.getParentFile();

        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("Failed to create parent directories for: " + outputFile);
        }
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = sevenZFile.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}