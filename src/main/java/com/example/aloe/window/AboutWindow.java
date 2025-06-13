package com.example.aloe.window;

import com.example.aloe.utils.Translator;
import com.example.aloe.settings.Settings;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.SegmentedButton;

import java.util.Objects;

/**
 * Represents the "About" window of the Aloe application.
 * <p>
 * This window displays two sections:
 * <ul>
 *   <li><strong>About</strong> – shows app name, version, description and a link to the repository.</li>
 *   <li><strong>Creator</strong> – shows creator details, inspiration message, and external references.</li>
 * </ul>
 * The content is styled using external CSS files based on the current theme selected in the application settings.
 * </p>
 *
 * <p>
 * This class extends {@link Stage} and creates its own {@link Scene}, using a {@link VBox} layout and a
 * {@link SegmentedButton} to toggle between views.
 * </p>
 *
 * @since 2.3.8
 */
public class AboutWindow extends Stage {

    /**
     * A reference to {@link HostServices} used to open external links in the default browser.
     */
    private final HostServices hostServices;

    /**
     * Constructs and displays the About window.
     *
     * @param hs the {@link HostServices} instance provided by the application, used for opening external links.
     */
    public AboutWindow(HostServices hs) {
        this.hostServices = hs;

        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.getStyleClass().add("background");
        container.getChildren().addAll(
                getSegmentedButtons(container, getAboutContainer(), getCreatorContainer()),
                getAboutContainer()
        );

        Scene scene = new Scene(container, 300, 390);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/structural/about.css")).toExternalForm());

        this.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        this.setScene(scene);
        this.setMaxHeight(390);
        this.setMaxWidth(300);
        this.setTitle(Translator.translate("window.about.title"));
        this.show();
    }

    /**
     * Creates and returns a {@link SegmentedButton} that switches between
     * the "About" and "Creator" content views.
     *
     * @param container        the main container to update when switching views
     * @param aboutContainer   the VBox representing the "About" section
     * @param creatorContainer the VBox representing the "Creator" section
     * @return a configured {@link SegmentedButton} for view navigation
     */
    private SegmentedButton getSegmentedButtons(VBox container, VBox aboutContainer, VBox creatorContainer) {
        ToggleButton aboutButton = new ToggleButton(Translator.translate("window.about.about"));
        aboutButton.setSelected(true);
        aboutButton.getStyleClass().add("nav-button");
        aboutButton.setTextFill(Color.web(Settings.getColor()));

        ToggleButton creatorButton = new ToggleButton(Translator.translate("window.about.creator"));
        creatorButton.getStyleClass().add("nav-button");
        creatorButton.setTextFill(Color.web(Settings.getColor()));

        SegmentedButton segmentedButton = new SegmentedButton(aboutButton, creatorButton);

        aboutButton.setOnMouseClicked(event -> {
            container.getChildren().clear();
            container.getChildren().addAll(segmentedButton, aboutContainer);
        });

        creatorButton.setOnMouseClicked(event -> {
            container.getChildren().clear();
            container.getChildren().addAll(segmentedButton, creatorContainer);
        });

        return segmentedButton;
    }

    /**
     * Builds the "About" section of the window, which includes:
     * <ul>
     *     <li>Application icon</li>
     *     <li>Application name and version</li>
     *     <li>Translated description and external link</li>
     *     <li>Disclaimer or warranty text</li>
     * </ul>
     *
     * @return a {@link VBox} containing the "About" content
     */
    private VBox getAboutContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("background");

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/folder.png"))));
        icon.setFitHeight(120);
        icon.setFitWidth(120);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().addAll("name", "text");

        Label version = new Label("2.7.1");
        version.getStyleClass().addAll("version", "text");

        Label description = new Label(Translator.translate("window.about.description"));
        description.getStyleClass().addAll("description", "text");

        Hyperlink link = createLink(Translator.translate("window.about.website"), "https://github.com/MysiuPysiu76/Aloe");

        Label warranty = new Label(Translator.translate("window.about.warranty"));
        warranty.setTextOverrun(OverrunStyle.CLIP);
        warranty.getStyleClass().addAll("warranty", "text-center", "text");
        warranty.setWrapText(true);

        container.getChildren().addAll(icon, name, version, description, link, warranty);
        container.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    /**
     * Builds the "Creator" section of the window, which includes:
     * <ul>
     *     <li>Icon representing the creator</li>
     *     <li>App name</li>
     *     <li>Inspiration text</li>
     *     <li>Link to creator's profile</li>
     *     <li>Credits for used assets</li>
     * </ul>
     *
     * @return a {@link VBox} containing the "Creator" content
     */
    private VBox getCreatorContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("background");

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/file.png"))));
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().addAll("name", "text");

        Label inspiration = new Label(Translator.translate("window.about.inspiration"));
        inspiration.setTextOverrun(OverrunStyle.CLIP);
        inspiration.getStyleClass().addAll("text-center", "text", "inspiration");
        inspiration.setWrapText(true);

        Hyperlink linkCreator = createLink(Translator.translate("window.about.creator-website"), "https://github.com/MysiuPysiu76");

        Label usedIcons = new Label(Translator.translate("window.about.used-icons"));
        usedIcons.getStyleClass().add("text");

        Hyperlink linkIcons = createLink("Flaticon", "https://www.flaticon.com/");
        linkCreator.setPadding(new Insets(5, 10, 10, 10));

        container.getChildren().addAll(icon, name, inspiration, linkCreator, usedIcons, linkIcons);
        container.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    /**
     * Creates a styled {@link Hyperlink} with the given display text and URL.
     * Opens the URL using {@link HostServices#showDocument(String)} when clicked.
     *
     * @param text the text to display for the link
     * @param url  the URL to open on click
     * @return a styled {@link Hyperlink} component
     */
    private Hyperlink createLink(String text, String url) {
        Hyperlink linkCreator = new Hyperlink(text);
        linkCreator.setTextFill(Color.web(Settings.getColor()));
        linkCreator.getStyleClass().add("text-center");
        linkCreator.setOnAction(event -> hostServices.showDocument(url));
        return linkCreator;
    }
}
