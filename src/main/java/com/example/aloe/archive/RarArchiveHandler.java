package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class RarArchiveHandler {
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
