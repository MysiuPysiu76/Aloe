package com.example.aloe.archive;

/**
 * Represents different types of archive formats supported by the application.
 * <p>
 * Each archive type is associated with its file extension.
 * </p>
 * <h2>Example usage:</h2>
 * <pre>
 *     ArchiveType type = ArchiveType.ZIP;
 *     System.out.println(type.getExtension()); // Output: .zip
 * </pre>
 *
 * @since 0.8.5
 */
public enum ArchiveType {

    /**
     * Represents the ZIP archive format.
     */
    ZIP(".zip"),

    /**
     * Represents the SEVEN.ZIP archive format.
     */
    SEVEN_ZIP(".7z"),

    /**
     * Represents the RAR archive format.
     */
    RAR(".rar"),

    /**
     * Represents the TAR archive format.
     */
    TAR(".tar"),

    /**
     * Represents the TAR.GZ (compressed TAR) archive format.
     */
    TAR_GZ(".tar.gz");

    private final String extension;

    /**
     * Constructs an {@code ArchiveType} with the given file extension.
     *
     * @param extension the file extension associated with the archive type
     */
    ArchiveType(String extension) {
        this.extension = extension;
    }

    /**
     * Retrieves the file extension associated with the archive type.
     *
     * @return the file extension as a {@code String}
     */
    public String getExtension() {
        return extension;
    }


    public static ArchiveType fromString(String value) {
        for (ArchiveType enumValue : ArchiveType.values()) {
            if (enumValue.getExtension().equals(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Nie znaleziono enuma dla wartości: " + value);
    }


    /**
     * Returns the file extension as the string representation of the archive type.
     *
     * @return the file extension as a {@code String}
     */
    @Override
    public String toString() {
        return extension;
    }
}