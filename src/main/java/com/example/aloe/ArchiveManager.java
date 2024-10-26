package com.example.aloe;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;

import java.io.File;

class ArchiveManager {
    public static void extract(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(FilesOperations.getCurrentDirectory().toPath().toString() + "/" + zipFile.getFile().getName().replace(".zip", ""));
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void compress(File file, String fileName, boolean compress) {
        try {
            ZipFile zipFile = new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName));
            ZipParameters parameters = new ZipParameters();
            if (!compress) {
                parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
            }
            if (file.isDirectory()) {
                zipFile.addFolder(file, parameters);
            } else {
                zipFile.addFile(file, parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}