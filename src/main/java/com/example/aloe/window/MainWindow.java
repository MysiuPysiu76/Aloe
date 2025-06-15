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

/**
 * The {@code MainWindow} class is responsible for creating and managing the main
 * user interface window of the Aloe file manager application.
 *
 * This class handles:
 * <ul>
 *     <li>Initializing the primary JavaFX {@link Stage} and {@link Scene}</li>
 *     <li>Loading and displaying the file browser, navigation, and menu UI</li>
 *     <li>Applying user settings such as theme, colors, and layout</li>
 *     <li>Responding to global keyboard shortcuts for file operations</li>
 *     <li>Managing modal overlays for dialogs and windows</li>
 * </ul>
 *
 * @since 2.8.3
 */
public class MainWindow {

    private static Stage stage;
    private static Scene scene;
    private static StackPane root;
    private static Pane interiorWindowPane;
    private static SplitPane filesPanel;

    /**
     * Creates and initializes the main application window.
     *
     * @param primaryStage the primary JavaFX stage
     */
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

    /**
     * Loads the initial directory based on user settings (home or custom path).
     */
    private static void loadInitialDirectory() {
        String folderSetting = Settings.getSetting("files", "start-folder");
        String path = folderSetting.equals("home") ? System.getProperty("user.home") : Settings.getSetting("files", "start-folder-location");
        FilesLoader.load(new File(path));
    }

    /**
     * Configures and shows the main application window with settings like title,
     * icon, and size persistence on close.
     */
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

    /**
     * Displays the darkening overlay, used for modal or secondary windows.
     */
    public static void showDarkeningPlate() {
        interiorWindowPane.setVisible(true);
    }

    /**
     * Hides the darkening overlay and removes any child content from it.
     */
    public static void hideDarkeningPlate() {
        interiorWindowPane.setVisible(false);
        interiorWindowPane.getChildren().clear();
    }

    /**
     * Returns the overlay pane used for displaying modal or interior windows.
     *
     * @return the interior overlay pane
     */
    public static Pane getInteriorPane() {
        return interiorWindowPane;
    }

    /**
     * Returns the main JavaFX scene for the application.
     *
     * @return the main scene
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Returns the primary stage (window) of the application.
     *
     * @return the primary JavaFX stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Loads the menu component based on user settings and attaches it
     * to either the left or right side of the file panel.
     */
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

    /**
     * Sets up the main application scene including size, stylesheets, and key bindings.
     */
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

    /**
     * Binds global keyboard shortcuts to application actions like file copy,
     * rename, open, delete, and selection.
     */
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
