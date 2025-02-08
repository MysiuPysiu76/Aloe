package com.example.aloe.archive;

import com.example.aloe.FilesOperations;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

class JarArchive implements Archive {

    @Override
    public void compress(ArchiveParameters parameters) {
        try (JarArchiveOutputStream out = new JarArchiveOutputStream(new FileOutputStream(FilesOperations.getCurrentDirectory().toString() + "/" + parameters.getFileName()))){
            for (File file : parameters.getFiles()) {
                addToArchive(out, file, ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Compression failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void decompress(File file) {
        String fileNameWithoutExtension = file.getName().replace(".jar", "");
        File destinationDirectory = new File(FilesOperations.getCurrentDirectory(), fileNameWithoutExtension);
        if (!destinationDirectory.exists()) {
            if (!destinationDirectory.mkdirs()) {
                throw new RuntimeException("Could not create destination directory: " + destinationDirectory.getPath());
            }
        }

        try (JarArchiveInputStream jin = new JarArchiveInputStream(new FileInputStream(file))){
            JarArchiveEntry entry;
            while ((entry = jin.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(destinationDirectory, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new RuntimeException("Could not create directory: " + parent.getPath());
                }
                IOUtils.copy(jin, new FileOutputStream(curfile));
            }
        } catch (Exception e) {
            throw new RuntimeException("Decompression failed: " + e.getMessage(), e);
        }
    }

    private void addToArchive(JarArchiveOutputStream out, File file, String dir) throws IOException {
        String name = dir + File.separator + file.getName();
        if (file.isFile()) {
            JarArchiveEntry entry = new JarArchiveEntry(name);
            out.putArchiveEntry(entry);
            entry.setSize(file.length());
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, out);
            }
            out.closeArchiveEntry();
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addToArchive(out, child, name);
                }
            }
        } else {
            System.out.println(file.getName() + " is not supported");
        }
    }
}
