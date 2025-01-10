package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.List;

class ZipArchiveHandler {

    /**
     * Compresses a list of files or directories into a ZIP archive.
     *
     * @param files       the list of files or directories to compress.
     * @param fileName    the name of the output ZIP file (without the ".zip" extension).
     * @param useCompress whether to apply compression. If {@code false}, the ZIP file will use no compression.
     */
    static void compress(List<File> files, String fileName, boolean useCompress) {
        compress(files, fileName, useCompress, null);
    }

    /**
     * Compresses a list of files or directories into a password-protected ZIP archive.
     *
     * @param files       the list of files or directories to compress.
     * @param fileName    the name of the output ZIP file (without the ".zip" extension).
     * @param useCompress whether to apply compression. If {@code false}, the ZIP file will use no compression.
     * @param password    the password for the ZIP archive. If {@code null}, the archive will not be password-protected.
     */
    static void compress(List<File> files, String fileName, boolean useCompress, String password) {
        try {
            ZipFile zipFile = (password == null)
                    ? new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName + ".zip"))
                    : new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName + ".zip"), password.toCharArray());

            ZipParameters parameters = createZipParameters(useCompress, password != null);

            for (File file : files) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
            WindowService.openArchiveInfoWindow("window.archive.compress.success");
        } catch (ZipException e) {
            WindowService.openArchiveInfoWindow("window.archive.compress.error");
            e.printStackTrace();
        }
    }

    /**
     * Creates and configures a {@link ZipParameters} object based on the provided settings.
     *
     * @param useCompress whether to apply compression.
     * @param encrypt     whether to enable encryption.
     * @return a configured {@link ZipParameters} object.
     */
    private static ZipParameters createZipParameters(boolean useCompress, boolean encrypt) {
        ZipParameters parameters = new ZipParameters();
        if (!useCompress) {
            parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
        }
        if (encrypt) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        }
        return parameters;
    }

    /**
     * Extracts a ZIP archive to the current working directory.
     * <p>
     * If the archive is password-protected, the user is prompted to enter the password.
     * </p>
     *
     * @param file the ZIP archive file to extract.
     */
    static void extract(File file) {
        ZipFile zipFile = new ZipFile(file);
        try {
            if (zipFile.isEncrypted()) {
                String password = WindowService.openPasswordPromptWindow();
                if (password != null) {
                    zipFile.setPassword(password.toCharArray());
                } else {
                    return;
                }
            }
            zipFile.extractAll(FilesOperations.getCurrentDirectory().toPath() + "/" + zipFile.getFile().getName().replace(".zip", ""));
            WindowService.openArchiveInfoWindow("window.archive.extract.success");
        } catch (ZipException e) {
            handleExtractionError(zipFile, e);
        }
    }

    /**
     * Handles errors that occur during ZIP extraction.
     * <p>
     * Deletes partially extracted files and provides user feedback.
     * </p>
     *
     * @param zipFile the {@link ZipFile} being extracted.
     * @param e       the exception that occurred.
     */
    private static void handleExtractionError(ZipFile zipFile, ZipException e) {
        if ("Wrong password!".equals(e.getMessage())) {
            WindowService.openArchiveInfoWindow("window.archive.extract.wrong-password");
        }
        String extractionPath = FilesOperations.getCurrentDirectory().toPath() + "/" + zipFile.getFile().getName().replace(".zip", "");
        FilesOperations.deleteFile(new File(extractionPath));
        WindowService.openArchiveInfoWindow("window.archive.extract.error");
        e.printStackTrace();
    }
}