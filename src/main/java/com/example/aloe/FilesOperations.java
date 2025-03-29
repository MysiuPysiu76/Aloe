package com.example.aloe;

import javafx.scene.input.Clipboard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class FilesOperations {

    private static File currentDirectory = null;

    public static File getCurrentDirectory() {
        if (currentDirectory == null) {
            FilesOperations.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        return currentDirectory;
    }

    public static void setCurrentDirectory(File directory) {
        currentDirectory = directory;
    }

    public static boolean isClipboardEmpty() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        List<File> filesFromClipboard = clipboard.getFiles();
        return filesFromClipboard == null || filesFromClipboard.isEmpty();
    }

    static void copyFileToDestination(File source, File destination, boolean replaceExisting) throws IOException {
        if (destination.exists() && !replaceExisting) {
            FileOperation operation = new FileOperation(FileOperation.OperationType.COPY, source, destination);
            if (FileOperation.addOperationToQueue(operation)) {
                System.out.println(WindowService.addFileDecisionAskToExistFileWindow(operation));
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static void copyDirectoryToDestination(File source, File destination, boolean replaceExisting, boolean combine) throws IOException {
        if (destination.exists()) {
            if (replaceExisting) {
                destination.mkdir();
                pasteFile(source, destination);
            } else {
                if (combine) {
                    pasteFile(source, destination);
                } else {
                    FileOperation operation = new FileOperation(FileOperation.OperationType.COPY, source, destination);
                    if (FileOperation.addOperationToQueue(operation)) {
                        WindowService.addDirectoryDecisionAskToExistFileWindow(operation);
                    }
                }
            }
        } else {
            destination.mkdir();
            pasteFile(source, destination);
        }
    }

    private static void pasteFile(File source, File destination) throws IOException {
        for (String file : Objects.requireNonNull(source.list())) {
            File sourceFile = new File(source, file);
            File destinationFile = new File(destination, file);
            if (sourceFile.isDirectory()) {
                copyDirectoryToDestination(sourceFile, destinationFile, true, false);
            } else {
                copyFileToDestination(sourceFile, destinationFile, true);
            }
        }
    }
    }