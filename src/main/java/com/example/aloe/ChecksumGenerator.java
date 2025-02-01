package com.example.aloe;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

class ChecksumGenerator {

    public static String generateChecksum(File file, String algorithm) {
        try (FileInputStream fis = new FileInputStream(file);
             DigestInputStream dis = new DigestInputStream(fis, MessageDigest.getInstance(algorithm))) {

            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
            return bytesToHex(dis.getMessageDigest().digest());
        } catch (Exception e) {
            throw new RuntimeException("Error calculating checksum for file: " + file.getAbsolutePath(), e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
