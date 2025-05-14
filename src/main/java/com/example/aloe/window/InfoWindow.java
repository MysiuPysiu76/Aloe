package com.example.aloe.window;

import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A simple modal informational window displaying a title, description, and a close button.
 * <p>
 * This window is styled according to the currently selected theme and is intended to provide
 * users with brief, non-interactive messages or alerts. It is automatically shown upon creation.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 *     new InfoWindow("Information", "Operation completed successfully.");
 * }</pre>
 *
 * @since 2.4.4
 */
public class InfoWindow extends Stage {

    /**
     * Constructs and displays an information window with the given title and description.
     *
     * @param title       the title text displayed at the top of the window
     * @param description the descriptive message shown below the title
     */
    public InfoWindow(String title, String description) {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(300);
        root.getStyleClass().add("background");

        Label titleLabel = new Label(title);
        titleLabel.setPadding(new Insets(15, 10, 10, 10));
        titleLabel.setStyle("-fx-font-size: 20px");
        titleLabel.getStyleClass().addAll("text");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setPadding(new Insets(0, 10, 10, 10));
        descriptionLabel.getStyleClass().addAll("text");

        Button close = new Button(Translator.translate("button.close"));
        close.getStyleClass().add("btn");

        root.getChildren().addAll(titleLabel, descriptionLabel, close);
        close.setOnAction(event -> this.close());

        Scene scene = new Scene(root, 300, 95);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        this.setScene(scene);
        this.setMinHeight(130);
        this.setMinWidth(420);
        this.initModality(Modality.WINDOW_MODAL);
        this.show();
    }
}
