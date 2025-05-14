package com.example.aloe.window;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.utils.Translator;
import com.example.aloe.settings.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal confirmation window prompting the user to confirm or cancel an action.
 * <p>
 * This window displays a customizable title and description, and provides two buttons:
 * one to cancel the operation and close the window, and another to confirm the operation,
 * executing a provided {@link EventHandler}.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Applies the current UI theme and styling dynamically</li>
 *   <li>Blocks the application flow until the user responds (via {@code showAndWait()})</li>
 *   <li>Localizes button text using the {@link Translator} utility</li>
 * </ul>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 *     new ConfirmWindow(
 *         "Delete File",
 *         "Are you sure you want to permanently delete this file?",
 *         "Delete",
 *         event -> deleteFile()
 *     );
 * }</pre>
 *
 * @since 2.4.6
 */
public class ConfirmWindow extends Stage {

    /**
     * Constructs and displays a confirmation dialog with the specified content and behavior.
     *
     * @param title               the title text displayed at the top of the window
     * @param description         a description of the action to be confirmed
     * @param confirm             the label for the confirmation button
     * @param confirmEventHandler an event handler executed if the user confirms the action
     */
    public ConfirmWindow(String title, String description, String confirm, EventHandler<ActionEvent> confirmEventHandler) {
        VBox container = new VBox();
        container.getStyleClass().addAll("background", "root");

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().addAll("title", "text");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().addAll("description", "text");

        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.getStyleClass().add("btn");
        cancel.setOnMouseClicked(e -> this.close());

        Button confirmButton = new Button(confirm);
        confirmButton.getStyleClass().addAll("btn", "text", "confirm");
        confirmButton.setOnAction(confirmEventHandler);
        confirmButton.setOnMouseClicked(e -> this.close());

        HBox box = new HBox(new HBoxSpacer(), cancel, confirmButton);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));

        container.getChildren().addAll(titleLabel, descriptionLabel, box);
        VBox.setMargin(box, new Insets(0, 15, 15, 5));

        Scene scene = new Scene(container);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/confirm.css").toExternalForm());
        scene.getStylesheets().add(String.format("data:text/css, .confirm { -fx-background-color: %s; }", Settings.getColor()));

        this.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        this.setScene(scene);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(Translator.translate("window.confirm.confirm-action"));
        this.showAndWait();
    }
}
