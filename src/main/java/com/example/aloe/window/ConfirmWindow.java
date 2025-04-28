package com.example.aloe.window;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.utils.Translator;
import com.example.aloe.WindowComponents;
import com.example.aloe.settings.Settings;
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
        container.getStyleClass().addAll("background", "root");
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().addAll("title", "text");
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().addAll("description", "text");
        Button cancel = WindowComponents.getCancelButton();
        cancel.setOnMouseClicked(e -> this.close());

        Button confirm = WindowComponents.getConfirmButton(Translator.translate("button.restart"));
        confirm.setOnAction(confirmEventHandler);
        confirm.setOnMouseClicked(e -> this.close());

        HBox box = new HBox(new HBoxSpacer(), cancel, confirm);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));

        container.getChildren().addAll(titleLabel, descriptionLabel, box);
        VBox.setMargin(box, new Insets(0, 15, 15, 5));

        Scene scene = new Scene(container);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/confirm.css").toExternalForm());
        this.setScene(scene);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(Translator.translate("window.confirm.confirm-action"));
        this.showAndWait();
    }
}