package com.example.aloe;

import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowService {

    public static void openArchiveInfoWindow(String key) {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(300);
        window.setMinHeight(95);
        window.setMinWidth(300);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate(key));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 20px");
        Button close = new Button(Translator.translate("button.close"));
        close.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 25px;");

        root.getChildren().addAll(title, close);

        close.setOnAction(event -> window.close());

        Scene scene = new Scene(root, 300, 95);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
    }
}