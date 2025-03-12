package com.example.aloe.menu;

import com.example.aloe.Main;
import com.example.aloe.Translator;
import com.example.aloe.WindowComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

class MenuWindowManager {
    public static void openAddItemToMenuWindow() {
        Stage window = WindowComponents.getInternalStage(380, 230);
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        TextField directoryName = WindowComponents.getTextField("", Translator.translate("utils.title"), 290);
        VBox.setMargin(directoryName, new Insets(10, 10, 7, 10));
        TextField directoryPath = WindowComponents.getTextField("", Translator.translate("files-menu.example-path"), 290);

        ObservableList<FontAwesome> icons = FXCollections.observableArrayList(FontAwesome.values());
        ComboBox<FontAwesome> iconComboBox = new ComboBox<>(icons);
        iconComboBox.setValue(FontAwesome.FOLDER_OPEN_O);
        iconComboBox.setVisibleRowCount(12);
        iconComboBox.setMaxWidth(300);
        VBox.setMargin(iconComboBox, new Insets(10, 10, 7, 10));

        Button cancel = WindowComponents.getCancelButton();
        Button add = WindowComponents.getConfirmButton(Translator.translate("button.save"));

        HBox bottomHBox = new HBox(cancel, add);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(WindowComponents.getTitle(Translator.translate("files-menu.title.add")), directoryName, directoryPath, iconComboBox, bottomHBox);

        cancel.setOnAction(event -> window.close());
        add.setOnAction(event -> {
            MenuManager.addItemToMenu(directoryPath.getText(), directoryName.getText(), iconComboBox.getValue().toString());
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

        Scene scene = new Scene(root, 10, 10);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
    }

    public static void openEditItemInMenuWindow(String key, String name, String icon) {
        Stage window = WindowComponents.getInternalStage(380, 230);
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        TextField directoryName = WindowComponents.getTextField(name, Translator.translate("utils.title"), 290);
        VBox.setMargin(directoryName, new Insets(10, 10, 7, 10));
        TextField directoryPath = WindowComponents.getTextField(key, Translator.translate("files-menu.example-path"), 290);

        ObservableList<FontAwesome> icons = FXCollections.observableArrayList(FontAwesome.values());
        ComboBox<FontAwesome> iconComboBox = new ComboBox<>(icons);
        iconComboBox.setValue(FontAwesome.valueOf(icon));
        iconComboBox.setVisibleRowCount(12);
        iconComboBox.setMaxWidth(300);
        VBox.setMargin(iconComboBox, new Insets(10, 10, 7, 10));

        Button cancel = WindowComponents.getCancelButton();
        Button add = WindowComponents.getConfirmButton(Translator.translate("button.save"));

        HBox bottomHBox = new HBox(cancel, add);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(WindowComponents.getTitle(Translator.translate("files-menu.title.edit")), directoryName, directoryPath, iconComboBox, bottomHBox);

        cancel.setOnAction(event -> window.close());
        add.setOnAction(event -> {
            MenuManager.editItemInMenu(key, directoryPath.getText(), directoryName.getText(), iconComboBox.getValue().toString());
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

        Scene scene = new Scene(root, 10, 10);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
    }
}