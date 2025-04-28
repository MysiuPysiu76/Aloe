package com.example.aloe.files;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * The {@code Checksum} class provides functionality for generating and verifying
 * checksums (hashes) of files using various hashing algorithms such as MD5, SHA-1, SHA-256, etc.
 *
 * <p>This class can be used to verify file integrity or to compare file contents.
 *
 * <p>Example usage:
 * <pre>{@code
 * File file = new File("example.txt");
 * Checksum checksum = new Checksum(file);
 * String hash = checksum.generateChecksum("SHA-256");
 * boolean isValid = checksum.verifyChecksum("SHA-256", "expected_hash_value");
 * }</pre>
 *
 * @since 1.7.6
 */
public class Checksum {

    /** The file for which the checksum will be generated or verified. */
    private final File file;

    /**
     * Constructs a new {@code Checksum} instance for the specified file.
     *
     * @param file the file to compute checksums for
     */
    public Checksum(File file) {
        this.file = file;
    }

    /**
     * Converts an array of bytes to a hexadecimal {@code String}.
     *
     * @param bytes the byte array to convert
     * @return a {@code String} containing the hexadecimal representation of the bytes
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Generates a checksum for the file using the specified algorithm.
     *
     * @param algorithm the name of the algorithm to use (e.g., "MD5", "SHA-1", "SHA-256")
     * @return the checksum value as a hexadecimal {@code String}
     * @throws RuntimeException if an error occurs while computing the checksum
     */
    public String generateChecksum(String algorithm) {
        try (FileInputStream fis = new FileInputStream(file);
             DigestInputStream dis = new DigestInputStream(fis, MessageDigest.getInstance(algorithm))) {

            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
            return bytesToHex(dis.getMessageDigest().digest());
        } catch (Exception e) {
            throw new RuntimeException("Error calculating checksum for file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Verifies that the checksum of the file matches the expected value.
     *
     * @param algorithm the name of the algorithm used to compute the checksum (e.g., "SHA-256")
     * @param expectedHash the expected checksum value in hexadecimal format
     * @return {@code true} if the computed checksum matches the expected value; {@code false} otherwise
     */
    public boolean verifyChecksum(String algorithm, String expectedHash) {
        return generateChecksum(algorithm).equalsIgnoreCase(expectedHash);
    }
}
