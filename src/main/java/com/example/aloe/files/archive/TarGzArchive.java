package com.example.aloe.files.archive;

import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.WindowService;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The {@code TarGzArchive} class extends {@link TarArchive} and provides functionality
 * for compressing and decompressing files in the TAR.GZ format (TAR archive compressed with GZIP).
 * <p>
 * This class adds GZIP compression to the standard TAR format, using the {@link GZIPOutputStream}
 * for compression and {@link GZIPInputStream} for decompression, while still relying on
 * Apache Commons Compress for handling the TAR archive.
 * </p>
 *
 * @see TarArchive
 * @see TarArchiveInputStream
 * @see TarArchiveOutputStream
 * @see GZIPInputStream
 * @see GZIPOutputStream
 * @since 0.9.7
 */
class TarGzArchive extends TarArchive {

    /**
     * Compresses the files specified in the parameters into a TAR.GZ archive.
     * Creates an archive file with the name specified in {@link ArchiveParameters} and the .tar.gz extension.
     * <p>
     * Each file from the {@code parameters.getFiles()} list is added to the archive,
     * and the resulting archive is compressed using GZIP.
     * </p>
     *
     * @param parameters The compression parameters, including the list of files to compress and the archive file name.
     */
    @Override
    public void compress(ArchiveParameters parameters) {
        File outputFile = new File(CurrentDirectory.get(), parameters.getFileName());
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

    /**
     * Decompresses a TAR.GZ archive file into a directory in the current working directory.
     * <p>
     * If the destination directory does not exist, it will be created. The files and directories
     * from the archive are extracted into this directory after first decompressing the GZIP stream.
     * </p>
     *
     * @param file The TAR.GZ archive file to decompress.
     */
    @Override
    public void decompress(File file) {
        File destDir = new File(CurrentDirectory.get(), getOutputDirectoryName(file));
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

    /**
     * Returns the output directory name for the extracted TAR.GZ archive.
     * <p>
     * This method removes the ".tar.gz" extension from the archive's file name and returns the base name.
     * </p>
     *
     * @param file The TAR.GZ archive file.
     * @return The name of the output directory (without the .tar.gz extension).
     */
    @Override
    protected String getOutputDirectoryName(File file) {
        return file.getName().replace(".tar.gz", "");
    }
}