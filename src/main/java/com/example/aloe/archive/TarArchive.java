package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;

public class TarArchive implements Archive {

    @Override
    public void compress(ArchiveParameters parameters) {
        File outputFile = new File(FilesOperations.getCurrentDirectory(), parameters.getFileName() + ".tar");
        try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new FileOutputStream(outputFile))) {
            for (File file : parameters.getFiles()) {
                addFileToTar(tarOut, file, "");
            }
            WindowService.openArchiveInfoWindow("window.archive.compress.success");
        } catch (IOException e) {
            handleError("window.archive.compress.error", e);
        }
    }

    @Override
    public void decompress(File file) {
        File destDir = new File(FilesOperations.getCurrentDirectory(), getOutputDirectoryName(file));
        if (!destDir.exists() && !destDir.mkdirs()) {
            handleError("window.archive.extract.error", new IOException("Failed to create destination directory."));
            return;
        }

        try (TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(file))) {
            extractEntries(tis, destDir);
            WindowService.openArchiveInfoWindow("window.archive.extract.success");
        } catch (IOException e) {
            handleError("window.archive.extract.error", e);
        }
    }

    protected void addFileToTar(TarArchiveOutputStream tarOut, File file, String parent) throws IOException {
        String entryName = parent + file.getName();
        TarArchiveEntry entry = new TarArchiveEntry(file, file.isDirectory() ? entryName + "/" : entryName);
        tarOut.putArchiveEntry(entry);

        if (file.isDirectory()) {
            tarOut.closeArchiveEntry();
            for (File child : file.listFiles()) {
                addFileToTar(tarOut, child, entryName + "/");
            }
        } else {
            writeFileToTar(tarOut, file);
        }
    }

    protected void writeFileToTar(TarArchiveOutputStream tarOut, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                tarOut.write(buffer, 0, len);
            }
        }
        tarOut.closeArchiveEntry();
    }

    protected void extractEntries(TarArchiveInputStream tis, File destDir) throws IOException {
        TarArchiveEntry entry;
        while ((entry = tis.getNextTarEntry()) != null) {
            File outFile = new File(destDir, entry.getName());
            if (entry.isDirectory()) {
                outFile.mkdirs();
            } else {
                writeFileFromTar(tis, outFile);
            }
        }
    }

    protected void writeFileFromTar(TarArchiveInputStream tis, File outFile) throws IOException {
        File parent = outFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    protected String getOutputDirectoryName(File file) {
        return file.getName().replace(".tar", "");
    }

    protected void handleError(String messageKey, Exception e) {
        WindowService.openArchiveInfoWindow(messageKey);
        e.printStackTrace();
    }
}
