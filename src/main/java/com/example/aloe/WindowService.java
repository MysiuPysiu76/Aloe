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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        window.showAndWait();
    }

    private static Stage decisionWindow;

    public static void openDecisionWindowFileExists() {
        decisionWindow = new Stage();
        ScrollPane root = new ScrollPane();
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setMinWidth(300);
        decisionWindow.setMinHeight(220);
        decisionWindow.setHeight(220);
        decisionWindow.setMinWidth(430);
        decisionWindow.setMaxWidth(430);
        decisionWindow.initModality(Modality.WINDOW_MODAL);

        if (filesDecisionsView == null) {
            filesDecisionsView = new VBox();
            filesDecisionsView.setAlignment(Pos.CENTER);
        }

        root.setContent(filesDecisionsView);
        Scene scene = new Scene(root, 430, 220);
        scene.getStylesheets().add(WindowService.class.getResource("/assets/css/style_file_exists.css").toExternalForm());
        filesDecisionsView.setMinWidth(425);
        decisionWindow.setScene(scene);
        decisionWindow.setTitle(Translator.translate("window.decision.title"));
        filesDecisionsView.setAlignment(Pos.CENTER);
        decisionWindow.setOnCloseRequest(event -> {
            if (WindowService.openConfirmWindow("confirm.skip.copy")) {
                filesDecisionsView.getChildren().clear();
                FileOperation.clearOperationFromQueue();
            } else {
                decisionWindow.showAndWait();
            }
        });
    }

    private static VBox filesDecisionsView;

    public static void addFileDecisionAskToExistFileWindow(FileOperation operation) {
        if (decisionWindow == null) {
            openDecisionWindowFileExists();
        }
        decisionWindow.setHeight(220.0);
        VBox content = new VBox();
        content.setMinWidth(425);
        Label destinationHasFile = new Label(Translator.translate("window.decision.destination-has-file") + operation.getDestination().getName());
        destinationHasFile.getStyleClass().add("title-label");
        VBox.setMargin(destinationHasFile, new Insets(10, 10, 5, 10));

        Button replaceButton = getReplaceButton("file");
        Button copyNextToButton = getCopyNextToButton();
        Button skipButton = getSkipButton("file");

        replaceButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
            try {
                FilesOperations.copyFileToDestination(operation.getSource(), operation.getDestination(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        copyNextToButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
            try {
                Path path = operation.getDestination().toPath();
                int i = 1;
                while (Files.exists(path)) {
                    String fileName = FilesOperations.getUniqueName(operation.getSource().getName(), i);
                    File destination = new File(operation.getDestination().toPath().toString());
                    File parent = new File(destination.getParent());
                    path = parent.toPath().resolve(fileName);
                    i++;
                }
                Files.copy(operation.getSource().toPath(), path, StandardCopyOption.REPLACE_EXISTING);
                FilesOperations.copyFileToDestination(operation.getSource(), operation.getDestination(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skipButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(destinationHasFile, replaceButton, copyNextToButton, skipButton);
        filesDecisionsView.getChildren().add(content);
        if (!decisionWindow.isShowing()) {
            decisionWindow.showAndWait();
        }
    }

    public static void addDirectoryDecisionAskToExistFileWindow(FileOperation operation) {
        if (decisionWindow == null) {
            openDecisionWindowFileExists();
        }
        decisionWindow.setHeight(270.0);
        VBox content = new VBox();
        content.setMinWidth(425);
        Label destinationHasDirectory = new Label(Translator.translate("window.decision.destination-has-directory") + operation.getDestination().getName());
        destinationHasDirectory.getStyleClass().add("title-label");
        VBox.setMargin(destinationHasDirectory, new Insets(10, 10, 5, 10));

        Button replaceButton = getReplaceButton("directory");
        Button combineButton = getCombineButton();
        Button copyNextToButton = getCopyNextToButton();
        Button skipButton = getSkipButton("directory");

        replaceButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
            try {
                FilesOperations.copyDirectoryToDestination(operation.getSource(), operation.getDestination(), true, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        combineButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
            try {
                FilesOperations.copyDirectoryToDestination(operation.getSource(), operation.getDestination(), false, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        copyNextToButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
            try {
                Path path = operation.getDestination().toPath();
                Path clear = path;
                int i = 1;
                while (Files.exists(path)) {
                    path = new File(clear.toString() + "_" + i).toPath();
                    i++;
                }
                FilesOperations.copyDirectoryToDestination(operation.getSource(), path.toFile(), false, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skipButton.setOnAction(event -> {
            updateDecisionAskForExistingFiles(content, operation);
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(destinationHasDirectory, replaceButton, combineButton, copyNextToButton, skipButton);
        filesDecisionsView.getChildren().add(content);
        if (!decisionWindow.isShowing()) {
            decisionWindow.showAndWait();
        }
    }

    private static void updateDecisionAskForExistingFiles(VBox content, FileOperation operation) {
        filesDecisionsView.getChildren().remove(content);
        FileOperation.removeOperationFromQueue(operation);
        if (FileOperation.isEmpty()) {
            decisionWindow.close();
        }
    }

    private static Button getSkipButton(String type) {
        FontIcon skipIcon = FontIcon.of(FontAwesome.REPLY_ALL);
        skipIcon.setIconSize(20);
        skipIcon.setIconColor(Color.rgb(2, 100, 200));
        Button skipButton = new Button(Translator.translate("window.decision.skip." + type), skipIcon);
        skipButton.setMinWidth(360);
        skipButton.setMinHeight(45);
        skipButton.setGraphicTextGap(6);
        VBox.setMargin(skipButton, new Insets(4, 10, 10, 10));
        return skipButton;
    }

    private static Button getCopyNextToButton() {
        FontIcon copyNextToIcon = FontIcon.of(FontAwesome.DATABASE);
        copyNextToIcon.setIconSize(20);
        copyNextToIcon.setIconColor(Color.rgb(5,130,5));
        Button copyNextToButton = new Button(Translator.translate("window.decision.copy-next-to"), copyNextToIcon);
        copyNextToButton.setMinWidth(360);
        copyNextToButton.setMinHeight(45);
        copyNextToButton.setGraphicTextGap(6);
        return copyNextToButton;
    }

    private static Button getReplaceButton(String type) {
        FontIcon replaceIcon = FontIcon.of(FontAwesome.CLIPBOARD);
        replaceIcon.setIconSize(20);
        replaceIcon.setIconColor(Color.rgb(170,8, 7));
        Button replaceButton = new Button(Translator.translate("window.decision.replace." + type), replaceIcon);
        replaceButton.setMinWidth(360);
        replaceButton.setMinHeight(45);
        replaceButton.setGraphicTextGap(6);
        VBox.setMargin(replaceButton, new Insets(7, 10, 4, 10));
        return replaceButton;
    }

    private static Button getCombineButton() {
        FontIcon combineIcon = FontIcon.of(FontAwesome.FOLDER_OPEN);
        combineIcon.setIconSize(20);
        combineIcon.setIconColor(Color.rgb(230, 130, 3));
        Button combineButton = new Button(Translator.translate("window.decision.combine-directory"), combineIcon);
        combineButton.setMinWidth(360);
        combineButton.setMinHeight(45);
        combineButton.setGraphicTextGap(6);
        return combineButton;
    }

    public static boolean openConfirmWindow(String key) {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(300);
        window.setMinHeight(115);
        window.setMinWidth(300);
        window.setWidth(400);
        window.setMaxWidth(500);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        final boolean[] userChose = {false};

        Label title = new Label(Translator.translate(key));
        title.setPadding(new Insets(17, 10, 10, 10));
        title.setStyle("-fx-font-size: 20px");
        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 25px;");
        Button confirm = new Button(Translator.translate("button.exit"));
        confirm.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 25px;");
        HBox buttonHBox = new HBox(cancel, confirm);
        HBox.setMargin(cancel, new Insets(0, 10, 0 , 0));
        buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);
        root.getChildren().addAll(title, buttonHBox);
        VBox.setMargin(buttonHBox, new Insets(19, 15, 15, 10));

        cancel.setOnAction(event -> {
            window.close();
        });
        confirm.setOnAction(event -> {
            userChose[0] = true;
            window.close();
        });

        Scene scene = new Scene(root, 300, 115);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
        return userChose[0];
    }
}