package com.example.aloe;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

class ArchiveManager {
    public static void extract(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(FilesOperations.getCurrentDirectory().toPath().toString());
        } catch (ZipException e) {
            e.printStackTrace();
        }

    }
}