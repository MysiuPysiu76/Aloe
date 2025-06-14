package com.example.aloe.elements.navigation;

import com.example.aloe.Main;
import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.components.ResponsivePane;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.elements.files.Sorting;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.settings.SettingsWindow;
import com.example.aloe.utils.Translator;
import com.example.aloe.files.DirectoryHistory;
import com.example.aloe.settings.Settings;
import com.example.aloe.window.AboutWindow;
import com.example.aloe.window.ShortcutsWindow;
import com.example.aloe.window.interior.DirectoryWindow;
import com.example.aloe.window.interior.FileWindow;
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

/**
 * Represents the top navigation panel containing file navigation buttons,
 * sorting, view settings, and access to general options like settings and about.
 *
 * @since 2.8.0
 */
public class NavigationPanel extends HBox {

    /** Toggle group for sorting radio buttons */
    private static ToggleGroup group = new ToggleGroup();

    /** Container for the file path UI */
    private static ResponsivePane filesPath = new ResponsivePane();

    /**
     * Constructs a new NavigationPanel and initializes its layout and components.
     */
    public NavigationPanel() {
        HBoxSpacer leftSpacer = new HBoxSpacer();
        leftSpacer.setMaxWidth(80);
        HBoxSpacer rightSpacer = new HBoxSpacer();
        rightSpacer.setMaxWidth(80);
        filesPath.setMaxHeight(30);
        filesPath.setMaxWidth(Double.MAX_VALUE);
        filesPath.setPadding(new Insets(0));
        filesPath.getStyleClass().add("files-path");

        this.setPadding(new Insets(5, 8, 5, 8));
        this.getStyleClass().add("background");
        initializeLayout();
    }

    /**
     * Initializes the panel layout with all buttons and spacers.
     */
    private void initializeLayout() {
        HBoxSpacer leftSpacer = createSpacer(80);
        HBoxSpacer rightSpacer = createSpacer(80);

        filesPath.setMaxHeight(30);
        filesPath.setMaxWidth(Double.MAX_VALUE);
        filesPath.setPadding(new Insets(0));
        filesPath.getStyleClass().add("files-path");

        this.setPadding(new Insets(5, 8, 5, 8));
        this.getStyleClass().add("background");
        this.getChildren().addAll(
                getPreviousButton(), getNextButton(), getParentButton(), getRefreshButton(),
                leftSpacer, filesPath, rightSpacer,
                getSortButton(), getTasksButton(), getViewButton(), getOptionsButton()
        );
    }

    /**
     * Creates an HBox spacer with the specified width.
     *
     * @param width the width of the spacer
     * @return the configured spacer
     */
    private HBoxSpacer createSpacer(double width) {
        HBoxSpacer spacer = new HBoxSpacer();
        spacer.setMaxWidth(width);
        return spacer;
    }

    /**
     * Updates the path navigation UI based on the current directory.
     */
    public static void updateFilesPath() {
        File currentDirectory = CurrentDirectory.get();
        HBox container = new HBox(getIcon());
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("transparent");
        filesPath.setContent(container);

        String currentPath = currentDirectory.toString();
        String separator = System.getProperty("file.separator");

        if (handleSpecialPaths(currentDirectory, currentPath, container)) {
            return;
        }

        StringBuilder fullPath = new StringBuilder();
        for (String part : currentPath.split(separator)) {
            if (part.isBlank()) continue;
            fullPath.append(separator).append(part);
            container.getChildren().addAll(createPathButton(part, fullPath.toString()), getStroke());
        }

        if (!container.getChildren().isEmpty()) {
            container.getChildren().removeLast();
        }
    }

    /**
     * Handles special paths like trash or home, adding custom buttons or icons.
     *
     * @param dir       the current directory
     * @param path      string representation of the path
     * @param container the UI container
     * @return true if a special path was handled
     */
    private static boolean handleSpecialPaths(File dir, String path, HBox container) {
        if (path.equals(Settings.getSetting("files", "trash"))) {
            container.getChildren().set(0, getIcon(FontAwesome.TRASH));
            container.getChildren().add(createFixedPathButton("menu.trash"));
            return true;
        }
        if (path.equalsIgnoreCase("%disks%")) {
            container.getChildren().set(0, getIcon(FontAwesome.HDD_O));
            container.getChildren().add(createFixedPathButton("menu.disks"));
            return true;
        }
        if (FilesUtils.isRoot(dir)) {
            container.getChildren().addAll(createFixedPathButton(""), createFixedPathButton(""));
            return true;
        }
        if (dir.equals(new File(System.getProperty("user.home")))) {
            container.getChildren().add(createFixedPathButton("menu.home"));
            return true;
        }
        return false;
    }

