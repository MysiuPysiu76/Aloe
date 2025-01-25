package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;

//some fragments of code come from https://memorynotfound.com/java-7z-seven-zip-example-compress-decompress-file/
class SevenZipArchive implements Archive {

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

    private String extractArchiveName(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    private File createOutputDirectory(String archiveName) {
        File outputDirectory = new File(FilesOperations.getCurrentDirectory(), archiveName);
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDirectory);
        }
        return outputDirectory;
    }

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