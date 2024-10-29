package com.example.aloe;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
                WindowService.openArchiveInfoWindow("archive.extract.wrong-password");
            }
            FilesOperations.deleteFile(new File(FilesOperations.getCurrentDirectory().toPath().toString() + "/" + zipFile.getFile().getName().replace(".zip", "")));
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("archive.extract.success");
    }

    public static void compress(File file, String fileName, boolean useCompress, boolean usePassword, String password, ArchiveType archiveType) {
        switch (archiveType) {
            case ZIP -> ArchiveManager.compressToZip(file, fileName, useCompress, usePassword, password);
            case TAR -> ArchiveManager.compressToTar(file, fileName);
        }
    }

    private static void compressToZip(File file, String fileName, boolean useCompress, boolean usePassword, String password) {
        try {
            ZipFile zipFile = new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName + ".zip"));
            ZipParameters parameters = new ZipParameters();
            if (!useCompress) {
                parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
            }
            if(usePassword) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zipFile = new ZipFile(new File(FilesOperations.getCurrentDirectory(), fileName + ".zip"), password.toCharArray());
            }
            if (file.isDirectory()) {
                zipFile.addFolder(file, parameters);
            } else {
                zipFile.addFile(file, parameters);
            }
        } catch (Exception e) {
            WindowService.openArchiveInfoWindow("archive.compress.error");
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("archive.compress.success");
    }

    private static void compressToTar(File sourceFile, String fileName) {
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("Source file or directory does not exist: " + fileName);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(FilesOperations.getCurrentDirectory(), fileName + ".tar"));
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {
            addFileToTar(tarOut, sourceFile, "");
        } catch (IOException e) {
            WindowService.openArchiveInfoWindow("archive.compress.error");
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("archive.compress.success");
    }

    private static void addFileToTar(TarArchiveOutputStream tarOut, File file, String parent) throws IOException {
        String entryName = parent + file.getName();
        if (file.isDirectory()) {
            TarArchiveEntry entry = new TarArchiveEntry(file, entryName + "/");
            tarOut.putArchiveEntry(entry);
            tarOut.closeArchiveEntry();
            for (File child : file.listFiles()) {
                addFileToTar(tarOut, child, entryName + "/");
            }
        } else {
            TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
            tarOut.putArchiveEntry(entry);
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    tarOut.write(buffer, 0, len);
                }
            }
            tarOut.closeArchiveEntry();
        }
    }
}