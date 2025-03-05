package com.example.aloe.components;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DraggablePane extends Pane {

    private final List<DraggableItem> items = new LinkedList<>();
    private double pressY;
    private double initialY;
    private DraggableItem draggableItem;
    private double spacing = 5;
    private OnUserChangeListener onUserChangeListener;

    private InfoBox infoBox;

    public DraggablePane(double width) {
        setPrefSize(width, USE_COMPUTED_SIZE);
        this.setStyle("fx-background-color: rgb(200,48,48);");
    }

    public void add(DraggableItem... items) {
        for (DraggableItem item : items) add(item);
    }

    public void add(List<DraggableItem> items) {
        for (DraggableItem item : items) add(item);
    }

    public void add(DraggableItem item) {
        configureItem(item);
        setupEventHandlers(item);
        items.add(item);
        getChildren().add(item);
    }

    private int calculateNewIndex() {
        for (int i = 0; i < items.size(); i++) {
            if (draggableItem.getLayoutY() < items.get(i).getLayoutY() + (items.get(i).getHeight() / 2)) {
                return i;
            }
        }
        return items.size() - 1;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public void clear() {
        items.clear();
        getChildren().clear();
    }

    private void configureItem(DraggableItem item) {
        item.setLayoutX(0);
        item.setPrefHeight(25);
        item.setLayoutY(items.size() * (item.getPrefHeight() + spacing));
        item.setMinWidth(getPrefWidth());
        item.setMaxWidth(getPrefWidth());
        item.setMinHeight(item.getHeight() + 1);
    }

    public InfoBox getInfoBox() {
        return infoBox;
    }

    public double getSpacing() {
        return spacing;
    }

    public List<DraggableItem> getItems() {
        return items;
    }

    private void handleMousePressed(double sceneY, DraggableItem item) {
        pressY = sceneY;
        initialY = item.getLayoutY();
        draggableItem = item;
        toFront(item);
    }

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

    private void handleMouseReleased() {
        if (draggableItem == null) return;
        swapItemsIfNeeded();
        sortItems();
        draggableItem = null;
        notifyChangeListener();
    }

    private double maxDraggableY() {
        return (items.size() - 1) * (draggableItem.getHeight() + spacing);
    }

    private void notifyChangeListener() {
        if (onUserChangeListener != null) onUserChangeListener.onUserChange();
    }

    private void setupEventHandlers(DraggableItem item) {
        item.setOnMousePressed(event -> handleMousePressed(event.getSceneY(), item));
        item.setOnMouseDragged(event -> handleMouseDragged(event, item));
        item.setOnMouseReleased(event -> handleMouseReleased());
        item.setOnMouseClicked(event -> showInfo(item));
    }

    public void remove(DraggableItem item) {
        items.remove(item);
        sortItems();
    }

    public void setInfoBox(InfoBox infoBox) {
        this.infoBox = infoBox;
    }

    public void setOnUserChange(OnUserChangeListener listener) {
        this.onUserChangeListener = listener;
    }

    private void showInfo(DraggableItem item) {
        if (infoBox != null) infoBox.setContent(item.getObject());
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    private void sortItems() {
        items.sort(Comparator.comparingDouble(Button::getLayoutY));
        double newY = 0;
        for (DraggableItem item : items) {
            item.setLayoutY(newY);
            newY += item.getHeight() + spacing;
        }
    }

    private void swapItemsIfNeeded() {
        if (draggableItem == null) return;
        if (draggableItem.getLayoutY() <= 0) {
            items.remove(draggableItem);
            items.addFirst(draggableItem);
        }
    }

    private void toFront(DraggableItem item) {
        Pane parent = (Pane) item.getParent();
        if (parent != null) {
            parent.getChildren().remove(item);
            parent.getChildren().add(item);
        }
    }

    public interface OnUserChangeListener {
        void onUserChange();
    }
}