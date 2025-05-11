package com.example.aloe.elements.navigation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class ProgressBox extends VBox {

    public ProgressBox(String title, DoubleProperty progress, StringProperty description) {
        this.setMinWidth(200);
        this.setMaxWidth(300);
        this.setPadding(new Insets(6, 13, 6, 13));

        Label titleLabel = new Label(title);
        titleLabel.setPadding(new Insets(7, 9, 7, 5));
        titleLabel.getStyleClass().add("text");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.progressProperty().bind(progress);
        progressBar.setMaxWidth(this.getMaxWidth());
        progressBar.setPadding(new Insets(0));
        progressBar.setMaxHeight(10);
        progressBar.getStyleClass().add("ce");

        Label descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("text");
        descriptionLabel.setPadding(new Insets(7, 9, 7, 5));
        descriptionLabel.textProperty().bind(description);

        this.getChildren().addAll(titleLabel, progressBar, descriptionLabel);
    }
}