package com.example.aloe;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    private File currentDirectory = new File(System.getProperty("user.home"));
    private ListView<String> filesList;
    private List<File> directoryHistory = new ArrayList<>();
    private int directoryHistoryPosition = -1;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        HBox navigationPanel = new HBox();

        ButtonBar navigationDirectoryPanel = new ButtonBar();
        Button prevDirectory = new Button("Previous");
        Button nextDirectory = new Button("Next");

        prevDirectory.setOnMouseClicked((event) -> {
            if (directoryHistoryPosition > 0) {
                directoryHistoryPosition--;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });

        nextDirectory.setOnMouseClicked((event) -> {
            if (directoryHistoryPosition < directoryHistory.size() - 1) {
                directoryHistoryPosition++;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });

        navigationDirectoryPanel.getButtons().addAll(prevDirectory, nextDirectory);
        navigationPanel.getChildren().add(navigationDirectoryPanel);
        root.getChildren().add(navigationPanel);

        filesList = new ListView<>();
        loadDirectoryContents(currentDirectory, true);

        root.getChildren().add(filesList);

        Scene scene = new Scene(root, 950, 550);
        stage.setTitle("Files");
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.show();
    }

    private void loadDirectoryContents(File directory, boolean addToHistory) {
        currentDirectory = directory;
        filesList.getItems().clear();

        if (addToHistory) {
            if (directoryHistoryPosition != directoryHistory.size() - 1) {
                directoryHistory = new ArrayList<>(directoryHistory.subList(0, directoryHistoryPosition + 1));
            }
            directoryHistory.add(directory);
            directoryHistoryPosition++;
        }

        File[] files = currentDirectory.listFiles();

        if (files != null) {
            List<String> directories = new ArrayList<>();
            List<String> normalFiles = new ArrayList<>();

            for (File file : files) {
                if (file.isDirectory()) {
                    directories.add(file.getName());
                } else {
                    normalFiles.add(file.getName());
                }
            }

            Collections.sort(directories);
            Collections.sort(normalFiles);

            filesList.getItems().addAll(directories);
            filesList.getItems().addAll(normalFiles);
        }

        filesList.setOnMouseClicked(event -> {
            String selectedItem = filesList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                File selectedFile = new File(currentDirectory, selectedItem);
                if (selectedFile.isDirectory()) {
                    loadDirectoryContents(selectedFile, true);
                } else {
                    openFileInBackground(selectedFile);
                }
            }
        });
    }

    private void openFileInBackground(File file) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                openFile(file);
                return null;
            }
        };
        new Thread(task).start();
    }

    private void openFile(File file) {
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
}
