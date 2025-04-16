package com.example.aloe.components;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.stage.Stage;

public class ExtendedContextMenu extends ContextMenu {

    private static Stage stage;
    private static final double ITEM_HEIGHT = 25;

    public static void setStage(Stage s) {
        stage = s;
    }

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

    private double calculateX(double clickX, double menuWidth) {
        double windowX = stage.getX();
        double windowWidth = stage.getWidth();

        if (clickX + menuWidth > windowX + windowWidth) {
            return windowX + windowWidth - menuWidth;
        }
        return clickX;
    }

    private double calculateY(double clickY, double menuHeight) {
        double windowY = stage.getY();
        double windowHeight = stage.getHeight();

        if (clickY + menuHeight > windowY + windowHeight) {
            return clickY - menuHeight;
        }
        return clickY;
    }
}
