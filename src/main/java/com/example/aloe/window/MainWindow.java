package com.example.aloe.window;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.elements.files.*;
import com.example.aloe.elements.menu.Menu;
import com.example.aloe.elements.navigation.NavigationPanel;
import com.example.aloe.files.FilesOpener;
import com.example.aloe.files.tasks.FileCopyTask;
import com.example.aloe.files.tasks.FileDeleteTask;
import com.example.aloe.files.tasks.FileMoveTask;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.interior.DirectoryWindow;
import com.example.aloe.window.interior.FileWindow;
import com.example.aloe.window.interior.RenameWindow;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;

public class MainWindow {

    private static Stage stage;
    private static Scene scene;
    private static StackPane root;
    private static Pane interiorWindowPane;
    private static SplitPane filesPanel;

    public static void create(Stage primaryStage) {
        stage = primaryStage;
        ExtendedContextMenu.setStage(stage);

        root = new StackPane();
        interiorWindowPane = new Pane();
        interiorWindowPane.setVisible(false);

        filesPanel = new SplitPane(FilesPane.get());
        filesPanel.getStyleClass().add("background");
        SplitPane.setResizableWithParent(FilesPane.get(), true);

        VBox mainContainer = new VBox(new NavigationPanel(), filesPanel);
        VBox.setVgrow(filesPanel, Priority.ALWAYS);
        root.getChildren().addAll(mainContainer, interiorWindowPane);

        loadMenu();
        loadInitialDirectory();
        setupScene();
        setupStage();
    }

    private static void loadInitialDirectory() {
        String folderSetting = Settings.getSetting("files", "start-folder");
        String path = folderSetting.equals("home") ? System.getProperty("user.home") : Settings.getSetting("files", "start-folder-location");
        FilesLoader.load(new File(path));
    }

    private static void setupStage() {
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
        if (!Boolean.TRUE.equals(Settings.getSetting("menu", "use-menu"))) return;

        if (filesPanel.getItems().size() > 1) {
            filesPanel.getItems().removeFirst();
        }

        if (Settings.getSetting("menu", "position").equals("right")) {
            filesPanel.getItems().addLast(Menu.get());
        } else {
            filesPanel.getItems().addFirst(Menu.get());
        }

        filesPanel.setDividerPositions((double) Settings.getSetting("menu", "divider-position"));
        SplitPane.setResizableWithParent(Menu.get(), false);

        filesPanel.getDividers().getFirst().positionProperty().addListener(
                (obs, oldVal, newVal) -> Settings.setSetting("menu", "divider-position", newVal)
        );
    }

    private static void setupScene() {
        double width = Settings.getSetting("other", "width");
        double height = Settings.getSetting("other", "height");

        scene = new Scene(root, width, height);

        String theme = Settings.getTheme();
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + theme + "/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/interior.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/" + theme + "/main.css").toExternalForm());
        scene.getStylesheets().add(MainWindow.class.getResource("/assets/styles/structural/main.css").toExternalForm());

        String accentColor = Settings.getColor();
        scene.getStylesheets().add(String.format("data:text/css, .text-field { -fx-highlight-fill: %s; } .extended-menu-item:hover, .confirm, .radio-button:selected .dot, .progress-bar .bar { -fx-background-color: %s; } .accent-color, .menu-option:hover { -fx-text-fill: %s; }", accentColor, accentColor, accentColor));

        setupShortcuts();
    }

    private static void setupShortcuts() {
        scene.setOnKeyPressed(e -> {
            var selectedFiles = SelectedFileBoxes.getSelectedFiles();

            if (selectedFiles.size() == 1) {
                if (e.getCode() == KeyCode.F2) {
                    new RenameWindow(selectedFiles.getFirst());
                } else if (e.isControlDown() && e.getCode() == KeyCode.O) {
                    FilesOpener.open(selectedFiles.getFirst());
                }
            }

            if (e.isControlDown()) {
                switch (e.getCode()) {
                    case A -> FileBox.selectAllFiles();
                    case C -> ClipboardManager.copyFilesToClipboard(selectedFiles);
                    case X -> ClipboardManager.cutFilesToClipboard(selectedFiles);
                    case V -> new FileCopyTask(Clipboard.getSystemClipboard().getFiles(), true);
                    case R -> FilesLoader.refresh();
                    case SLASH, F1 -> new ShortcutsWindow();
                    case D -> { if (e.isShiftDown()) new DirectoryWindow(); }
                    case F -> { if (e.isShiftDown()) new FileWindow(); }
                }
            }

            if (e.getCode() == KeyCode.DELETE) {
                if (e.isShiftDown()) {
                    new FileDeleteTask(selectedFiles, true);
                } else {
                    new FileMoveTask(selectedFiles, new File((String) Settings.getSetting("files", "trash")), true);
                }
            }

            if (e.getCode() == KeyCode.ESCAPE) {
                SelectedFileBoxes.removeSelection();
                hideDarkeningPlate();
            }
        });
    }
}
