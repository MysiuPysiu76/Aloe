package com.example.aloe.archive;

import java.io.File;

/**
 * The {@code Archive} interface defines the contract for handling compression and decompression operations
 * for various archive formats. Implementations of this interface should provide specific logic for
 * supported formats such as ZIP, TAR, TAR.GZ or RAR.
 *
 * <p>Classes implementing this interface allow the user to:</p>
 * <ul>
 *     <li>Compress a list of files into a single archive.</li>
 *     <li>Decompress an existing archive into a directory.</li>
 * </ul>
 *
 * <h2>Example usage:</h2>
 * <pre>{@code
 *     ArchiveParameters parameters = new ArchiveParameters(List.of(new File("file1.txt"), new File("file2.txt")), ArchiveType.ZIP, "archive.zip", true);
 *     new ZipArchive().compress(parameters);
 *
 *     new TarArchive().decompress(new File("archive.tar"));
 * }</pre>
 *
 * @since 0.9.4
 */
interface Archive {

    /**
     * Compresses a set of files into an archive.
     *
     * @param parameters the {@link ArchiveParameters} object containing details about the files to compress and the desired output archive name.
     * @throws RuntimeException if an I/O error or unsupported operation occurs during compression.
     */
    void compress(ArchiveParameters parameters);

    /**
     * Decompresses an archive into a specified directory.
     *
     * @param file the archive file to decompress. Must not be {@code null}.
     * @throws RuntimeException if an I/O error or unsupported operation occurs during decompression.
     */
    void decompress(File file);
}
