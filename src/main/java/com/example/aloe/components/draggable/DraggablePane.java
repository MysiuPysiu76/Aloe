package com.example.aloe.components.draggable;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A custom JavaFX container for managing and visually rearranging {@link DraggableItem} components
 * through drag-and-drop interaction.
 * <p>
 * Items can be added to the pane and reordered by the user via mouse dragging.
 * The pane maintains an internal list of the items, ensuring they stay aligned vertically
 * with a defined spacing between them.
 * <p>
 * This class handles the layout, reordering logic, drag tracking, and optional callbacks for
 * notifying external components when changes are made by the user.
 *
 * @since 1.5.1
 */
public class DraggablePane extends Pane {

    /**
     *  List of draggable items contained within the pane.
     */
    private final List<DraggableItem> items = new LinkedList<>();

    /**
     * Y-coordinate when mouse was pressed (used for dragging calculation).
     */
    private double pressY;

    /**
     * Initial Y position of the dragged item before drag starts.
     */
    private double initialY;

    /**
     * Currently dragged {@link DraggableItem}.
     */
    private DraggableItem draggableItem;

    /**
     * Vertical spacing between items in the pane.
     */
    private double spacing = 5;

    /**
     * Optional listener triggered when the user reorders items.
     */
    private OnUserChangeListener onUserChangeListener;

    /**
     * Optional UI component to display item-specific information.
     */
    private InfoBox infoBox;

    /**
    * Constructs a DraggablePane with specified preferred width.
    *
    * @param width the preferred width of the pane
    */
    public DraggablePane(double width) {
        setPrefSize(width, USE_COMPUTED_SIZE);
        this.setStyle("fx-background-color: rgb(200,48,48);");
    }

    /**
     * Adds multiple draggable items to the pane.
     *
     * @param items one or more {@link DraggableItem}s to be added
     */
    public void add(DraggableItem... items) {
        for (DraggableItem item : items) add(item);
    }

    /**
     * Adds a list of draggable items to the pane.
     *
     * @param items list of {@link DraggableItem}s to be added
     */
    public void add(List<DraggableItem> items) {
        for (DraggableItem item : items) add(item);
    }

    /**
     * Adds a single draggable item to the pane, configures it and sets up its event handlers.
     *
     * @param item the {@link DraggableItem} to add
     */
    public void add(DraggableItem item) {
        configureItem(item);
        setupEventHandlers(item);
        items.add(item);
        getChildren().add(item);
    }

    /**
     * Determines the new index (position) for the currently dragged item based on its current Y-coordinate.
     * Compares the dragged item's position to the vertical midpoints of existing items to find where it should be inserted.
     *
     * @return the calculated index at which the {@code draggableItem} should be placed
     */
    private int calculateNewIndex() {
        for (int i = 0; i < items.size(); i++) {
            if (draggableItem.getLayoutY() < items.get(i).getLayoutY() + (items.get(i).getHeight() / 2)) {
                return i;
            }
        }
        return items.size() - 1;
    }

    /**
     * Restricts a given value to be within a specified minimum and maximum range.
     *
     * @param value the value to clamp
     * @param min the minimum allowable value
     * @param max the maximum allowable value
     * @return the clamped value between {@code min} and {@code max}
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Removes all items from the pane.
     */
    public void clear() {
        items.clear();
        getChildren().clear();
    }

    /**
     * Configures the layout properties for a given {@link DraggableItem}.
     * This method sets the position, dimensions, and layout properties of the item
     * within the pane, ensuring it is appropriately sized and positioned within the
     * list of items. The item is positioned vertically based on the number of
     * existing items and the specified spacing between items.
     *
     * @param item the {@link DraggableItem} to configure
     */
    private void configureItem(DraggableItem item) {
        item.setLayoutX(0);
        item.getStyleClass().add("text");
        item.setPrefHeight(25);
        item.setLayoutY(items.size() * (item.getPrefHeight() + spacing));
        item.setMinWidth(getPrefWidth());
        item.setMaxWidth(getPrefWidth());
        item.setMinHeight(item.getHeight() + 1);
    }

    /**
     * Returns the currently set InfoBox.
     *
     * @return the InfoBox or null if not set
     */
    public InfoBox getInfoBox() {
        return infoBox;
    }

    /**
     * Returns the current spacing between items.
     *
     * @return vertical spacing value
     */
    public double getSpacing() {
        return spacing;
    }

    /**
     * Returns the list of draggable items in the pane.
     *
     * @return list of items
     */
    public List<DraggableItem> getItems() {
        return items;
    }

    /**
     * Handles the mouse press event when the user starts dragging an item.
     * This method captures the initial Y-coordinate of the mouse press and sets the
     * initial Y-position of the item being dragged. It also makes the dragged item
     * appear in front of other items in the pane.
     *
     * @param sceneY the Y-coordinate of the mouse press in the scene
     * @param item the {@link DraggableItem} that is being pressed
     */
    private void handleMousePressed(double sceneY, DraggableItem item) {
        pressY = sceneY;
        initialY = item.getLayoutY();
        draggableItem = item;
        toFront(item);
    }