    /**
     * Creates a clickable button for a path segment.
     *
     * @param text the button label
     * @param path the full path
     * @return the configured button
     */
    private static Button createPathButton(String text, String path) {
        Button button = new Button(text);
        button.setUserData(path);
        button.getStyleClass().addAll("transparent", "cursor-hand", "accent-color");
        HBox.setMargin(button, new Insets(4, 3, 0, 3));
        button.setOnAction(e -> FilesLoader.load(new File(path)));
        return button;
    }

    /**
     * Creates a fixed path button using a translation key.
     *
     * @param translationKey the translation key
     * @return the button with localized text
     */
    private static Button createFixedPathButton(String translationKey) {
        Button button = new Button(Translator.translate(translationKey));
        button.getStyleClass().addAll("transparent", "cursor-hand", "accent-color");
        HBox.setMargin(button, new Insets(4, 3, 0, 3));
        return button;
    }

    /**
     * Returns a font icon representing the current directory (home or disk).
     *
     * @return the font icon
     */
    private static FontIcon getIcon() {
        FontIcon icon = FontIcon.of(FilesUtils.isInHomeDir(CurrentDirectory.get()) ? FontAwesome.HOME : FontAwesome.HDD_O);
        icon.setIconSize(22);
        icon.getStyleClass().add("font-icon");
        HBox.setMargin(icon, new Insets(4, 5, 0, 10));
        return icon;
    }

    /**
     * Returns a font icon with the specified icon type.
     *
     * @param icon the FontAwesome icon
     * @return the configured font icon
     */
    private static FontIcon getIcon(FontAwesome icon) {
        FontIcon fontIcon = getIcon();
        fontIcon.setIconCode(icon);
        return fontIcon;
    }

    /**
     * Returns a separator icon (angle right) for the file path UI.
     *
     * @return the separator node
     */
    private static Node getStroke() {
        FontIcon icon = FontIcon.of(FontAwesome.ANGLE_RIGHT);
        icon.setIconSize(22);
        HBox.setMargin(icon, new Insets(2, 0, 0, 0));
        icon.getStyleClass().add("font-icon");
        return icon;
    }

