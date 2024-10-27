package com.example.aloe;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;

public class ArchiveManager {
    private static String password = "";

    private static String getPassword() {
        return password;
    }

    public static void setPassword(String pass) {
        password = pass;
    }

    private static void clearPassword() {
        password = null;
    }

    public static void extract(File file) {
        ZipFile zipFile = new ZipFile(file);
        try {
            if (zipFile.isEncrypted()) {
                WindowService.openPasswordPromptWindow();
                zipFile.setPassword(ArchiveManager.getPassword().toCharArray());
                ArchiveManager.clearPassword();
            }
            zipFile.extractAll(FilesOperations.getCurrentDirectory().toPath().toString() + "/" + zipFile.getFile().getName().replace(".zip", ""));
        } catch (ZipException e) {
            if (e.getMessage().equals("Wrong password!")) {
                WindowService.openWrongPasswordWindow();
            }
            FilesOperations.deleteFile(new File(FilesOperations.getCurrentDirectory().toPath().toString() + "/" + zipFile.getFile().getName().replace(".zip", "")));
            e.printStackTrace();
        }
    }

    public static void compress(File file, String fileName, boolean useCompress, boolean usePassword, String password) {
        try {
            ZipFile zipFile = new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName));
            ZipParameters parameters = new ZipParameters();
            if (!useCompress) {
                parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
            }
            if(usePassword) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zipFile = new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName), password.toCharArray());
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