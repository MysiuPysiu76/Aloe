package com.example.aloe;

import com.example.aloe.files.tasks.FileCopyTask;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.util.List;

public class ClipboardManager {

    public static void copyFilesToClipboard(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putFiles(files);
        clipboard.setContent(content);
        FileCopyTask.setCut(false);
    }

    public static void cutFilesToClipboard(List<File> files) {
        copyFilesToClipboard(files);
        FileCopyTask.setCut(true);
    }
}