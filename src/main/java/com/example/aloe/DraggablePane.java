package com.example.aloe;

import javafx.scene.control.Button;
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
        this.setMinWidth(width);
        this.setPrefWidth(width);
        this.setMaxWidth(width);
        this.setStyle("-fx-background-color: red");
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    public void add(DraggableItem item) {
        item.setLayoutX(0);
        item.setLayoutY(items.size() * (item.getHeight() + spacing));
        item.setMinWidth(this.getMinWidth());
//        item.setPrefWidth(this.getWidth());
        item.setMaxWidth(this.getMinWidth());
        this.getChildren().add(item);

        item.setOnMousePressed(event -> {
            pressY = event.getSceneY();
            initialY = item.getLayoutY();
            draggableItem = item;
            Pane parent = (Pane) item.getParent();
            if (parent != null) {
                parent.getChildren().remove(item);
                parent.getChildren().add(item);
            }
        });

        item.setOnMouseDragged(event -> {
            if (draggableItem != null) {
                double offset = event.getSceneY() - pressY;
                double newY = initialY + offset;
                double minY = 0;
                double maxY = (items.size() - 1) * (item.getHeight() + spacing);
                if (newY < minY) newY = minY;
                if (newY > maxY) newY = maxY;
                draggableItem.setLayoutY(newY);
            }
        });

        item.setOnMouseReleased(event -> {
            if (draggableItem != null) {
                swapItemsIfNeeded();
                sortItems();
                draggableItem = null;
            }
        });

        item.setOnMouseClicked(event -> {
            if (infoBox != null) {
                this.infoBox.setContent(item.getObject());
            }
        });
        items.add(item);
    }

    public InfoBox getInfoBox() {
        return infoBox;
    }

    public void setInfoBox(InfoBox infoBox) {
        this.infoBox = infoBox;
    }

    private void sortItems() {
        items.sort(Comparator.comparingDouble(Button::getLayoutY));
        double newY = 0;
        for (DraggableItem item : items) {
            item.setLayoutY(newY);
            newY += item.getHeight() + spacing;
        }
    }

    private void executeChangeListener() {
        if (onUserChangeListener != null) {
            onUserChangeListener.onUserChange();
        }
    }

    private void swapItemsIfNeeded() {
        if (draggableItem == null) return;
        executeChangeListener();

        if (draggableItem.getLayoutY() <= 0) {
            items.remove(draggableItem);
            items.addFirst(draggableItem);
            return;
        }

        double maxY = (items.size() - 1) * (draggableItem.getHeight() + spacing);
        if (draggableItem.getLayoutY() >= maxY) {
            items.remove(draggableItem);
            items.add(draggableItem);
            return;
        }

        for (int i = 0; i < items.size() - 1; i++) {
            Button current = items.get(i);
            Button next = items.get(i + 1);
            if (draggableItem == current || draggableItem == next) {
                double midPoint = (current.getLayoutY() + next.getLayoutY() + draggableItem.getHeight()) / 2;
                if (draggableItem.getLayoutY() < midPoint && items.indexOf(draggableItem) > i) {
                    items.remove(draggableItem);
                    items.add(i, draggableItem);
                    break;
                } else if (draggableItem.getLayoutY() >= midPoint && items.indexOf(draggableItem) < i + 1) {
                    items.remove(draggableItem);
                    items.add(i + 1, draggableItem);
                    break;
                }
            }
        }
    }

    public void remove(DraggableItem item) {
        items.remove(item);
        sortItems();
    }

    public void clear() {
        items.clear();
        sortItems();
    }

    public void set(int index, DraggableItem item) {
        items.set(index, item);
        sortItems();
    }

    public List<DraggableItem> getItems() {
        return items;
    }

    public void addLast(DraggableItem item) {
        items.addLast(item);
        sortItems();
    }

    public void addFirst(DraggableItem item) {
        items.addFirst(item);
        sortItems();
    }

    public interface OnUserChangeListener {
        void onUserChange();
    }

    public void setOnUserChange(OnUserChangeListener listener) {
        this.onUserChangeListener = listener;
    }
}