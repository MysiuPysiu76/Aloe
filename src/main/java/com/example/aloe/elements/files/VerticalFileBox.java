package com.example.aloe.elements.files;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.io.File;

class VerticalFileBox extends FileBox {

    private VBox content = new VBox();

    public VerticalFileBox(File file) {
        super(file);

        this.content.setMinWidth(100 * scale);
        this.content.setPrefWidth(100 * scale);
        this.content.setMaxWidth(100 * scale);
        this.content.setMinHeight(125 * scale);
        this.content.setMaxHeight(125 * scale);
        this.content.setAlignment(Pos.TOP_CENTER);
        this.content.setSpacing(5 * scale);
        this.content.setPadding(new Insets(0, 5, 10, 5));

        this.content.getChildren().addAll(getImageBox(60, new Insets(8, 2, 5, 2)), getName());
        this.getChildren().add(content);
    }
}