package com.example.aloe.files;

import com.example.aloe.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryHistory {

    private static final List<File> files = new ArrayList<>();
    private static int position = -1;

    public static void addDirectory(File file) {
        files.add(file);
        position = files.size() - 1;
    }

    public static void loadPreviousDirectory() {
        if (position > 0) {
            position--;
            new Main().loadDirectoryContents(files.get(position), false);
        }
    }

    public static void loadNextDirectory() {
        if (position < files.size() - 1) {
            position++;
            new Main().loadDirectoryContents(files.get(position), false);
        }
    }
}