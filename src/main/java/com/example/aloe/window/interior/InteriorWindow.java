package com.example.aloe.window.interior;

import com.example.aloe.WindowComponents;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.window.MainWindow;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The {@code InteriorWindow} class provides a base implementation for internal overlay windows
 * within the main application interface. It includes shared UI components and layout logic,
 * and it serves as a superclass for all specialized internal window implementations.
 *
 * <p>Upon instantiation, an {@code InteriorWindow} is centered within the application's
 * main overlay pane and displays a darkening background layer.</p>
 *
 * <p>Subclasses can use the provided utility methods to add standardized input fields,
 * buttons, labels, and layout containers.</p>
 *
 * @see MainWindow
 * @since 2.1.3
 */
public class InteriorWindow extends VBox {

    /**
     * The confirm button typically used to finalize user actions within the window.
     */
    protected Button confirmButton;

    /**
     * Constructs a new {@code InteriorWindow}, adds it to the main interior pane,
     * and centers it on the screen. Also activates a darkening overlay in the background.
     */
    public InteriorWindow() {
        this.getStyleClass().addAll("background", "window");

        MainWindow.getInteriorPane().getChildren().add(this);
        MainWindow.showDarkeningPlate();

        this.layoutXProperty().bind(MainWindow.getInteriorPane().widthProperty().subtract(this.widthProperty()).divide(2));
        this.layoutYProperty().bind(MainWindow.getInteriorPane().heightProperty().subtract(this.heightProperty()).divide(2));
    }

    /**
     * Creates a styled {@code TextField} with initial text and a placeholder prompt.
     *
     * @param text   the initial text value
     * @param prompt the placeholder prompt text
     * @return a configured {@code TextField}
     */
    protected TextField getInput(String text, String prompt) {
        TextField input = new TextField(text);
        input.getStyleClass().addAll("background", "text");
        input.selectAll();
        input.setPromptText(prompt);
        VBox.setMargin(input, new Insets(4, 0, 5, 0));
        return input;
    }

    /**
     * Creates a title-style {@code Label} used to display window titles.
     *
     * @param text the label text
     * @return a styled {@code Label}
     */
    protected Label getTitleLabel(String text) {
        Label title = new Label(text);
        title.getStyleClass().addAll("text", "title");
        return title;
    }

    /**
     * Creates an informational {@code Label}, typically used for helper text or descriptions.
     *
     * @param text the label text
     * @return a styled {@code Label}
     */
    protected Label getInfoLabel(String text) {
        Label title = new Label(text);
        title.getStyleClass().addAll("text", "info");
        return title;
    }

    /**
     * Retrieves a standardized confirm button from {@code WindowComponents}.
     *
     * @param text the button label
     * @return a pre-styled confirm {@code Button}
     */
    protected Button getConfirmButton(String text) {
        return WindowComponents.getConfirmButton(text);
    }

    /**
     * Retrieves a cancel button with predefined styling and behavior.
     * Automatically hides the darkening background overlay when clicked.
     *
     * @return a pre-styled cancel {@code Button}
     */
    protected static Button getCancelButton() {
        Button button = WindowComponents.getCancelButton();
        button.setOnAction(e -> MainWindow.hideDarkeningPlate());
        return button;
    }

    /**
     * Creates a horizontal layout container typically used for bottom control panels.
     * Items are aligned to the right.
     *
     * @param items the UI nodes to include in the panel
     * @return a configured {@code HBox}
     */
    protected HBox getBottomPanel(Node... items) {
        HBox box = new HBox(items);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setPadding(new Insets(13, 0, 0, 0));
        return box;
    }

    /**
     * Hides the overlay and refreshes any necessary file-related data.
     * Typically called when a window is closed or canceled.
     */
    protected void hideOverlay() {
        FilesLoader.refresh();
        MainWindow.hideDarkeningPlate();
    }

    /**
     * Sets the event handler for the confirm button's click event.
     *
     * @param eventHandler the mouse event handler to assign
     */
    protected void setOnConfirm(EventHandler<? super MouseEvent> eventHandler) {
        confirmButton.setOnMouseClicked(eventHandler);
    }

    /**
     * Creates a {@code ComboBox} with the specified list of options.
     * The combo box will expand to fill its horizontal container.
     *
     * @param observableList the list of items to display
     * @return a configured {@code ComboBox}
     */
    protected ComboBox getComboBox(ObservableList observableList) {
        ComboBox comboBox = new ComboBox(observableList);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBox, Priority.ALWAYS);
        return comboBox;
    }
}
