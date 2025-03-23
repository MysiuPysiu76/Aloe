package com.example.aloe;

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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.SegmentedButton;

import java.util.Objects;

public class AboutWindow extends Stage {
    private final HostServices hostServices;

    public AboutWindow(HostServices hs) {
        this.hostServices = hs;
        Stage window = new Stage();
        window.setResizable(false);
        window.initStyle(StageStyle.UNIFIED);

        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(getSegmentedButtons(container, getAboutContainer(), getCreatorContainer()), getAboutContainer());

        Scene scene = new Scene(container, 300, 390);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/style_about.css")).toExternalForm());
        this.setScene(scene);
        this.setTitle(Translator.translate("window.about.title"));
        this.initModality(Modality.APPLICATION_MODAL);
        this.show();
    }

    private SegmentedButton getSegmentedButtons(VBox container, VBox aboutContainer, VBox creatorContainer) {
        ToggleButton aboutButton = new ToggleButton(Translator.translate("window.about.about"));
        aboutButton.setSelected(true);
        aboutButton.setMinWidth(150);

        ToggleButton creatorButton = new ToggleButton(Translator.translate("window.about.creator"));
        creatorButton.setMinWidth(150);
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

    private VBox getAboutContainer() {
        VBox aboutContainer = new VBox();
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/folder.png"))));
        icon.setFitHeight(120);
        icon.setFitWidth(120);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().add("about-name");
        name.setPadding(new Insets(25, 10, 5, 10));

        Label version = new Label("1.3.5");
        version.getStyleClass().add("about-version");

        Label description = new Label(Translator.translate("window.about.description"));
        description.getStyleClass().add("about-description");

        Hyperlink link = createLink(Translator.translate("window.about.website"), "https://github.com/MysiuPysiu76/Aloe");

        Label warranty = new Label(Translator.translate("window.about.warranty"));
        warranty.setTextOverrun(OverrunStyle.CLIP);
        warranty.setMaxWidth(250);
        warranty.setAlignment(Pos.CENTER);
        warranty.getStyleClass().addAll("about-warranty", "text-center");
        warranty.setWrapText(true);

        aboutContainer.getChildren().addAll(icon, name, version, description, link, warranty);
        aboutContainer.setAlignment(Pos.TOP_CENTER);
        return aboutContainer;
    }

    private VBox getCreatorContainer() {
        VBox creatorContainer = new VBox();
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/file.png"))));
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().add("about-name");
        name.setPadding(new Insets(25, 10, 5, 10));

        Label inspiration = new Label(Translator.translate("window.about.inspiration"));
        inspiration.setPadding(new Insets(25, 10, 10, 10));
        inspiration.setTextOverrun(OverrunStyle.CLIP);
        inspiration.setMaxWidth(250);
        inspiration.setAlignment(Pos.CENTER);
        inspiration.getStyleClass().add("text-center");
        inspiration.setWrapText(true);

        Hyperlink linkCreator = createLink(Translator.translate("window.about.creator-website"), "https://github.com/MysiuPysiu76");

        Label usedIcons = new Label(Translator.translate("window.about.used-icons"));

        Hyperlink linkIcons = createLink("Flaticon", "https://www.flaticon.com/");
        linkCreator.setPadding(new Insets(5, 10, 10, 10));

        creatorContainer.getChildren().addAll(icon, name, inspiration, linkCreator, usedIcons, linkIcons);
        creatorContainer.setAlignment(Pos.TOP_CENTER);
        return creatorContainer;
    }

    private Hyperlink createLink(String text, String url) {
        Hyperlink linkCreator = new Hyperlink(text);
        linkCreator.getStyleClass().add("text-center");
        linkCreator.setOnAction(event -> hostServices.showDocument(url));
        return linkCreator;
    }
}