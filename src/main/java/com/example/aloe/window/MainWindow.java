package com.example.aloe.window;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.elements.files.FileBox;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.elements.files.FilesPane;
import com.example.aloe.elements.menu.Menu;
import com.example.aloe.elements.navigation.NavigationPanel;
import com.example.aloe.files.tasks.FileCopyTask;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.interior.RenameWindow;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class MainWindow {

    private static Stage stage;
    private static Scene scene;
    private static StackPane root;
    private static Pane interiorWindowPane;
    private static SplitPane filesPanel;

    public static void create(Stage stage1) {
        ExtendedContextMenu.setStage(stage1);
        stage = stage1;
        root = new StackPane();
        interiorWindowPane = new Pane();

        filesPanel = new SplitPane(FilesPane.get());
        filesPanel.getStyleClass().add("background");
        SplitPane.setResizableWithParent(FilesPane.get(), true);

        loadMenu();

        FilesLoader.load(new File(Settings.getSetting("files", "start-folder").equals("home") ? System.getProperty("user.home") : Settings.getSetting("files", "start-folder-location")));

        VBox mainContainer = new VBox(new NavigationPanel(), filesPanel);
        VBox.setVgrow(filesPanel, Priority.ALWAYS);
        interiorWindowPane.setVisible(false);
        root.getChildren().addAll(mainContainer, interiorWindowPane);
        setUpScene();
        stage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("/assets/icons/folder.png")));
        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            Settings.setSetting("other", "height", stage.getHeight());
            Settings.setSetting("other", "width", stage.getWidth());
        });
    }

    public static void showDarkeningPlate() {
        interiorWindowPane.setVisible(true);
    }

    public static void hideDarkeningPlate() {
        interiorWindowPane.setVisible(false);
        interiorWindowPane.getChildren().clear();
    }

    public static Pane getInteriorPane() {
        return interiorWindowPane;
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void loadMenu() {
        if (Boolean.TRUE.equals(Settings.getSetting("menu", "use-menu"))) {
            if (Settings.getSetting("menu", "position").equals("right")) {
                if (filesPanel.getItems().size() > 1) {
                    filesPanel.getItems().removeFirst();
                }
                filesPanel.getItems().addLast(Menu.get());
            } else {
                if (filesPanel.getItems().size() > 1) {
                    filesPanel.getItems().removeFirst();
                }
                filesPanel.getItems().addFirst(Menu.get());
            }

            filesPanel.setDividerPositions((double) Settings.getSetting("menu", "divider-position"));
            SplitPane.setResizableWithParent(Menu.get(), false);
            SplitPane.Divider divider = filesPanel.getDividers().getFirst();
            divider.positionProperty().addListener(((observableValue, number, t1) -> Settings.setSetting("menu", "divider-position", t1)));
        }
    }

    private static void setUpScene() {
        scene = new Scene(root, Settings.getSetting("other", "width"), Settings.getSetting("other", "height"));
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/interior.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + Settings.getTheme() + "/main.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/main.css").toExternalForm());
        scene.getStylesheets().add(String.format("data:text/css, .text-field { -fx-highlight-fill: %s; } .extended-menu-item:hover, .confirm, .radio-button:selected .dot, .progress-bar .bar { -fx-background-color: %s; } .accent-color, .menu-option:hover { -fx-text-fill: %s; }", Settings.getColor(), Settings.getColor(), Settings.getColor()));

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F2) {
               if (FileBox.getSelectedFiles().size() == 1) {
                   new RenameWindow(FileBox.getSelectedFiles().getFirst());
               }
            }
            if (e.isControlDown() && e.getCode() == KeyCode.A) {
                FileBox.selectAllFiles();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.C) {
                ClipboardManager.copyFilesToClipboard(FileBox.getSelectedFiles());
            }
            if (e.isControlDown() && e.getCode() == KeyCode.X) {
                ClipboardManager.cutFilesToClipboard(FileBox.getSelectedFiles());
            }
            if (e.isControlDown() && e.getCode() == KeyCode.V) {
                new FileCopyTask(Clipboard.getSystemClipboard().getFiles(), true);
            }
            if ((e.isControlDown() && e.getCode() == KeyCode.R) || e.getCode() == KeyCode.F5) {
                FilesLoader.refresh();
            }
            if (e.getCode() == KeyCode.DELETE) {
                new FileMoveTask(FileBox.getSelectedFiles(), new File(Settings.getSetting("files", "trash").toString()), true);
            }
            if (e.isShiftDown() && e.getCode() == KeyCode.DELETE) {
                new FileDeleteTask(FileBox.getSelectedFiles(), true);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                FileBox.removeSelection();
                hideDarkeningPlate();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.SLASH) {
                new ShortcutsWindow();
            }
        });
    }
}
