package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.window.interior.PasswordPromptWindow;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the {@link Archive} interface for handling ZIP archives.
 * Provides methods to compress and decompress files using the Zip4j library.
 *
 * <p>
 * <h2>Example usage:</h2>
 * <pre>
 *     ArchiveParameters parameters = new ArchiveParameters(files, "output", "password123");
 *     new ZipArchive().compress(parameters);
 *
 *     File archive = new File("output.zip");
 *     new ZipArchive().decompress(archive);
 * </pre>
 * </p>
 *
 * @see SevenZipArchive
 * @since 0.9.3
 */
class ZipArchive implements Archive {

    /**
     * Compresses a list of files into a ZIP archive.
     *
     * @param parameters the parameters for the archive, including file list, compression settings, and password.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        try {
            ZipFile zipFile = createZipFileInstance(parameters);
            ZipParameters zipParameters = createZipParameters(parameters.getPassword() != null);

            for (File file : parameters.getFiles()) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, zipParameters);
                } else {
                    zipFile.addFile(file, zipParameters);
                }
            }
            WindowService.openArchiveInfoWindow("window.archive.compress.success");
        } catch (ZipException e) {
            handleCompressionError(e);
        }
    }

    /**
     * Extracts a ZIP archive to the output directory.
     * <p>
     * If the archive is password-protected, the user is prompted to provide the password.
     * </p>
     *
     * @param file the ZIP archive to extract.
     */
    @Override
    public void decompress(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Path outputPath = getOutputPath(file);

            if (zipFile.isEncrypted()) {
                CompletableFuture<Void> completableFuture = new CompletableFuture<>();

                StringBuilder password = new StringBuilder();
                new PasswordPromptWindow(completableFuture, password);
                completableFuture.thenRun(() -> {
                    zipFile.setPassword(password.toString().toCharArray());
                    System.out.println(password);
                    try {
                        zipFile.extractAll(outputPath.toString());
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                    WindowService.openArchiveInfoWindow("window.archive.extract.success");
                });
            } else {
                zipFile.extractAll(outputPath.toString());
            }
        } catch (ZipException e) {
            handleDecompressionError(file, e);
        }
    }

    /**
     * Creates an instance of {@link ZipFile} based on the provided parameters.
     *
     * @param parameters the parameters for creating the archive.
     * @return a configured {@link ZipFile} instance.
     */
    private ZipFile createZipFileInstance(ArchiveParameters parameters) {
        File zipFile = new File(FilesOperations.getCurrentDirectory(), parameters.getFileName());
        return (parameters.getPassword() == null) ? new ZipFile(zipFile) : new ZipFile(zipFile, parameters.getPassword().toCharArray());
    }

    /**
     * Configures and returns ZIP parameters for the archive.
     *
     * @param encrypt     whether to enable encryption.
     * @return a configured {@link ZipParameters} object.
     */
    private ZipParameters createZipParameters(boolean encrypt) {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
        if (encrypt) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        }
        return parameters;
    }

    /**
     * Handles errors that occur during ZIP compression.
     *
     * @param e the exception that occurred.
     */
    private void handleCompressionError(ZipException e) {
        WindowService.openArchiveInfoWindow("window.archive.compress.error");
        e.printStackTrace();
    }

    /**
     * Handles errors that occur during ZIP extraction.
     * <p>
     * Deletes partially extracted files and provides user feedback.
     * </p>
     *
     * @param file the {@link ZipFile} being extracted.
     * @param e    the exception that occurred.
     */
    private void handleDecompressionError(File file, ZipException e) {
        if ("Wrong password!".equals(e.getMessage())) {
            WindowService.openArchiveInfoWindow("window.archive.extract.wrong-password");
        }
        String extractionPath = FilesOperations.getCurrentDirectory().toPath() + "/" + file.getName().replace(".zip", "");
        new FileDeleteTask(new File(extractionPath), true);
        WindowService.openArchiveInfoWindow("window.archive.extract.error");
        e.printStackTrace();
    }

    /**
     * Determines the output path for the extracted files based on the archive name.
     *
     * @param file the ZIP archive.
     * @return the path where the files will be extracted.
     */
    private Path getOutputPath(File file) {
        return FilesOperations.getCurrentDirectory().toPath().resolve(file.getName().replace(".zip", ""));
    }
}