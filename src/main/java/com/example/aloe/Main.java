package com.example.aloe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    private File currentDirectory = new File(System.getProperty("user.home"));
    private ListView<String> filesList;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();

        File homeDirectory = new File(System.getProperty("user.home"));
        filesList = new ListView<>();

        File[] files = homeDirectory.listFiles();

        loadDirectoryContents(currentDirectory);

        filesList.setOnMouseClicked((event) -> {
            String selectedItem = filesList.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
                File selectedFile = new File(currentDirectory, selectedItem);
                if(selectedFile.isDirectory()) {
                    loadDirectoryContents(selectedFile);
                }
            }
        });

        root.getChildren().add(filesList);

        Scene scene = new Scene(root, 950, 550);
        stage.setTitle("Files");
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.show();
    }

    private void loadDirectoryContents(File directory) {
        currentDirectory = directory;
        filesList.getItems().clear();

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
    }
}