package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.List;

class TarArchiveHandler {
    static void compress(List<File> files, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(new File(FilesOperations.getCurrentDirectory(), fileName + ".tar"));
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {
            for (File file : files) {
                addFileToTar(tarOut, file, "");
            }
        } catch (IOException e) {
            WindowService.openArchiveInfoWindow("window.archive.compress.error");
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("window.archive.compress.success");
    }

    protected static void addFileToTar(TarArchiveOutputStream tarOut, File file, String parent) throws IOException {
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

    static void extract(File file) {
        File dest = new File(FilesOperations.getCurrentDirectory(), file.getName().replace(".tar", ""));
        if (!dest.exists()) dest.mkdirs();
        try (FileInputStream fis = new FileInputStream(file);
             TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                File outFile = new File(dest, entry.getName().replace(".tar", ""));
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(outFile);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = tis.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            WindowService.openArchiveInfoWindow("window.archive.extract.error");
            e.printStackTrace();
        }
        WindowService.openArchiveInfoWindow("window.archive.extract.success");
    }
}