package com.example.aloe.elements.navigation;

import com.example.aloe.Main;
import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.utils.Translator;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.SettingsManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class NavigationPanel extends HBox {

    public NavigationPanel() {
        this.setPadding(new Insets(5, 8, 5, 8));
        this.getChildren().addAll(getPreviousButton(), getNextButton(), getParentButton(), getRefreshButton(), new HBoxSpacer(), getTasksButton(), getViewButton(), getOptionsButton());
    }

    private Button getNavigationButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setMinSize(40, 40);
        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
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
            ProgressManager popOver = new ProgressManager();
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            popOver.setDetachable(true);
            popOver.show(button);
        });
        return button;
    }

    private Button getViewButton() {
        Button button = getNavigationButton();
        final boolean[] isGrid = {SettingsManager.getSetting("files", "view").equals("grid")};
        button.setGraphic(getIcon(isGrid[0] ? FontAwesome.LIST_UL: FontAwesome.TH_LARGE, 20));
        button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
        HBox.setMargin(button, new Insets(0, 5, 0, 5));
        button.setOnMouseClicked(e -> {
            isGrid[0] = !isGrid[0];
            button.setGraphic(getIcon(isGrid[0] ? FontAwesome.LIST_UL : FontAwesome.TH_LARGE, 20));
            button.setTooltip(new Tooltip(Translator.translate(isGrid[0] ? "tooltip.navigate.view.grid" : "tooltip.navigate.view.list")));
            SettingsManager.setSetting("files", "view", (isGrid[0] ? "grid" : "list"));
            FilesLoader.refresh();
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