    /**
     * Handles the mouse drag event when the user drags an item.
     * This method calculates the new Y-coordinate of the dragged item based on the
     * difference in the mouse's Y-position during the drag. It also ensures that the item
     * stays within the bounds of the pane and does not overlap with other items.
     *
     * @param event the mouse event that provides the current Y-coordinate of the mouse
     * @param item the {@link DraggableItem} that is being dragged
     */
    private void handleMouseDragged(MouseEvent event, DraggableItem item) {
        if (draggableItem != null) {
            this.setMinHeight(this.getHeight());
            double offset = event.getSceneY() - pressY;
            double newY = initialY + offset;
            double minY = 0;
            double maxY = (items.size() - 1) * (item.getHeight() + spacing) + 0.01;
            if (newY < minY) newY = minY;
            if (newY > maxY) newY = maxY;
            draggableItem.setLayoutY(newY);
        }
    }

    /**
     * Handles the mouse release event when the user stops dragging an item.
     * Performs the following actions:
     * <ul>
     *   <li>Checks if the dragged item should be moved to the top of the list.</li>
     *   <li>Sorts the items in their final order based on their Y-coordinate.</li>
     *   <li>Notifies the listener of the change in the item order.</li>
     * </ul>
     */
    private void handleMouseReleased() {
        if (draggableItem == null) return;
        swapItemsIfNeeded();
        sortItems();
        draggableItem = null;
        notifyChangeListener();
    }

    /**
     * Calculates the maximum vertical position (Y-coordinate) the currently dragged item
     * can occupy within the pane without exceeding the layout bounds.
     *
     * @return the maximum Y-coordinate the {@code draggableItem} can be dragged to
     */
    private double maxDraggableY() {
        return (items.size() - 1) * (draggableItem.getHeight() + spacing);
    }

    /**
     * Notifies the registered {@link OnUserChangeListener} that the user has made changes to the item order.
     * If no listener is registered, no action is taken.
     */
    private void notifyChangeListener() {
        if (onUserChangeListener != null) onUserChangeListener.onUserChange();
    }

    /**
     * Sets up the event handlers for a given {@link DraggableItem}.
     * This method binds the necessary mouse event listeners to the item for the following actions:
     * <ul>
     *   <li>Mouse press: Initiates the drag action by capturing the initial position of the item.</li>
     *   <li>Mouse drag: Updates the item's position as it is being dragged.</li>
     *   <li>Mouse release: Finalizes the drag action, swaps items if needed, and sorts the list.</li>
     *   <li>Mouse click: Displays additional information about the item in the associated {@link InfoBox}.</li>
     * </ul>
     *
     * @param item the {@link DraggableItem} for which the event handlers will be set
     */
    private void setupEventHandlers(DraggableItem item) {
        item.setOnMousePressed(event -> handleMousePressed(event.getSceneY(), item));
        item.setOnMouseDragged(event -> handleMouseDragged(event, item));
        item.setOnMouseReleased(event -> handleMouseReleased());
        item.setOnMouseClicked(event -> showInfo(item));
    }

    /**
     * Removes the given item from the pane and updates the layout.
     *
     * @param item the {@link DraggableItem} to remove
     */
    public void remove(DraggableItem item) {
        items.remove(item);
        sortItems();
    }

    /**
     * Sets the {@link InfoBox} to display item-specific content when clicked.
     *
     * @param infoBox the InfoBox to assign
     */
    public void setInfoBox(InfoBox infoBox) {
        this.infoBox = infoBox;
    }

    /**
     * Sets a listener to be notified when user reorders items.
     *
     * @param listener the {@link OnUserChangeListener} to notify
     */
    public void setOnUserChange(OnUserChangeListener listener) {
        this.onUserChangeListener = listener;
    }

    /**
     * Displays additional information about the given {@link DraggableItem} in the associated {@link InfoBox}.
     * If an {@code InfoBox} is set, its content is updated to reflect the properties of the selected item.
     *
     * @param item the {@link DraggableItem} whose information will be displayed
     */
    private void showInfo(DraggableItem item) {
        if (infoBox != null) infoBox.setContent(item.getObject());
    }

    /**
     * Sets the vertical spacing between items.
     *
     * @param spacing the spacing to set
     */
    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    /**
     * Sorts the list of draggable items in ascending order of their Y-coordinate.
     * Adjusts the Y-position of each item to ensure they are arranged correctly in the pane.
     */
    private void sortItems() {
        items.sort(Comparator.comparingDouble(Button::getLayoutY));
        double newY = 0;
        for (DraggableItem item : items) {
            item.setLayoutY(newY);
            newY += item.getHeight() + spacing;
        }
    }

    /**
     * Checks if the currently dragged item has been moved to the top of the pane.
     * If so, moves it to the beginning of the item list.
     * This allows the item to be visually and logically reordered to the top.
     */
    private void swapItemsIfNeeded() {
        if (draggableItem == null) return;
        if (draggableItem.getLayoutY() <= 0) {
            items.remove(draggableItem);
            items.addFirst(draggableItem);
        }
    }

    /**
     * Brings the specified item to the front of its parent container,
     * ensuring it appears above other UI elements during dragging.
     *
     * @param item the {@link DraggableItem} to bring to front
     */
    private void toFront(DraggableItem item) {
        Pane parent = (Pane) item.getParent();
        if (parent != null) {
            parent.getChildren().remove(item);
            parent.getChildren().add(item);
        }
    }

    /**
     * Interface for receiving change events when the user reorders items.
     */
    public interface OnUserChangeListener {
        void onUserChange();
    }
}
