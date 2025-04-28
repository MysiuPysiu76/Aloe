package com.example.aloe.files.archive;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * The {@code ArchiveParameters} class encapsulates the parameters required for
 * compression and decompression operations on archives.
 *
 * <p>It holds information such as the list of files to process, the type of archive,
 * the output file name, whether compression is enabled, and an optional password
 * for secure archives.</p>
 *
 * <h2>Example usage:</h2>
 * <pre>{@code
 * List<File> files = List.of(new File("file1.txt"), new File("file2.txt"));
 * ArchiveParameters params = new ArchiveParameters(files, ArchiveType.ZIP, "output.zip", "securePassword");
 * }</pre>
 *
 * @since 0.9.5
 */
public class ArchiveParameters {

    private List<File> files;
    private ArchiveType archiveType;
    private String fileName;
    private String password;

    /**
     * Constructs an instance of {@code ArchiveParameters} with the specified details.
     *
     * @param files       the list of files to include in the archive. May be {@code null} for decompression.
     * @param type        the type of archive (e.g., ZIP, TAR).
     * @param fileName    the name of the output archive file.
     * @param password    an optional password for securing the archive. May be {@code null}.
     */
    public ArchiveParameters(List<File> files, ArchiveType type, String fileName, String password) {
        this.fileName = fileName;
        this.password = password;
        this.files = files;
        this.archiveType = type;
    }

    /**
     * Constructs an instance of {@code ArchiveParameters} without a password.
     *
     * @param files       the list of files to include in the archive. May be {@code null} for decompression.
     * @param type        the type of archive (e.g., ZIP, TAR).
     * @param fileName    the name of the output archive file.
     * @param useCompress {@code true} if compression should be used; {@code false} otherwise.
     */
    public ArchiveParameters(List<File> files, ArchiveType type, String fileName, boolean useCompress) {
        this.fileName = fileName;
        this.password = null;
        this.files = files;
        this.archiveType = type;
    }

    /**
     * Returns the list of files associated with this archive operation.
     *
     * @return the list of files, or {@code null} if not applicable.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Sets the list of files to include in the archive.
     *
     * @param files the list of files to include.
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * Returns the archive type (e.g., ZIP, TAR).
     *
     * @return the archive type.
     */
    public ArchiveType getArchiveType() {
        return archiveType;
    }

    /**
     * Sets the archive type (e.g., ZIP, TAR).
     *
     * @param archiveType the archive type to set.
     */
    public void setArchiveType(ArchiveType archiveType) {
        this.archiveType = archiveType;
    }

    /**
     * Returns the name of the output archive file.
     *
     * @return the archive file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the output archive file.
     *
     * @param fileName the archive file name to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the password for securing the archive, if applicable.
     *
     * @return the password, or {@code null} if no password is set.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for securing the archive.
     *
     * @param password the password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Provides a string representation of the {@code ArchiveParameters}.
     *
     * @return a string describing the parameters.
     */
    @Override
    public String toString() {
        return "ArchiveParameters{" + "files=" + files + ", archiveType=" + archiveType + ", fileName='" + fileName + '\'' + ", password='" + password + '\'' + '}';
    }

    /**
     * Checks if this {@code ArchiveParameters} instance is equal to another object.
     *
     * @param o the object to compare with.
     * @return {@code true} if the objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArchiveParameters that = (ArchiveParameters) o;
        return Objects.equals(files, that.files) && Objects.equals(fileName, that.fileName) && Objects.equals(password, that.password);
    }

    /**
     * Computes the hash code for this {@code ArchiveParameters} instance.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(files, fileName, password);
    }
}
