package com.example.aloe;

import com.example.aloe.settings.SettingsWindow;
import com.example.aloe.window.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        MainWindow.create(stage);
        new SettingsWindow();
    }
}
