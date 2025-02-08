package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.Objects;

/**
 * The {@code TarArchive} class implements the {@link Archive} interface and provides functionality
 * for compressing and decompressing files in the TAR format.
 * <p>
 * Compression creates a TAR archive from the provided files, and decompression extracts
 * the contents of a TAR archive into the specified directory.
 * </p>
 * <p>
 * This class utilizes the Apache Commons Compress library to handle TAR archive operations.
 * </p>
 *
 * @see Archive
 * @see TarArchiveEntry
 * @see TarArchiveInputStream
 * @see TarArchiveOutputStream
 * @since 0.9.7
 */
class TarArchive implements Archive {

    /**
     * Compresses the files specified in the parameters into a TAR archive.
     * Creates an archive file with the name specified in {@link ArchiveParameters} and the .tar extension.
     * <p>
     * Each file from the {@code parameters.getFiles()} list is added to the archive.
     * </p>
     *
     * @param parameters The compression parameters, including the list of files to compress and the archive file name.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        File outputFile = new File(FilesOperations.getCurrentDirectory(), parameters.getFileName());
        try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new FileOutputStream(outputFile))) {
            for (File file : parameters.getFiles()) {
                addFileToTar(tarOut, file, "");
            }
            WindowService.openArchiveInfoWindow("window.archive.compress.success");
        } catch (IOException e) {
            handleError("window.archive.compress.error", e);
        }
    }

    /**
     * Decompresses a TAR archive file into a directory in the current working directory.
     * <p>
     * If the destination directory does not exist, it will be created. Files and directories from
     * the archive will be extracted into this directory.
     * </p>
     *
     * @param file The TAR archive file to decompress.
     */
    @Override
    public void decompress(File file) {
        File destDir = new File(FilesOperations.getCurrentDirectory(), getOutputDirectoryName(file));
        if (!destDir.exists() && !destDir.mkdirs()) {
            handleError("window.archive.extract.error", new IOException("Failed to create destination directory."));
            return;
        }

        try (TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(file))) {
            extractEntries(tis, destDir);
            WindowService.openArchiveInfoWindow("window.archive.extract.success");
        } catch (IOException e) {
            handleError("window.archive.extract.error", e);
        }
    }

    /**
     * Adds a file to the TAR archive. If the file is a directory, it recursively adds the directory's contents.
     *
     * @param tarOut The {@link TarArchiveOutputStream} used to write the archive.
     * @param file   The file or directory to add.
     * @param parent The parent directory path for the current file (used for recursive calls).
     * @throws IOException If an error occurs while adding the file to the archive.
     */
    protected void addFileToTar(TarArchiveOutputStream tarOut, File file, String parent) throws IOException {
        String entryName = parent + file.getName();
        TarArchiveEntry entry = new TarArchiveEntry(file, file.isDirectory() ? entryName + "/" : entryName);
        tarOut.putArchiveEntry(entry);

        if (file.isDirectory()) {
            tarOut.closeArchiveEntry();
            for (File child : Objects.requireNonNull(file.listFiles())) {
                addFileToTar(tarOut, child, entryName + "/");
            }
        } else {
            writeFileToTar(tarOut, file);
        }
    }

    /**
     * Writes a file's contents to the TAR archive.
     *
     * @param tarOut The {@link TarArchiveOutputStream} to write the file data.
     * @param file   The file to write to the archive.
     * @throws IOException If an error occurs while writing the file to the archive.
     */
    protected void writeFileToTar(TarArchiveOutputStream tarOut, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                tarOut.write(buffer, 0, len);
            }
        }
        tarOut.closeArchiveEntry();
    }

    /**
     * Extracts entries from the TAR archive input stream to the specified directory.
     *
     * @param tis     The {@link TarArchiveInputStream} to read from.
     * @param destDir The destination directory to extract files into.
     * @throws IOException If an error occurs while extracting the entries.
     */
    protected void extractEntries(TarArchiveInputStream tis, File destDir) throws IOException {
        TarArchiveEntry entry;
        while ((entry = tis.getNextTarEntry()) != null) {
            File outFile = new File(destDir, entry.getName());
            if (entry.isDirectory()) {
                outFile.mkdirs();
            } else {
                writeFileFromTar(tis, outFile);
            }
        }
    }

    /**
     * Writes a file from the TAR archive to the destination directory.
     *
     * @param tis     The {@link TarArchiveInputStream} to read data from.
     * @param outFile The output file to write to.
     * @throws IOException If an error occurs while writing the file.
     */
    protected void writeFileFromTar(TarArchiveInputStream tis, File outFile) throws IOException {
        File parent = outFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    /**
     * Gets the output directory name for the extracted TAR archive.
     *
     * @param file The TAR archive file.
     * @return The name of the output directory (without the .tar extension).
     */
    protected String getOutputDirectoryName(File file) {
        return file.getName().replace(".tar", "");
    }

    /**
     * Handles errors by showing an appropriate message and printing the stack trace.
     *
     * @param messageKey The message key for the error message.
     * @param e          The exception that was thrown.
     */
    protected void handleError(String messageKey, Exception e) {
        WindowService.openArchiveInfoWindow(messageKey);
        e.printStackTrace();
    }
}
