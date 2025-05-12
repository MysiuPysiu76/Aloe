package com.example.aloe.files.archive;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.InfoWindow;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Implementation of the {@link Archive} interface for handling JAR archive operations.
 * <p>
 * This class provides methods for compressing a set of files into a JAR archive
 * and decompressing an existing JAR file into a directory.
 * </p>
 * <p>Some fragments of code are based on examples from:
 * <a href="https://memorynotfound.com/compress-decompress-java-jar-file-apache-compress/">here</a></p>
 * <h2>Example usage:</h2>
 * <pre>
 *     JarArchive jarArchive = new JarArchive();
 *     ArchiveParameters params = new ArchiveParameters("example.jar", List.of(new File("file1.txt"), new File("file2.txt")));
 *     jarArchive.compress(params);
 *
 *     File jarFile = new File("example.jar");
 *     jarArchive.decompress(jarFile);
 * </pre>
 *
 * @since 1.0.7
 */
class JarArchive implements Archive {

    /**
     * Compresses the specified files into a JAR archive.
     *
     * @param parameters The parameters containing the list of files to be compressed
     *                   and the name of the output archive.
     * @throws RuntimeException if compression fails due to an I/O error.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        try (JarArchiveOutputStream out = new JarArchiveOutputStream(new FileOutputStream(CurrentDirectory.get().toString() + "/" + parameters.getFileName()))) {
            for (File file : parameters.getFiles()) {
                addToArchive(out, file, ".");
            }
        } catch (Exception e) {
            new InfoWindow(Translator.translate("window.archive.extract.error"), null);
            throw new RuntimeException("Compression failed: " + e.getMessage(), e);
        }
        new InfoWindow(Translator.translate("window.archive.compress.success"), null);
    }

    /**
     * Decompresses a JAR archive into a directory with the same name as the file (without the .jar extension).
     *
     * @param file The JAR archive file to be decompressed.
     * @throws RuntimeException if decompression fails due to an I/O error or directory creation failure.
     */
    @Override
    public void decompress(File file) {
        String fileNameWithoutExtension = file.getName().replace(".jar", "");
        File destinationDirectory = new File(CurrentDirectory.get(), fileNameWithoutExtension);
        if (!destinationDirectory.exists()) {
            if (!destinationDirectory.mkdirs()) {
                throw new RuntimeException("Could not create destination directory: " + destinationDirectory.getPath());
            }
        }

        try (JarArchiveInputStream jin = new JarArchiveInputStream(new FileInputStream(file))) {
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
            new InfoWindow(Translator.translate("window.archive.extract.error"), null);
            throw new RuntimeException("Decompression failed: " + e.getMessage(), e);
        }
        new InfoWindow(Translator.translate("window.archive.extract.success"), null);
    }

    /**
     * Adds a file or directory to the JAR archive output stream.
     * <p>
     * If the file is a directory, its contents are recursively added to the archive.
     * </p>
     *
     * @param out The JAR archive output stream.
     * @param file The file or directory to be added to the archive.
     * @param dir The relative path inside the archive.
     * @throws IOException if an I/O error occurs while writing to the archive.
     */
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
