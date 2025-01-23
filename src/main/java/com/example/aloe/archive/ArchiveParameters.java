package com.example.aloe.archive;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ArchiveParameters {

    private List<File> files;
    private ArchiveType archiveType;
    private String fileName;
    private boolean useCompress;
    private String password;

    public ArchiveParameters(List<File> files, ArchiveType type, String fileName, boolean useCompress, String password) {
        this.fileName = fileName;
        this.useCompress = useCompress;
        this.password = password;
        this.files = files;
        this.archiveType = type;
    }

    public ArchiveParameters(List<File> files, ArchiveType type, String fileName, boolean useCompress) {
        this.fileName = fileName;
        this.useCompress = useCompress;
        this.password = null;
        this.files = files;
        this.archiveType = type;
    }

    @Override
    public String toString() {
        return "ArchiveParameters{" +
                "files=" + files +
                ", fileName='" + fileName + '\'' +
                ", useCompress=" + useCompress +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArchiveParameters that = (ArchiveParameters) o;
        return useCompress == that.useCompress && Objects.equals(files, that.files) && Objects.equals(fileName, that.fileName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files, fileName, useCompress, password);
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public ArchiveType getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(ArchiveType archiveType) {
        this.archiveType = archiveType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isUseCompress() {
        return useCompress;
    }

    public void setUseCompress(boolean useCompress) {
        this.useCompress = useCompress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}