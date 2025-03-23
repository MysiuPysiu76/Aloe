package com.example.aloe;

import com.example.aloe.files.FileDecision;
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
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class WindowService {
    public static String openPasswordPromptWindow() {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(430);
        window.setMinHeight(167);
        window.setMinWidth(430);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);
        final String[] password = {""};

        Label name = new Label(Translator.translate("window.archive.extract.enter-password"));
        name.setPadding(new Insets(1, 212, 7, 0));
        name.setStyle("-fx-font-size: 14px");

        TextField passwordField = new TextField();
        passwordField.setStyle("-fx-font-size: 15px");
        passwordField.setMaxWidth(330);
        passwordField.setPadding(new Insets(7, 10, 7, 10));

        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        Button extract = new Button(Translator.translate("button.extract"));
        extract.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");

        HBox bottomHBox = new HBox(cancel, extract);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(WindowComponents.getTitle(Translator.translate("window.archive.extract.password-required")), name, passwordField, bottomHBox);

        cancel.setOnAction(event -> {
            password[0] = null;
            window.close();
        });

        extract.setOnAction(e -> {
            password[0] = passwordField.getText();
            window.close();
        });

        Scene scene = new Scene(root, 330, 140);
        window.setScene(scene);
        window.initOwner(Main.stage);
        window.showAndWait();
        return password[0];
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
        scene.getStylesheets().add(WindowService.class.getResource("/assets/styles/style_file_exists.css").toExternalForm());
        filesDecisionsView.setMinWidth(425);
        decisionWindow.setScene(scene);
        decisionWindow.setTitle(Translator.translate("window.decision.title"));
        filesDecisionsView.setAlignment(Pos.CENTER);
    }

    private static VBox filesDecisionsView;

    public static FileDecision addFileDecisionAskToExistFileWindow(FileOperation operation) {
        openDecisionWindowFileExists();
        decisionWindow.setHeight(220.0);
        VBox content = new VBox();
        content.setMinWidth(425);
        Label destinationHasFile = new Label(Translator.translate("window.decision.destination-has-file") + operation.getSource().getName());
        destinationHasFile.getStyleClass().add("title-label");
        VBox.setMargin(destinationHasFile, new Insets(10, 10, 5, 10));

        Button replaceButton = getReplaceButton("file");
        Button copyNextToButton = getCopyNextToButton();
        Button skipButton = getSkipButton("file");
        final FileDecision[] decisions = {FileDecision.NEXT_TO};

        replaceButton.setOnAction(event -> {
            decisions[0] = FileDecision.REPLACE;
            decisionWindow.close();
        });

        copyNextToButton.setOnAction(event -> {
            decisions[0] = FileDecision.NEXT_TO;
            decisionWindow.close();
        });

        skipButton.setOnAction(event -> {
            decisions[0] = FileDecision.SKIP;
            decisionWindow.close();
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(destinationHasFile, replaceButton, copyNextToButton, skipButton);
        filesDecisionsView.getChildren().add(content);
        decisionWindow.showAndWait();
        return decisions[0];
    }

    public static FileDecision addDirectoryDecisionAskToExistFileWindow(FileOperation operation) {
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
        final FileDecision[] decisions = {FileDecision.NEXT_TO};

        replaceButton.setOnAction(event -> {
            decisions[0] = FileDecision.REPLACE;
            decisionWindow.close();
        });

        combineButton.setOnAction(event -> {
            decisions[0] = FileDecision.COMBINE;
            decisionWindow.close();
        });

        copyNextToButton.setOnAction(event -> {
            decisions[0] = FileDecision.NEXT_TO;
            decisionWindow.close();
        });

        skipButton.setOnAction(event -> {
            decisions[0] = FileDecision.SKIP;
            decisionWindow.close();
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(destinationHasDirectory, replaceButton, combineButton, copyNextToButton, skipButton);
        filesDecisionsView.getChildren().add(content);

        decisionWindow.showAndWait();
        updateDecisionAskForExistingFiles(content, operation);
        return decisions[0];
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