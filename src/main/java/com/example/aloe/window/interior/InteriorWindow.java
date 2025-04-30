package com.example.aloe.window.interior;

import com.example.aloe.WindowComponents;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.window.MainWindow;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InteriorWindow extends VBox {

    protected Button confirmButton;

    public InteriorWindow() {
        this.getStyleClass().addAll("background", "window");

        MainWindow.getInteriorPane().getChildren().add(this);
        MainWindow.showDarkeningPlate();

        this.layoutXProperty().bind(MainWindow.getInteriorPane().widthProperty().subtract(this.widthProperty()).divide(2));
        this.layoutYProperty().bind(MainWindow.getInteriorPane().heightProperty().subtract(this.heightProperty()).divide(2));
    }

    protected TextField getInput(String text, String prompt) {
        TextField input = new TextField(text);
        input.getStyleClass().addAll("background", "text");
        input.selectAll();
        input.setPromptText(prompt);
        VBox.setMargin(input, new Insets(4, 0, 5, 0));
        return input;
    }

    protected Label getTitleLabel(String text) {
        Label title = new Label(text);
        title.getStyleClass().addAll("text", "title");
        return title;
    }

    protected Label getInfoLabel(String text) {
        Label title = new Label(text);
        title.getStyleClass().addAll("text", "info");
        return title;
    }

    protected Button getConfirmButton(String text) {
        return WindowComponents.getConfirmButton(text);
    }

    protected static Button getCancelButton() {
        Button button = WindowComponents.getCancelButton();
        button.setOnAction(e -> MainWindow.hideDarkeningPlate());
        return button;
    }

    protected HBox getBottomPanel(Node... items) {
        HBox box = new HBox(items);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));
        return box;
    }

    protected void hideOverlay() {
        FilesLoader.refresh();
        MainWindow.hideDarkeningPlate();
    }

    protected void setOnConfirm(EventHandler<? super MouseEvent> eventHandler) {
        confirmButton.setOnMouseClicked(eventHandler);
    }

    protected ComboBox getComboBox(ObservableList observableList) {
        ComboBox comboBox = new ComboBox(observableList);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBox, Priority.ALWAYS);
        return comboBox;
    }
}
