package com.example.aloe.elements;

import com.example.aloe.Main;
import com.example.aloe.Translator;
import com.example.aloe.WindowComponents;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class NavigationPanel extends HBox {

    public NavigationPanel() {
        this.setPadding(new Insets(5, 8, 5, 8));
        this.getChildren().addAll(getPreviousButton(), getNextButton(), getParentButton(), getRefreshButton(), WindowComponents.getSpacer(), getViewButton(), getOptionsButton());
    }

    private Button getNavigationButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setMinSize(40, 40);
        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
        button.setStyle("-fx-background-color: transparent");
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
        button.setOnMouseClicked(e -> new Main().getParentDirectory());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.parent")));
        return button;
    }

    private Button getRefreshButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.REPEAT, 20));
        HBox.setMargin(button, new Insets(0, 5, 0, 0));
        button.setOnMouseClicked(e -> new Main().refreshCurrentDirectory());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.reload")));
        return button;
    }

    private Button getViewButton() {
        Button button = getNavigationButton();
        final boolean[] isGrid = {SettingsManager.getSetting("files", "view").equals("grid")};
        button.setGraphic(getIcon(isGrid[0] ? FontAwesome.TH_LARGE : FontAwesome.LIST_UL, 20));
        button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
        button.setOnMouseClicked(e -> {
            isGrid[0] = !isGrid[0];
            button.setGraphic(getIcon(isGrid[0] ? FontAwesome.TH_LARGE : FontAwesome.LIST_UL, 20));
            button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
            SettingsManager.setSetting("files", "view", isGrid);
            new Main().refreshCurrentDirectory();
        });
        return button;
    }

    private Button getOptionsButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.NAVICON, 20));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.options")));
        button.setOnMouseClicked(e -> Main.showOptions(button));
        return button;
    }

    private FontIcon getIcon(FontAwesome iconName, int size) {
        FontIcon icon = FontIcon.of(iconName);
        icon.setIconSize(size);
        return icon;
    }
}