    /**
     * Creates a standardized navigation button with base styling.
     *
     * @return the navigation button
     */
    private Button getNavigationButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setMinSize(40, 40);
        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
        button.getStyleClass().addAll("nav-button", "transparent");
        return button;
    }

    /**
     * Creates the 'Previous Directory' button.
     */
    private Button getPreviousButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.ANGLE_LEFT, 30));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.previous")));
        button.setOnMouseClicked(e -> DirectoryHistory.loadPreviousDirectory());
        return button;
    }

    /**
     * Creates the 'Next Directory' button.
     */
    private Button getNextButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.ANGLE_RIGHT, 30));
        HBox.setMargin(button, new Insets(0, 5, 0, 5));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.next")));
        button.setOnMouseClicked(e -> DirectoryHistory.loadNextDirectory());
        return button;
    }

    /**
     * Creates the 'Parent Directory' button.
     */
    private Button getParentButton() {
        Button button = getNavigationButton();
        FontIcon icon = getIcon(FontAwesome.ANGLE_UP, 30);
        button.setGraphic(icon);
        button.setPadding(new Insets(0, 0, 10, 0));
        button.setOnMouseClicked(e -> FilesLoader.loadParent());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.parent")));
        return button;
    }

    /**
     * Creates the 'Refresh' button.
     */
    private Button getRefreshButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.REPEAT, 20));
        HBox.setMargin(button, new Insets(0, 15, 0, 0));
        button.setOnMouseClicked(e -> FilesLoader.refresh());
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.reload")));
        return button;
    }

    /**
     * Creates the 'Task Progress' button.
     */
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

    /**
     * Creates the 'Sort' button with sorting options.
     */
    private Button getSortButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.SORT_ALPHA_ASC, 20));
        HBox.setMargin(button, new Insets(0, 5, 0, 15));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.sort")));

        VBox content = createSortContent();

        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        popOver.setContentNode(content);

        button.setOnMouseClicked(e -> popOver.show(button));
        return button;
    }

    /**
     * Creates the sort option UI.
     *
     * @return the VBox containing radio buttons for sorting
     */
    private VBox createSortContent() {
        VBox content = new VBox();
        content.setPadding(new Insets(10, 7, 10, 7));
        content.setSpacing(4);
        content.setAlignment(Pos.TOP_CENTER);
        content.getStyleClass().add("popover-content");

        Label label = new Label(Translator.translate("tooltip.navigate.sort"));
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        label.getStyleClass().add("text");

        content.getChildren().add(label);

        for (Sorting sorting : Sorting.values()) {
            RadioButton radio = getRadioButton(sorting.name().toLowerCase(), group, sorting);
            content.getChildren().add(radio);
            if (sorting == Sorting.safeValueOf(Settings.getSetting("files", "sorting").toString().toUpperCase())) {
                group.selectToggle(radio);
            }
        }

        group.selectedToggleProperty().addListener((obs, old, selected) -> {
            Settings.setSetting("files", "sorting", selected.getUserData().toString());
            FilesLoader.refresh();
        });

        return content;
    }

    /**
     * Creates a sorting radio button.
     */
    private RadioButton getRadioButton(String text, ToggleGroup group, Sorting method) {
        RadioButton radioButton = new RadioButton(Translator.translate("navigation.sorting." + text));
        radioButton.setToggleGroup(group);
        radioButton.setUserData(method);
        radioButton.getStyleClass().add("text");
        return radioButton;
    }

    /**
     * Creates the 'Change View' button (list/grid).
     */
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

    /**
     * Creates the 'Options' button showing popover with additional actions.
     */
    private Button getOptionsButton() {
        Button button = getNavigationButton();
        button.setGraphic(getIcon(FontAwesome.NAVICON, 20));
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate.options")));

        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        popOver.setDetachable(false);
        popOver.getStyleClass().add("background");

        VBox content = createOptionsContent();
        popOver.setContentNode(content);

        button.setOnMouseClicked(e -> popOver.show(button));
        return button;
    }

    /**
     * Creates the content shown in the options popover.
     */
    private VBox createOptionsContent() {
        VBox container = new VBox();
        container.setSpacing(4);
        container.setPadding(new Insets(7, 7, 10, 7));
        container.setAlignment(Pos.TOP_CENTER);
        container.getStyleClass().add("popover-content");

        Button newFile = createOptionButton("context-menu.new-file", () -> new FileWindow());
        Button newFolder = createOptionButton("context-menu.new-folder", () -> new DirectoryWindow());
        CheckBox hiddenFiles = createHiddenFilesCheckBox();
        Button about = createOptionButton("navigate.about-button", () -> new AboutWindow(new Main().getHostServices()));
        Button settings = createOptionButton("navigate.settings", SettingsWindow::new);
        Button shortcuts = createOptionButton("navigate.shortcuts", ShortcutsWindow::new);

        container.getChildren().addAll(newFile, newFolder, hiddenFiles, about, settings, shortcuts);
        return container;
    }

    /**
     * Creates an option button with a localized label and attached action.
     */
    private Button createOptionButton(String translationKey, Runnable action) {
        Button button = new Button(Translator.translate(translationKey));
        button.getStyleClass().addAll("nav-btn", "text");
        button.setOnMouseClicked(e -> action.run());
        return button;
    }

    /**
     * Creates a checkbox to toggle hidden files visibility.
     */
    private CheckBox createHiddenFilesCheckBox() {
        CheckBox checkBox = new CheckBox(Translator.translate("navigate.hidden-files"));
        checkBox.setSelected(Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden")));
        checkBox.setStyle("-fx-mark-color: " + Settings.getColor() + ";");
        checkBox.getStyleClass().addAll("text", "nav-btn", "hidden-files");
        checkBox.setOnAction(e -> {
            Settings.setSetting("files", "show-hidden", checkBox.isSelected());
            FilesLoader.refresh();
        });
        return checkBox;
    }

    /**
     * Returns a styled FontAwesome icon.
     */
    private FontIcon getIcon(FontAwesome iconName, int size) {
        FontIcon icon = FontIcon.of(iconName);
        icon.setIconSize(size);
        icon.getStyleClass().add("font-icon");
        return icon;
    }
}
