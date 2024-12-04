package com.example.aloe.archive;

import com.example.aloe.FilesOperations;

import java.io.File;
import java.util.List;

public class ArchiveHandler {
    public static void compress(List<File> files, String fileName, boolean useCompress, boolean usePassword, String password, ArchiveType archiveType) {
        switch (archiveType) {
            case ZIP -> ZipArchiveHandler.compress(files, fileName, useCompress, usePassword, password);
            case TAR -> TarArchiveHandler.compress(files, fileName);
            case TARGZ -> TarGzArchiveHandler.compress(files, fileName);
        }
    }

   public static void extract(File file) {
        switch (FilesOperations.getExtension(file)) {
            case "zip" -> ZipArchiveHandler.extract(file);
            case "tar" -> TarArchiveHandler.extract(file);
            case "tar.gz" -> TarGzArchiveHandler.extract(file);
        }
    }
}