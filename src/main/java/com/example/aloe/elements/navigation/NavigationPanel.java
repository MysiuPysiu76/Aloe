package com.example.aloe.elements.navigation;

import com.example.aloe.Main;
import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.elements.files.Sorting;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.SettingsWindow;
import com.example.aloe.utils.Translator;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.Settings;
import com.example.aloe.window.AboutWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public class NavigationPanel extends HBox {

    private static ToggleGroup group = new ToggleGroup();
    private static ResponsivePane filesPath = new ResponsivePane();

    public NavigationPanel() {
        HBoxSpacer leftSpacer = new HBoxSpacer();
        leftSpacer.setMaxWidth(80);
        HBoxSpacer rightSpacer = new HBoxSpacer();
        rightSpacer.setMaxWidth(80);
        filesPath.setMaxHeight(30);
        filesPath.setMaxWidth(Double.MAX_VALUE);
        filesPath.setPadding(new Insets(0, 0, 0, 0));
        filesPath.getStyleClass().add("files-path");

        this.setPadding(new Insets(5, 8, 5, 8));
        this.getStyleClass().add("background");
        this.getChildren().addAll(getPreviousButton(), getNextButton(), getParentButton(), getRefreshButton(), leftSpacer, filesPath, rightSpacer, getSortButton(), getTasksButton(), getViewButton(), getOptionsButton());
    }

    public static void updateFilesPath() {
        File currentDirectory = CurrentDirectory.get();

        HBox container = new HBox(getIcon());
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("transparent");
        String separator = System.getProperty("file.separator");
        StringBuilder path = new StringBuilder();

        for (String text : currentDirectory.getAbsolutePath().split(separator)) {
            if (text.isBlank()) continue;
            path.append(separator);
            path.append(text);

            Button button = new Button(text);
            button.setUserData(path.toString());
            button.getStyleClass().addAll("transparent", "cursor-hand", "accent-color");
            HBox.setMargin(button, new Insets(4, 3, 0, 3));
            String pathString = path.toString();
            button.setOnAction(e -> FilesLoader.load(new File(pathString)));

            container.getChildren().addAll(button, getStroke());
        }

        container.getChildren().removeLast();
        filesPath.setContent(container);
    }

    private static FontIcon getIcon() {
        FontIcon icon = FontIcon.of(FilesUtils.isinHomeDir(CurrentDirectory.get()) ? FontAwesome.HOME : FontAwesome.HDD_O);
        icon.setIconSize(22);
        icon.getStyleClass().add("font-icon");
        HBox.setMargin(icon, new Insets(4, 5, 0, 10));
        return icon;
    }

    private static Node getStroke() {
        FontIcon icon = FontIcon.of(FontAwesome.ANGLE_RIGHT);
        icon.setIconSize(22);
        HBox.setMargin(icon, new Insets(2, 0, 0, 0));
        icon.getStyleClass().add("font-icon");
        return icon;
    }

    private Button getNavigationButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setMinSize(40, 40);
        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
        button.getStyleClass().addAll("nav-button", "transparent");
        return button;
    }

    private Button getPreviousButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.ANGLE_LEFT, 30));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.previous")));
        button.setOnMouseClicked(e -> DirectoryHistory.loadPreviousDirectory());
        return button;
    }

    private Button getNextButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.ANGLE_RIGHT, 30));
        HBox.setMargin(button, new Insets(0, 5, 0, 5));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.next")));
        button.setOnMouseClicked(e -> DirectoryHistory.loadNextDirectory());
        return button;
    }

    private Button getParentButton() {
        Button button = getNavigationButton();
        FontIcon icon = getIcon(FontAwesome.ANGLE_UP, 30);
        button.setGraphic(icon);
        button.setPadding(new Insets(0, 0, 10, 0));
        button.setOnMouseClicked(e -> FilesLoader.loadParent());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.parent")));
        return button;
    }

    private Button getRefreshButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.REPEAT, 20));
        HBox.setMargin(button, new Insets(0, 5, 0, 0));
        button.setOnMouseClicked(e -> FilesLoader.refresh());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.reload")));
        return button;
    }

    private Button getTasksButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.HOURGLASS_HALF, 20));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.tasks")));
        button.setOnMouseClicked(e -> {
            ProgressManager progressManager = new ProgressManager();
            progressManager.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            progressManager.setDetachable(false);
            progressManager.show(button);
        });
        return button;
    }

    private Button getSortButton() {
        Button button = getNavigationButton();
        HBox.setMargin(button, new Insets(0, 5, 0, 15));
        button.setGraphic(getIcon(FontAwesome.SORT_ALPHA_ASC, 20));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.sort")));

        RadioButton nameAsc = getRadioButton("name-asc", group, Sorting.NAMEASC);
        RadioButton nameDesc = getRadioButton("name-desc", group, Sorting.NAMEDESC);
        RadioButton dateAsc = getRadioButton("date-asc", group, Sorting.DATEASC);
        RadioButton dateDesc = getRadioButton("date-desc", group, Sorting.DATEDESC);
        RadioButton sizeAsc = getRadioButton("size-asc", group, Sorting.SIZEASC);
        RadioButton sizeDesc = getRadioButton("size-desc", group, Sorting.SIZEDESC);

        Label choseSortingLabel = new Label(Translator.translate("tooltip.navigate.sort"));
        choseSortingLabel.setPadding(new Insets(2, 5, 5, 5));
        choseSortingLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        choseSortingLabel.getStyleClass().add("text");
        VBox content = new VBox(choseSortingLabel, nameAsc, nameDesc, dateAsc, dateDesc, sizeAsc, sizeDesc);
        content.getStyleClass().add("popover-content");
        content.setFillWidth(true);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10, 7, 10, 7));

        switch (Sorting.safeValueOf(Settings.getSetting("files", "sorting").toString().toUpperCase())) {
            case NAMEASC -> group.selectToggle(nameAsc);
            case NAMEDESC -> group.selectToggle(nameDesc);
            case DATEASC -> group.selectToggle(dateAsc);
            case DATEDESC -> group.selectToggle(dateDesc);
            case SIZEASC -> group.selectToggle(sizeAsc);
            case SIZEDESC -> group.selectToggle(sizeDesc);
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Settings.setSetting("files", "sorting", group.getSelectedToggle().getUserData().toString());
            FilesLoader.refresh();
        });

        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        popOver.setContentNode(content);

        button.setOnMouseClicked(e -> popOver.show(button));
        return button;
    }

    private RadioButton getRadioButton(String text, ToggleGroup group, Sorting method) {
        RadioButton radioButton = new RadioButton(Translator.translate("navigation.sorting." + text));
        radioButton.setToggleGroup(group);
        radioButton.setUserData(method);
        radioButton.getStyleClass().addAll("text", "r");
        return radioButton;
    }

    private Button getViewButton() {
        Button button = getNavigationButton();
        final boolean[] isGrid = {Settings.getSetting("files", "view").equals("grid")};
        button.setGraphic(getIcon(isGrid[0] ? FontAwesome.LIST_UL : FontAwesome.TH_LARGE, 20));
        button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
        HBox.setMargin(button, new Insets(0, 5, 0, 5));
        button.setOnMouseClicked(e -> {
            isGrid[0] = !isGrid[0];
            button.setGraphic(getIcon(isGrid[0] ? FontAwesome.LIST_UL : FontAwesome.TH_LARGE, 20));
            button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
            Settings.setSetting("files", "view", (isGrid[0] ? "grid" : "list"));
            FilesLoader.refresh();
        });
        return button;
    }

    private Button getOptionsButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.NAVICON, 20));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.options")));
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        popOver.setDetachable(false);
        popOver.getStyleClass().add("background");
        VBox container = new VBox();
        container.getStyleClass().add("popover-content");
        container.setAlignment(Pos.TOP_CENTER);
        container.setSpacing(3);
        container.setPadding(new Insets(7, 7, 10, 7));
        CheckBox showHiddenFiles = new CheckBox(Translator.translate("navigate.hidden-files"));
        VBox.setMargin(showHiddenFiles, new Insets(5, 10, 5, 10));
        showHiddenFiles.setSelected(Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden")));
        showHiddenFiles.setStyle("-fx-mark-color: " + Settings.getColor() + ";");
        showHiddenFiles.setOnAction(event -> {
            Settings.setSetting("files", "show-hidden", showHiddenFiles.isSelected());
            FilesLoader.refresh();
        });
        showHiddenFiles.getStyleClass().add("text");
        Button aboutButton = new Button(Translator.translate("navigate.about-button"));
        aboutButton.setOnMouseClicked(event -> new AboutWindow(new Main().getHostServices()));
        aboutButton.getStyleClass().addAll("nav-btn", "text");
        Button settingsButton = new Button(Translator.translate("navigate.settings"));
        settingsButton.getStyleClass().addAll("nav-btn", "text");
        settingsButton.setOnMouseClicked(event -> new SettingsWindow().show());
        container.getChildren().addAll(showHiddenFiles, aboutButton, settingsButton);
        popOver.setContentNode(container);
        button.setOnMouseClicked(e -> {
            popOver.show(button);
        });
        return button;
    }

    private FontIcon getIcon(FontAwesome iconName, int size) {
        FontIcon icon = FontIcon.of(iconName);
        icon.setIconSize(size);
        icon.getStyleClass().add("font-icon");
        return icon;
    }
}
