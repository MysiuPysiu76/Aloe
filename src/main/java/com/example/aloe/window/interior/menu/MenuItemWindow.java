package com.example.aloe.window.interior.menu;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.interior.InteriorWindow;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * {@code MenuItemWindow} is a specialized internal window used for creating or editing
 * custom menu entries. It allows the user to specify a title, a file or URL path,
 * and select an icon from the {@link FontAwesome} set.
 *
 * <p>This class extends {@link InteriorWindow} and assembles a structured form layout
 * including input fields and a styled combo box with icon previews.</p>
 *
 * <p>The icon selection list is visually enhanced with {@link FontIcon} components to
 * preview each icon next to its identifier. All labels and prompts are localized using
 * {@link Translator}.</p>
 *
 * @see InteriorWindow
 */
class MenuItemWindow extends InteriorWindow {

    /** Text field for the menu item title */
    protected TextField title;

    /** Text field for the path (e.g., file path, URL) */
    protected TextField path;

    /** ComboBox for selecting a FontAwesome icon */
    protected ComboBox<FontAwesome> icon;

    /**
     * Constructs a {@code MenuItemWindow} allowing the user to edit or define a menu entry.
     *
     * @param name the window title (localized)
     * @param titleText the initial text for the title input field
     * @param pathText the initial text for the path input field
     * @param iconText the identifier of the initially selected FontAwesome icon
     */
    public MenuItemWindow(String name, String titleText, String pathText, String iconText) {
        confirmButton = getConfirmButton(Translator.translate("window.interior.menu.save"));

        title = getInput(titleText, Translator.translate("window.interior.menu.title"));
        title.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));

        path = getInput(pathText, Translator.translate("window.interior.menu.example-path"));
        path.setStyle(String.format("-fx-border-color: %s;", Settings.getColor()));

        icon = getComboBox(iconText);
        icon.setStyle(String.format("-fx-border-color: %s; -fx-border-radius: 10px", Settings.getColor()));

        this.getChildren().addAll(
                getTitleLabel(name),
                getInfoLabel(Translator.translate("window.interior.menu.title")),
                title,
                getInfoLabel(Translator.translate("window.interior.menu.path")),
                path,
                getInfoLabel(Translator.translate("window.interior.menu.icon")),
                new HBox(icon),
                getBottomPanel(new HBoxSpacer(), getCancelButton(), confirmButton)
        );
    }

    /**
     * Creates and configures a ComboBox for selecting a FontAwesome icon.
     * The list is styled with graphical previews using {@link FontIcon}.
     *
     * @param icon the name of the icon to pre-select
     * @return a styled {@link ComboBox} populated with FontAwesome icons
     */
    protected ComboBox<FontAwesome> getComboBox(String icon) {
        ObservableList<FontAwesome> icons = FXCollections.observableArrayList(FontAwesome.values());
        ComboBox<FontAwesome> comboBox = getComboBox(icons);

        comboBox.setValue(FontAwesome.valueOf(icon));
        comboBox.setVisibleRowCount(8);

        comboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FontAwesome> call(ListView<FontAwesome> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FontAwesome item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            FontIcon iconView = new FontIcon(item);
                            iconView.setIconSize(20);
                            iconView.getStyleClass().add("font-icon");
                            setText(item.name());
                            setGraphic(iconView);
                        }
                    }
                };
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FontAwesome item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontIcon iconView = new FontIcon(item);
                    iconView.setIconSize(20);
                    iconView.getStyleClass().add("font-icon");
                    setText(item.name());
                    setGraphic(iconView);
                }
            }
        });

        return comboBox;
    }
}
