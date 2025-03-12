package com.example.aloe;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InteriorWindow extends VBox {

    public InteriorWindow() {

        this.setPadding(new Insets(25, 25, 23, 25));
        this.setMinWidth(500);
        this.setStyle("-fx-background-color: #f0f1f0; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-border-color: #acacac;");

        this.layoutXProperty().bind(Main.pane.widthProperty().subtract(this.widthProperty()).divide(2));
        this.layoutYProperty().bind(Main.pane.heightProperty().subtract(this.heightProperty()).divide(2));
    }

    protected TextField getInput(String text, String prompt) {
        TextField input = new TextField(text);
        input.setStyle("-fx-font-size: 14px; -fx-border-radius: 10px");
        input.setPadding(new Insets(8));
        input.selectAll();
        input.setPromptText(prompt);
        VBox.setMargin(input, new Insets(2, 0, 5, 0));
        return input;
    }

    protected Label getTitleLabel(String text) {
        Label title = new Label(text);
        title.setStyle("-fx-text-fill: #242524; -fx-font-size: 21px; -fx-font-weight: bold;");
        title.setPadding(new Insets(0, 0, 10, 0));
        return title;
    }

    protected Label getInfoLabel(String text) {
        Label title = new Label(text);
        title.setStyle("-fx-text-fill: #444445; -fx-font-size: 14px");
        title.setPadding(new Insets(4, 0, 4, 0));
        return title;
    }

    protected Button getConfirmButton(String text) {
        return WindowComponents.getConfirmButton(text);
    }

    protected static Button getCancelButton() {
        Button button = WindowComponents.getCancelButton();
        button.setOnAction(e -> Main.hideDarkeningPlate());
        return button;
    }

    protected HBox getBottomPanel(Node... items) {
        HBox box = new HBox(items);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));
        return box;
    }
}