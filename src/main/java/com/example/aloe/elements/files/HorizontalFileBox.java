package com.example.aloe.elements.files;

import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.files.properties.FileProperties;
import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import oshi.software.os.OSFileStore;

import java.io.File;

/**
 * A specialized {@link FileBox} implementation that presents file or disk information
 * in a horizontal layout, designed primarily for list views in a file manager interface.
 *
 * <p>This component supports:
 * <ul>
 *     <li>Display of file name, size, and modification date in a horizontal format</li>
 *     <li>Display of disk volumes with available and total space</li>
 *     <li>Lock icon for inaccessible disk mounts</li>
 *     <li>Custom layout styling and responsiveness</li>
 * </ul>
 * </p>
 *
 * @since 2.8.5
 */
class HorizontalFileBox extends FileBox {

    /** The main horizontal container holding file or disk information. */
    private final HBox content = new HBox();

    /**
     * Constructs a {@code HorizontalFileBox} representing a file or folder.
     *
     * @param file the file or directory to represent
     */
    HorizontalFileBox(File file) {
        super(file);
        initContent();

        Label name = getName();
        name.setMaxWidth(Double.MAX_VALUE);
        name.setAlignment(Pos.CENTER_LEFT);
        FileProperties fileProperties = new FileProperties(this.getFile());
        VBox.setMargin(this, new Insets(1, 15, 2, 15));

        this.content.getChildren().addAll(getImageBox(30, new Insets(2, 10, 2, 10)), name, new HBoxSpacer(), getModified(fileProperties), getSize(fileProperties));
        this.getChildren().add(content);
    }

    /**
     * Constructs a {@code HorizontalFileBox} representing a mounted disk.
     *
     * @param store the disk to represent
     */
    HorizontalFileBox(OSFileStore store) {
        super(store);
        initContent();

        Label name = getName();
        name.setMaxWidth(Double.MAX_VALUE);
        name.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(this, new Insets(1, 15, 2, 15));

        this.content.getChildren().addAll(getImageBox(30, new Insets(2, 10, 2, 10), "disk"), name, new HBoxSpacer(), getAvailableSpace());
        this.getChildren().add(content);

        if (new File(this.store.getMount()).listFiles() == null) {
            FontIcon icon = FontIcon.of(FontAwesome.LOCK);
            icon.getStyleClass().add("font-icon-red");
            icon.setIconSize(17);
            HBox.setMargin(icon, new Insets(5));
            this.content.getChildren().add(2, icon);
        }
    }

    /**
     * Initializes the main HBox layout for the file or disk content.
     * Sets padding, spacing, and resizes with the parent container.
     */
    private void initContent() {
        this.content.setMinHeight(35 * scale);
        this.content.setPadding(new Insets(7));
        this.content.setAlignment(Pos.CENTER);
        this.content.setSpacing(5 * scale);
        this.widthProperty().addListener((ob, ol, ne) -> this.content.setMinWidth(Double.parseDouble(ne.toString())));
    }

    /**
     * Creates the header info panel used in the list view.
     *
     * @return an HBox containing name, modified date, and size labels
     */
    static HBox getInfoPanel() {
        HBox box = new HBox();
        box.setPadding(new Insets(7, 72, 7, 75));

        Label name = getInfoLabel("window.file-box.name");
        Label modified = getInfoLabel("window.file-box.modified");
        modified.setMinWidth(148);

        Label size = getInfoLabel("window.file-box.size");
        size.setMinWidth(60);
        size.setAlignment(Pos.CENTER);

        box.getChildren().addAll(name, new HBoxSpacer(), modified, size);
        return box;
    }

    /**
     * Returns a label showing available and total disk space.
     *
     * @return label containing formatted disk space information
     */
    private Label getAvailableSpace() {
        Label label = new Label(String.format("%s / %s %s", UnitConverter.convert(this.store.getTotalSpace()), UnitConverter.convert(this.store.getUsableSpace()), Translator.translate("utils.available-space")));
        label.setPadding(new Insets(0, 15, 0, 0));
        label.setStyle("-fx-font-weight: bold");
        label.getStyleClass().add("text");
        return label;
    }

    /**
     * Returns a translated and styled label for column headers in the info panel.
     *
     * @param key the translation key
     * @return a styled {@code Label} with translated text
     */
    private static Label getInfoLabel(String key) {
        Label label = new Label(Translator.translate(key));
        label.setStyle("-fx-font-weight: bold");
        label.getStyleClass().add("text");
        return label;
    }

    /**
     * Creates a label showing the last modified time of a file.
     *
     * @param properties file properties to extract modification time
     * @return label with formatted modified date
     */
    private Label getModified(FileProperties properties) {
        Label label = new Label(properties.getModifiedTime());
        label.setMinWidth(140);
        label.getStyleClass().add("text");
        return label;
    }

    /**
     * Creates a label showing the size of the file in a short human-readable format.
     *
     * @param properties file properties to extract size
     * @return label with formatted size
     */
    private Label getSize(FileProperties properties) {
        Label size = new Label(properties.getShortSize());
        size.setMinWidth(80);
        size.getStyleClass().add("text");
        HBox.setMargin(size, new Insets(0, 15, 0, 20));
        return size;
    }
}