package com.example.aloe.files;

import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.files.tasks.FileOpenerTask;

import java.io.File;

public class FilesOpener {

    public static void open(File file) {
        if (file.isFile()) {
            new FileOpenerTask(file, true);
        } else {
            FilesLoader.load(file);
        }
    }
}