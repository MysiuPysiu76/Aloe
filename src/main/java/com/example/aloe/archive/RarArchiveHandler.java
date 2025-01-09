package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The {@code RarArchiveHandler} class provides utility methods for handling the extraction of RAR archive files.
 * <p>
 * This class uses the {@link com.github.junrar.Archive} library to process and extract files from a given RAR archive.
 * It handles the creation of directories and files based on the structure of the archive and extracts their contents.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * {@code
 * File rarFile = new File("path/to/archive.rar");
 * RarArchiveHandler.extract(rarFile);
 * }
 * </pre>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *     <li>{@link com.example.aloe.FilesOperations} - For file path manipulation and directory management.</li>
 *     <li>{@link com.github.junrar.Archive} - To interact with RAR archives.</li>
 * </ul>
 *
 * <p><b>Note:</b> This class does not have a public constructor and is intended to be used statically.</p>
 *
 * @since 0.8.8
 */
class RarArchiveHandler {

    /**
     * Extracts the contents of the given RAR archive file into a directory.
     * <p>
     * The extracted contents are placed in a new directory created in the current working directory.
     * The name of the directory is derived from the name of the RAR file (without the `.rar` extension).
     * </p>
     *
     * @param file the RAR file to be extracted. Must not be {@code null}.
     * @throws RuntimeException if an {@link IOException} or {@link com.github.junrar.exception.RarException} occurs during extraction.
     */
    static void extract(File file) {
        File output = new File(FilesOperations.getCurrentDirectory(), file.getName().replace(".rar", ""));
        if (!output.exists()) {
            output.mkdirs();
        }
        try (Archive archive = new Archive(file)) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                File outputFile = new File(output, fileHeader.getFileNameString().trim());
                if (fileHeader.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        archive.extractFile(fileHeader, fos);
                    }
                }
            }
        } catch (RarException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
