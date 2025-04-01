package com.example.aloe;

import com.example.aloe.files.tasks.FileCopyTask;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.List;

/**
 * The {@code ClipboardManager} class provides utility methods for interacting with the system clipboard.
 * It allows copying text and files to the clipboard, checking if the clipboard is empty, and cutting files.
 *
 * @since 1.4.5
 */
public class ClipboardManager {

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text the text to be copied to the clipboard
     */
    public static void copyTextToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * Checks whether the clipboard is empty.
     *
     * @return {@code true} if the clipboard contains no files, {@code false} otherwise
     */
    public static boolean isClipboardEmpty() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        List<File> filesFromClipboard = clipboard.getFiles();
        return filesFromClipboard == null || filesFromClipboard.isEmpty();
    }

    /**
     * Copies a list of files to the system clipboard.
     *
     * @param files the list of files to be copied to the clipboard
     */
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

    /**
     * Cuts a list of files to the system clipboard. This method first copies the files
     * and then marks them as cut.
     *
     * @param files the list of files to be cut to the clipboard
     */
    public static void cutFilesToClipboard(List<File> files) {
        copyFilesToClipboard(files);
        FileCopyTask.setCut(true);
    }
}