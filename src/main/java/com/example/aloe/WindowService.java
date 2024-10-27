package com.example.aloe;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowService {
    public static void openPasswordPromptWindow() {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(430);
        window.setMinHeight(167);
        window.setMinWidth(430);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate("archive.extract.title"));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 20px");

        Label name = new Label(Translator.translate("archive.extract.enter-password"));
        name.setPadding(new Insets(1, 212, 7, 0));
        name.setStyle("-fx-font-size: 14px");

        TextField password = new TextField();
        password.setStyle("-fx-font-size: 15px");
        password.setMaxWidth(330);
        password.setPadding(new Insets(7, 10, 7, 10));

        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        Button extract = new Button(Translator.translate("button.extract"));
        extract.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");

        HBox bottomHBox = new HBox(cancel, extract);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(title, name, password, bottomHBox);

        cancel.setOnAction(event -> window.close());

        extract.setOnAction(e -> {
            ArchiveManager.setPassword(password.getText());
            window.close();
        });

        Scene scene = new Scene(root, 330, 140);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
    }

    public static void openWrongPasswordWindow() {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(200);
        window.setMinHeight(95);
        window.setMinWidth(200);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate("archive.extract.wrong-password"));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 20px");
        Button close = new Button(Translator.translate("button.close"));
        close.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 25px;");

        root.getChildren().addAll(title, close);

        close.setOnAction(event -> window.close());

        Scene scene = new Scene(root, 230, 95);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
    }
}