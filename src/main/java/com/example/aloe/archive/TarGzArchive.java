package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class TarGzArchive extends TarArchive {
    @Override
    public void compress(ArchiveParameters parameters) {
        File outputFile = new File(FilesOperations.getCurrentDirectory(), parameters.getFileName() + ".tar.gz");
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GZIPOutputStream gzos = new GZIPOutputStream(bos);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzos)) {
            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

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

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             GZIPInputStream gzis = new GZIPInputStream(bis);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzis)) {
            extractEntries(tarIn, destDir);
            WindowService.openArchiveInfoWindow("window.archive.extract.success");
        } catch (IOException e) {
            handleError("window.archive.extract.error", e);
        }
    }

    @Override
    protected String getOutputDirectoryName(File file) {
        return file.getName().replace(".tar.gz", "");
    }
}