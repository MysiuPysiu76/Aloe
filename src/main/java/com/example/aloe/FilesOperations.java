package com.example.aloe;

import javafx.concurrent.Task;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class FilesOperations {

    private static File currentDirectory = null;

    public static void copyFile(File file) {
        if (file.exists()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putFiles(java.util.Collections.singletonList(file));
            clipboard.setContent(content);
        }
    }

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
                WindowService.addFileDecisionAskToExistFileWindow(operation);
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static void copyDirectoryToDestination(File source, File destination, boolean replaceExisting, boolean combine) throws IOException {
        if(destination.exists()) {
            if (replaceExisting) {
                FilesOperations.deleteFile(destination);
                destination.mkdir();
                pasteFiles(source, destination);
            } else {
                if (combine) {
                    pasteFiles(source, destination);
                } else {
                    FileOperation operation = new FileOperation(FileOperation.OperationType.COPY, source, destination);
                    if (FileOperation.addOperationToQueue(operation)) {
                        WindowService.addDirectoryDecisionAskToExistFileWindow(operation);
                    }
                }
            }
        } else {
            destination.mkdir();
            pasteFiles(source, destination);
        }
    }

    private static void pasteFiles(File source, File destination) throws IOException {
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

    public static void deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
        }
        file.delete();
    }

    public static void openFileInBackground(File file) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                openFile(file);
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported on this system.");
        }
    }

    public static void copyFilesToClipboard(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putFiles(files);
        clipboard.setContent(content);
    }

    public static void pasteFilesFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        List<File> filesFromClipboard = clipboard.getFiles();

        if (filesFromClipboard != null && !filesFromClipboard.isEmpty()) {
            for (File fileFromClipboard : filesFromClipboard) {
                File destinationFile = new File(getCurrentDirectory(), fileFromClipboard.getName());
                try {
                    if (fileFromClipboard.isDirectory()) {
                        copyDirectoryToDestination(fileFromClipboard, destinationFile, false, false);
                    } else {
                        copyFileToDestination(fileFromClipboard, destinationFile, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void moveFileToParent(File file) {
        try {
            Files.move(file.toPath(), file.getParentFile().getParentFile().toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moveFileToParent(List<File> files) {
        try {
            for (File file : files) {
                Files.move(file.toPath(), file.getParentFile().getParentFile().toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getExtension(File file) {
        return FilesOperations.getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        if (fileName.endsWith(".tar.gz")) return "tar.gz";
        return fileName.substring(lastDotIndex + 1);
    }

    public static File chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Translator.translate("context-menu.move-to"));
        directoryChooser.setInitialDirectory(FilesOperations.getCurrentDirectory());
        return directoryChooser.showDialog(Main.scene.getWindow());
    }

    public static void moveFileTo(List<File> files) {
        moveFileTo(files, chooseDirectory());
    }

    public static void moveFileTo(List<File> files, File destination) {
        try {
            if(destination != null) {
                for (File file : files) {
                    Files.move(file.toPath(), destination.toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void moveFileToTrash(File file) {
        File trash = new File(System.getProperty("user.home"), ".trash");
        if (!trash.exists() || trash.isFile()) {
            trash.mkdir();
        }
        Path newPath = trash.toPath().resolve(file.getName());
        int i = 0;
        while (Files.exists(newPath)) {
            String fileName = getUniqueName(file.getName(), i);
            newPath = trash.toPath().resolve(fileName);
            i++;
        }
        try {
            Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveFileToTrash(List<File> files) {
        File trash = new File(System.getProperty("user.home"), ".trash");
        if (!trash.exists() || trash.isFile()) {
            trash.mkdir();
        }

        for (File file : files) {
            Path newPath = trash.toPath().resolve(file.getName());
            short i = 1;
            while (Files.exists(newPath)) {
                String fileName = getUniqueName(file.getName(), i);
                newPath = trash.toPath().resolve(fileName);
                i++;
            }
            try {
                Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getUniqueName(String name, int suffix) {
        String fileName;
        String extension = "";
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex > 0) {
            fileName = name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        } else {
            fileName = name;
        }
        return fileName + "_" + suffix + extension;
    }

    public static String getUniqueDirectoryName(String name, int suffix) {
        return name + "_" + suffix;
    }
}