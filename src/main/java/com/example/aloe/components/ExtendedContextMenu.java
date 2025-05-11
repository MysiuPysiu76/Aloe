package com.example.aloe.components;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.stage.Stage;

/**
 * An extended version of {@link ContextMenu} that adjusts its position
 * to ensure it remains fully visible within the bounds of the application window.
 * <p>
 * This component is especially useful in fullscreen or resizable UIs where
 * standard context menus may overflow the visible area.
 * </p>
 *
 * <p>
 * Before using {@code ExtendedContextMenu}, the main application {@link Stage}
 * must be provided via {@link #setStage(Stage)} so the menu can correctly calculate its bounds.
 * </p>
 *
 * <p>
 * The menu automatically recalculates its position after initial display, and
 * shifts left or up if it would otherwise go beyond the window's right or bottom edge.
 * </p>
 *
 * <pre>{@code
 * ExtendedContextMenu contextMenu = new ExtendedContextMenu();
 * contextMenu.getItems().addAll(...);
 * ExtendedContextMenu.setStage(primaryStage);
 * contextMenu.show(node, x, y);
 * }</pre>
 *
 * @see javafx.scene.control.ContextMenu
 * @since 1.8.9
 */
public class ExtendedContextMenu extends ContextMenu {

    private static Stage stage;
    private static final double ITEM_HEIGHT = 34;

    /**
     * Sets the reference stage used for calculating screen bounds when showing the context menu.
     *
     * @param s the main application {@link Stage}
     */
    public static void setStage(Stage s) {
        stage = s;
    }

    /**
     * Constructs a new {@code ExtendedContextMenu} with default settings and applies
     * the CSS style class {@code "extended-context-menu"} for custom styling.
     * <p>
     * This constructor initializes the context menu without any items.
     * To function correctly within the application window bounds, the main application
     * stage must be set beforehand via {@link #setStage(Stage)}.
     * </p>
     */
    public ExtendedContextMenu() {
        super();
        this.getStyleClass().add("extended-context-menu");
    }

    /**
     * Shows the context menu at a given location, but automatically adjusts its position
     * to stay within the visible area of the application window.
     *
     * @param node  the associated node
     * @param userX the requested X coordinate
     * @param userY the requested Y coordinate
     */
    @Override
    public void show(Node node, double userX, double userY) {
        super.show(node, userX, userY);
        double menuWidth = this.getWidth();
        double menuHeight = getItems().size() * ITEM_HEIGHT;

        this.hide();

        double newX = calculateX(userX, menuWidth);
        double newY = calculateY(userY, menuHeight);

        super.show(node, newX, newY);
    }

    /**
     * Calculates the horizontal position to prevent the menu from overflowing to the right.
     */
    private double calculateX(double clickX, double menuWidth) {
        double windowX = stage.getX();
        double windowWidth = stage.getWidth();

        if (clickX + menuWidth > windowX + windowWidth) {
            return windowX + windowWidth - menuWidth;
        }
        return clickX;
    }

    /**
     * Calculates the vertical position to prevent the menu from overflowing
     * beyond the bottom or top edge of the application window.
     */
    private double calculateY(double clickY, double menuHeight) {
        double windowY = stage.getY();
        double windowHeight = stage.getHeight();

        if (clickY + menuHeight > windowY + windowHeight) {
            clickY = clickY - menuHeight;
        }

        if (clickY < windowY) {
            clickY = windowY;
        }
        return clickY;
    }
}
