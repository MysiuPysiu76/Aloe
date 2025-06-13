package com.example.aloe.elements.menu;

import com.example.aloe.components.draggable.ObjectProperties;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.Translator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Map;

/**
 * Represents a customizable menu item component within the application's user interface.
 * <p>
 * Each {@code MenuItem} can display an icon, a title, and is bound to a file path.
 * Clicking the item with the primary mouse button will open the associated file or folder.
 * A context menu is also provided for additional operations.
 * </p>
 *
 * <p>
 * Icon and text visibility, as well as icon alignment (left or right),
 * are determined by user-configurable settings under the "menu" section.
 * </p>
 *
 * <p>
 * This class also implements {@link ObjectProperties} for property serialization and display.
 * </p>
 *
 * @since 2.7.5
 */
public class MenuItem extends Button implements ObjectProperties {

    private final String icon;
    private final String title;
    private final String path;

    private static final boolean useIcon;
    private static final boolean useText;
    private static final boolean iconOnRight;

    static {
        useIcon = Boolean.TRUE.equals(Settings.getSetting("menu", "use-icon"));
        useText = Boolean.TRUE.equals(Settings.getSetting("menu", "use-text"));
        iconOnRight = "right".equalsIgnoreCase(Settings.getSetting("menu", "icon-position").toString());
    }

    /**
     * Constructs a new {@code MenuItem} with the given icon, title, and associated file path.
     *
     * @param icon  the FontAwesome icon name to display
     * @param title the display name of the menu item
     * @param path  the file system path the item points to
     */
    public MenuItem(String icon, String title, String path) {
        this.icon = icon;
        this.title = title;
        this.path = path;

        initialize();
    }

    /**
     * Initializes the visual and behavioral properties of the menu item.
     * This includes icon display, text display, alignment, click handlers,
     * and context menu registration.
     */
    private void initialize() {
        this.setFocusTraversable(false);
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        this.getStyleClass().addAll("transparent", "text", "menu-option");

        if (useIcon) setupIcon();
        if (useText) this.setText(title);

        this.setContextMenu(new MenuItemContextMenu(this));
        this.setOnContextMenuRequested(e -> Menu.hideContextMenu());
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                FilesOpener.open(new File(path));
            }
        });
    }

    /**
     * Configures and displays the icon using the FontAwesome library,
     * applying user-defined alignment (left or right).
     */
    private void setupIcon() {
        FontIcon fontIcon = FontIcon.of(FontAwesome.valueOf(icon));
        fontIcon.setIconSize(18);
        fontIcon.getStyleClass().add("font-icon");
        fontIcon.setWrappingWidth(20);
        this.setGraphicTextGap(10);
        this.setGraphic(fontIcon);

        if (iconOnRight) {
            this.setAlignment(Pos.CENTER_RIGHT);
            this.setContentDisplay(ContentDisplay.RIGHT);
        } else {
            this.setAlignment(Pos.CENTER_LEFT);
        }
    }

    /**
     * @return the icon identifier (FontAwesome name)
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @return the visible title of the menu item
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the file system path the menu item points to
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns a flat map of properties used for serialization or saving.
     *
     * @return a map containing "name", "icon", and "path"
     */
    @Override
    public Map<String, String> getObjectProperties() {
        return Map.of("name", title, "icon", icon, "path", path);
    }

    /**
     * Returns a localized and user-friendly map of the item's properties,
     * suitable for display in UI views.
     *
     * @return a map with translated keys for title, icon, and path
     */
    @Override
    public Map<String, String> getObjectPropertiesView() {
        return Map.of(
                Translator.translate("menu.title"), title,
                Translator.translate("menu.icon"), icon,
                Translator.translate("menu.path"), path
        );
    }
}
