package com.example.aloe.archive;

public enum ArchiveType {

    ZIP(".zip"),
    TAR(".tar"),
    TARGZ(".tar.gz");

    private String extension;

    ArchiveType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return extension;
    }
}