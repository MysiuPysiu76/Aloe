package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class TarGzArchiveHandler extends TarArchiveHandler {
    static void compress(List<File> files, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(new File(FilesOperations.getCurrentDirectory(), fileName + ".tar.gz"));
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GZIPOutputStream gzos = new GZIPOutputStream(bos);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzos)) {
            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            for (File file : files) {
                addFileToTar(tarOut, file, "");
            }
        } catch (IOException e) {
            WindowService.openArchiveInfoWindow("archive.compress.error");
            e.printStackTrace();
            return;
        }
        WindowService.openArchiveInfoWindow("archive.compress.success");
    }

    static void extract(File file) {
        File dest = new File(FilesOperations.getCurrentDirectory(), file.getName().replace(".tar.gz", ""));
        if (!dest.exists()) {
            dest.mkdirs();
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             GZIPInputStream gzis = new GZIPInputStream(bis);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                File outputFile = new File(dest, entry.getName());

                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    File parent = outputFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (OutputStream out = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = tarIn.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error decompressing tar.gz file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}