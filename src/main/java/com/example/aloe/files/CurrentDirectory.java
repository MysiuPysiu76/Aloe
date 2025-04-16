package com.example.aloe.files;

import java.io.File;

public class CurrentDirectory {

    private static File currentDirectory = null;

    public static File get() {
        if (currentDirectory == null) {
            currentDirectory = new File(System.getProperty("user.home"));
        }
        return currentDirectory;
    }

    public static void set(File directory) {
        currentDirectory = directory;
    }
}