package com.example.aloe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

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

    public static void openAddItemToMenuWindow() {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        window.setMinHeight(230);
        window.setMinWidth(320);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate("files-menu.title.add"));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 17px");

        TextField directoryName = new TextField();
        directoryName.setStyle("-fx-font-size: 14px");
        directoryName.setMinWidth(290);
        directoryName.setMaxWidth(290);
        directoryName.setPadding(new Insets(7, 10, 7, 10));
        directoryName.setPromptText(Translator.translate("utils.title"));
        VBox.setMargin(directoryName, new Insets(10, 10, 7, 10));

        TextField directoryPath = new TextField();
        directoryPath.setStyle("-fx-font-size: 14px");
        directoryPath.setMinWidth(290);
        directoryPath.setMaxWidth(290);
        directoryPath.setPadding(new Insets(7, 10, 7, 10));
        directoryPath.setPromptText(Translator.translate("files-menu.example-path"));

        ObservableList<FontAwesome> icons = FXCollections.observableArrayList(FontAwesome.values());
        ComboBox<FontAwesome> iconComboBox = new ComboBox<>(icons);
        iconComboBox.setValue(FontAwesome.FOLDER_OPEN_O);
        iconComboBox.setVisibleRowCount(12);
        iconComboBox.setMaxWidth(300);
        VBox.setMargin(iconComboBox, new Insets(10, 10, 7, 10));

        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        Button add = new Button(Translator.translate("button.add"));
        add.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");

        HBox bottomHBox = new HBox(cancel, add);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(title, directoryName, directoryPath, iconComboBox, bottomHBox);

        cancel.setOnAction(event -> window.close());
        add.setOnAction(event -> {
            Main main = new Main();
            main.addDirectoryListInMenu(directoryPath.getText(), directoryName.getText(), iconComboBox.getValue());
            window.close();
        });

        iconComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FontAwesome> call(ListView<FontAwesome> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FontAwesome item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            FontIcon iconView = new FontIcon(item);
                            iconView.setIconSize(20);
                            iconView.setIconColor(Color.BLACK);
                            setText(item.name());
                            setGraphic(iconView);
                        }
                    }
                };
            }
        });
        iconComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FontAwesome item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontIcon iconView = new FontIcon(item);
                    iconView.setIconSize(20);
                    iconView.setIconColor(Color.BLACK);
                    setText(item.name());
                    setGraphic(iconView);
                }
            }
        });

        Scene scene = new Scene(root, 380, 150);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.show();
    }

    public static void openEditItemInMenuWindow(String key, String name, FontAwesome icon) {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        window.setMinHeight(230);
        window.setMinWidth(320);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate("files-menu.title.edit"));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 17px");

        TextField directoryName = new TextField(name);
        directoryName.setStyle("-fx-font-size: 14px");
        directoryName.setMinWidth(290);
        directoryName.setMaxWidth(290);
        directoryName.setPadding(new Insets(7, 10, 7, 10));
        directoryName.setPromptText(Translator.translate("utils.title"));
        VBox.setMargin(directoryName, new Insets(10, 10, 7, 10));

        TextField directoryPath = new TextField(key);
        directoryPath.setStyle("-fx-font-size: 14px");
        directoryPath.setMinWidth(290);
        directoryPath.setMaxWidth(290);
        directoryPath.setPadding(new Insets(7, 10, 7, 10));
        directoryPath.setPromptText(Translator.translate("files-menu.example-path"));

        ObservableList<FontAwesome> icons = FXCollections.observableArrayList(FontAwesome.values());
        ComboBox<FontAwesome> iconComboBox = new ComboBox<>(icons);
        iconComboBox.setValue(icon);
        iconComboBox.setVisibleRowCount(12);
        iconComboBox.setMaxWidth(300);
        VBox.setMargin(iconComboBox, new Insets(10, 10, 7, 10));

        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        Button add = new Button(Translator.translate("button.save"));
        add.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");

        HBox bottomHBox = new HBox(cancel, add);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(title, directoryName, directoryPath, iconComboBox, bottomHBox);

        cancel.setOnAction(event -> window.close());
        add.setOnAction(event -> {
            Main main = new Main();
            main.replaceItemInDirectoryList(key, directoryPath.getText(), directoryName.getText(),iconComboBox.getValue());
            window.close();
        });

        iconComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FontAwesome> call(ListView<FontAwesome> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FontAwesome item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            FontIcon iconView = new FontIcon(item);
                            iconView.setIconSize(20);
                            iconView.setIconColor(Color.BLACK);
                            setText(item.name());
                            setGraphic(iconView);
                        }
                    }
                };
            }
        });
        iconComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FontAwesome item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontIcon iconView = new FontIcon(item);
                    iconView.setIconSize(20);
                    iconView.setIconColor(Color.BLACK);
                    setText(item.name());
                    setGraphic(iconView);
                }
            }
        });

        Scene scene = new Scene(root, 380, 150);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.show();
    }
}