package com.example.aloe.window;

import com.example.aloe.files.FileDecision;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@code DecisionWindow} class provides a modal dialog window that prompts the user
 * to make a decision when a file or directory with the same name already exists
 * in the destination location.
 * <p>
 * The window supports decisions such as skipping, replacing, copying next to,
 * or combining directories. It uses JavaFX for UI rendering and localization via
 * the {@link Translator} utility.
 * <p>
 * This class is a singleton: only one instance of the window exists at any time.
 *
 * @since 2.4.2
 */
public class DecisionWindow extends Stage {

    /**
     * Singleton instance of the decision window.
     */
    private static DecisionWindow window;

    /**
     * Root container that holds decision content panels.
     */
    private static VBox root;

    /**
     * Atomic reference to store the user's decision.
     */
    private static AtomicReference<FileDecision> userDecision;

    /**
     * Private constructor to initialize and configure the decision window.
     */
    private DecisionWindow() {
        ScrollPane pane = new ScrollPane(root);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setMinWidth(300);
        pane.setFitToHeight(true);
        pane.getStyleClass().add("background");

        Scene scene = new Scene(pane, 430, 240);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/decision.css").toExternalForm());

        this.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        this.setScene(scene);
        this.setMinHeight(235);
        this.setMinWidth(430);
        this.setMaxWidth(430);
        this.initModality(Modality.WINDOW_MODAL);
        this.setTitle(Translator.translate("window.decision.title"));
    }

    static {
        root = new VBox();
        root.setMinWidth(430);
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("background");

        userDecision = new AtomicReference<>();
        window = new DecisionWindow();
    }

    /**
     * Displays a modal dialog for a conflicting file and returns the user's decision.
     *
     * @param file the file that conflicts with an existing file in the destination
     * @return the user's decision as a {@link FileDecision}
     */
    public static FileDecision addFile(File file) {
        VBox content = new VBox();

        Label title = new Label(Translator.translate("window.decision.destination-has-file") + file.getName());
        title.getStyleClass().addAll("title", "text");

        Button skipbutton = getSkipButton("file");
        skipbutton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.SKIP);
            root.getChildren().remove(content);
            window.close();
        });

        Button copyNextToButton = getCopyNextToButton();
        copyNextToButton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.NEXT_TO);
            root.getChildren().remove(content);
            window.close();
        });

        Button replaceButton = getReplaceButton("file");
        replaceButton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.REPLACE);
            root.getChildren().remove(content);
            window.close();
        });

        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("background");
        content.getChildren().addAll(title, skipbutton, copyNextToButton, replaceButton);
        root.getChildren().add(content);
        if (root.getChildren().size() == 1) {
            window.setHeight(235);
        }
        if (!window.isShowing()) {
            window.showAndWait();
        }
        return userDecision.get();
    }

    /**
     * Displays a modal dialog for a conflicting directory and returns the user's decision.
     * Includes an option to combine directory contents.
     *
     * @param file the directory that conflicts with an existing directory in the destination
     * @return the user's decision as a {@link FileDecision}
     */
    public static FileDecision addDirectory(File file) {
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);

        Label title = new Label(Translator.translate("window.decision.destination-has-file") + file.getName());
        title.getStyleClass().addAll("title", "text");

        Button skipbutton = getSkipButton("directory");
        skipbutton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.SKIP);
            root.getChildren().remove(content);
            window.close();
        });

        Button copyNextToButton = getCopyNextToButton();
        copyNextToButton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.NEXT_TO);
            root.getChildren().remove(content);
            window.close();
        });

        Button replaceButton = getReplaceButton("directory");
        replaceButton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.REPLACE);
            root.getChildren().remove(content);
            window.close();
        });

        Button combineButton = getCombineButton();
        combineButton.setOnMouseClicked(e -> {
            userDecision.set(FileDecision.COMBINE);
            root.getChildren().remove(content);
            window.close();
        });

        content.getStyleClass().add("background");
        content.getChildren().addAll(title, skipbutton, copyNextToButton, replaceButton, combineButton);
        root.getChildren().add(content);
        if (root.getChildren().size() == 1) {
            window.setHeight(265);
        }
        if (!window.isShowing()) {
            window.showAndWait();
        }
        return userDecision.get();
    }

    /**
     * Creates a generic styled button with the specified icon, color, and text.
     *
     * @param icon  the {@link FontIcon} to be displayed on the button
     * @param color the {@link Paint} color of the icon
     * @param text  the text label of the button
     * @return a styled {@link Button} node
     */
    private static Button getButton(FontIcon icon, Paint color, String text) {
        Button button = new Button(text, icon);
        icon.setIconSize(20);
        icon.setIconColor(color);
        button.getStyleClass().addAll("option", "text", "button-option");
        button.setOnMouseEntered(e -> button.setStyle("-fx-text-fill: #62d0de;"));
        button.setOnMouseExited(e -> button.setStyle(""));
        return button;
    }

    /**
     * Creates a "Skip" button for file or directory conflicts.
     *
     * @param type either "file" or "directory" to select the correct localized label
     * @return a styled skip {@link Button}
     */
    private static Button getSkipButton(String type) {
        Button button = getButton(
                FontIcon.of(FontAwesome.REPLY_ALL),
                Color.rgb(2, 100, 200),
                Translator.translate("window.decision.skip." + type));
        VBox.setMargin(button, new Insets(4, 10, 10, 10));
        return button;
    }

    /**
     * Creates a "Copy Next To" button for file or directory conflicts.
     *
     * @return a styled copy-next-to {@link Button}
     */
    private static Button getCopyNextToButton() {
        return getButton(
                FontIcon.of(FontAwesome.DATABASE),
                Color.rgb(5, 130, 5),
                Translator.translate("window.decision.copy-next-to"));
    }

    /**
     * Creates a "Replace" button for file or directory conflicts.
     *
     * @param type either "file" or "directory" to select the correct localized label
     * @return a styled replace {@link Button}
     */
    private static Button getReplaceButton(String type) {
        Button button = getButton(
                FontIcon.of(FontAwesome.CLIPBOARD),
                Color.rgb(170, 8, 7),
                Translator.translate("window.decision.replace." + type));
        VBox.setMargin(button, new Insets(7, 10, 4, 10));
        return button;
    }

    /**
     * Creates a "Combine" button specifically for directory conflicts.
     *
     * @return a styled combine {@link Button}
     */
    private static Button getCombineButton() {
        return getButton(
                FontIcon.of(FontAwesome.FOLDER_OPEN),
                Color.rgb(230, 130, 3),
                Translator.translate("window.decision.combine-directory"));
    }

    /**
     * Overrides the default close behavior to prevent closing when panels remain.
     */
    @Override
    public void close() {
        if (root.getChildren().isEmpty()) {
            super.close();
        }
    }
}
