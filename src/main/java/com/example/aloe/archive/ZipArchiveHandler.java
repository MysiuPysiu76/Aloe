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
    static void compress(List<File> files, String fileName, boolean useCompress, boolean usePassword, String password) {
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

            for (File file : files) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
        } catch (ZipException e) {
            WindowService.openArchiveInfoWindow("archive.compress.error");
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("archive.compress.success");
    }

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
}