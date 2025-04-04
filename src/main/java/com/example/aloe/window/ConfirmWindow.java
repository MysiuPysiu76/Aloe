package com.example.aloe.window;

import com.example.aloe.Translator;
import com.example.aloe.WindowComponents;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow extends Stage {

    public ConfirmWindow(String title, String description, EventHandler<ActionEvent> confirmEventHandler) {
        VBox container = new VBox();
        container.setMinWidth(500);
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setPadding(new Insets(13, 10, 3, 20));
        titleLabel.setStyle("-fx-font-size: 20px");
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setPadding(new Insets(3, 10, 5, 20));
        descriptionLabel.setStyle("-fx-font-size: 14px");
        Button cancel = WindowComponents.getCancelButton();
        cancel.setOnMouseClicked(e -> this.close());

        Button confirm = WindowComponents.getConfirmButton(Translator.translate("button.restart"));
        confirm.setOnAction(confirmEventHandler);
        confirm.setOnMouseClicked(e -> this.close());

        HBox box = new HBox(WindowComponents.getSpacer(), cancel, confirm);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));

        container.getChildren().addAll(titleLabel, descriptionLabel, box);
        VBox.setMargin(box, new Insets(0, 15, 15, 5));

        Scene scene = new Scene(container);
        this.setScene(scene);
        this.initModality(Modality.APPLICATION_MODAL);
        this.showAndWait();
    }
}