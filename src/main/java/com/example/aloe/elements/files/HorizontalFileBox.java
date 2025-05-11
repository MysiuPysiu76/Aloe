package com.example.aloe.elements.files;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.files.properties.FileProperties;
import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

class HorizontalFileBox extends FileBox {

    private final HBox content = new HBox();

    HorizontalFileBox(File file) {
        super(file);
        this.content.setMinHeight(35 * scale);
        this.content.setPadding(new Insets(7));
        this.content.setAlignment(Pos.CENTER);
        this.content.setSpacing(5 * scale);
        this.widthProperty().addListener((ob, ol, ne) -> this.content.setMinWidth(Double.parseDouble(ne.toString())));

        Label name = getName();
        name.setMaxWidth(Double.MAX_VALUE);
        name.setAlignment(Pos.CENTER_LEFT);
        FileProperties fileProperties = new FileProperties(this.getFile());
        VBox.setMargin(this, new Insets(1, 15, 2, 15));

        this.content.getChildren().addAll(getImageBox(30, new Insets(2, 10, 2, 10)), name, new HBoxSpacer(), getModified(fileProperties), getSize(fileProperties));
        this.getChildren().add(content);
    }

    static HBox getInfoPanel() {
        HBox box = new HBox();
        box.setPadding(new Insets(7, 72, 7, 75));

        Label name = getInfoLabel("window.file-box.name");
        Label modified = getInfoLabel("window.file-box.modified");
        modified.setMinWidth(148);
        Label size = getInfoLabel("window.file-box.size");
        size.setMinWidth(60);
        size.setAlignment(Pos.CENTER);

        box.getChildren().addAll(name, new HBoxSpacer(), modified, size);
        return box;
    }

    static Label getInfoLabel(String key) {
        Label label = new Label(Translator.translate(key));
        label.setStyle("-fx-font-weight: bold");
        label.getStyleClass().add("text");
        return label;
    }

    private Label getModified(FileProperties properties) {
        Label label = new Label(properties.getModifiedTime());
        label.setMinWidth(140);
        label.getStyleClass().add("text");
        return label;
    }

    private Label getSize(FileProperties properties) {
        Label size = new Label(properties.getShortSize());
        size.setMinWidth(80);
        size.getStyleClass().add("text");
        HBox.setMargin(size, new Insets(0, 15, 0, 20));
        return size;
    }
}