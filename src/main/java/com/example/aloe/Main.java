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
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();

        File homeDirectory = new File(System.getProperty("user.home"));
        ListView<String> fileList = new ListView<>();

        File[] files = homeDirectory.listFiles();

        if(files != null) {
            List<String> directories = new ArrayList<>();
            List<String> normalFiles = new ArrayList<>();

            for(File file : files) {
                if(file.isDirectory()) {
                    directories.add(file.getName());
                } else {
                    normalFiles.add(file.getName());
                }
            }

            Collections.sort(directories);
            Collections.sort(normalFiles);

            fileList.getItems().addAll(directories);
            fileList.getItems().addAll(normalFiles);
        }

        root.getChildren().add(fileList);

        Scene scene = new Scene(root, 950, 550);
        stage.setTitle("Files");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(900);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}