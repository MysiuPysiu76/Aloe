package com.example.aloe.window.interior.menu;

import com.example.aloe.Translator;
import com.example.aloe.WindowComponents;
import com.example.aloe.window.interior.InteriorWindow;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

class MenuItemWindow extends InteriorWindow {

    protected TextField title;
    protected TextField path;
    protected ComboBox<FontAwesome> icon;

    public MenuItemWindow(String name, String titleText, String pathText, String iconText) {
        confirmButton = getConfirmButton(Translator.translate("window.interior.menu.save"));

        title = getInput(titleText, Translator.translate("window.interior.menu.title"));
        path = getInput(pathText, Translator.translate("window.interior.menu.example-path"));
        icon = getComboBox(iconText);

        this.getChildren().addAll(getTitleLabel(name),
                getInfoLabel(Translator.translate("window.interior.menu.title")),
                title,
                getInfoLabel(Translator.translate("window.interior.menu.path")),
                path,
                getInfoLabel(Translator.translate("window.interior.menu.icon")),
                new HBox(icon),
                getBottomPanel(WindowComponents.getSpacer(), getCancelButton(), confirmButton));
    }

    protected ComboBox getComboBox(String icon) {
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
                            iconView.setIconColor(Color.BLACK);
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
                    iconView.setIconColor(Color.BLACK);
                    setText(item.name());
                    setGraphic(iconView);
                }
            }
        });
        return comboBox;
    }
}