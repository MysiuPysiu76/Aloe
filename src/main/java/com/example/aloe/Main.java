package com.example.aloe;

import com.example.aloe.components.ExtendedContextMenu;
import com.example.aloe.elements.files.DirectoryContextMenu;
import com.example.aloe.elements.files.FileBox;
import com.example.aloe.elements.files.FilesLoader;
import com.example.aloe.elements.navigation.NavigationPanel;
import com.example.aloe.files.CurrentDirectory;
import com.example.aloe.elements.menu.MenuManager;
import com.example.aloe.settings.Settings;
import com.example.aloe.settings.SettingsWindow;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.Translator;
import com.example.aloe.window.AboutWindow;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.util.*;

public class Main extends Application {

    private SplitPane filesPanel = new SplitPane();
    public static ScrollPane filesMenu = new ScrollPane();
    public static ScrollPane filesPane = new ScrollPane();
    public static Scene scene;

    private VBox mainContainer = new VBox();
    private StackPane root = new StackPane();
    public static Pane pane = new Pane();
    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new StackPane();
        filesMenu = new ScrollPane();
        filesPanel = new SplitPane();
        filesPane = new ScrollPane();
        mainContainer = new VBox();
        pane = new Pane();

        ExtendedContextMenu.setStage(stage);
        root.getChildren().addAll(mainContainer, pane);
        mainContainer.getStyleClass().add("root");
        Main.stage = stage;
        scene = new Scene(root, 975, 550);

        filesPanel.getStyleClass().add("navigation-panel");
        filesPane.getStyleClass().add("files-pane");

        Main.filesPane.setFitToHeight(true);
        Main.filesPane.setFitToWidth(true);
        filesPanel.getStyleClass().add("files-panel");
        filesMenu.getStyleClass().add("files-menu");

        VBox.setVgrow(filesPanel, Priority.ALWAYS);

        filesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                directoryMenu.hide();
            }
        });

        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        pane.setVisible(false);

        filesPanel.getItems().add(filesPane);

        if (Boolean.TRUE.equals(Settings.getSetting("menu", "use-menu"))) {
            loadMenu();
            if (Settings.getSetting("menu", "position").equals("right")) {
                filesPanel.getItems().addLast(filesMenu);
            } else {
                filesPanel.getItems().addFirst(filesMenu);
            }
            filesPanel.setDividerPositions((double) Settings.getSetting("menu", "divider-position"));
        }

        SplitPane.setResizableWithParent(filesMenu, false);

        filesPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FileBox.removeSelection();
        });

        filesMenu.setMinWidth(10);
        filesMenu.setPrefWidth(160);
        HBox navigationPanel1 = new NavigationPanel();
        mainContainer.getChildren().addAll(navigationPanel1, filesPanel);

        if (!Objects.equals(Settings.getSetting("files", "start-folder"), "home")) {
            FilesLoader.load(new File((String) Objects.requireNonNull(Settings.getSetting("files", "start-folder-location"))));
        } else {
            FilesLoader.load(new File(System.getProperty("user.home")));
        }

        scene.getStylesheets().add(getClass().getResource("/assets/styles/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + (Settings.getSetting("appearance", "theme").equals("light") ? "light" : "dark") + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + (Settings.getSetting("appearance", "theme").equals("light") ? "light" : "dark") + "/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/interior.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/main.css").toExternalForm());
        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
        new SettingsWindow();


        stage.setOnCloseRequest(event -> {
            Settings.setSetting("menu", "divider-position", filesPanel.getDividerPositions());
            if (Objects.equals(Settings.getSetting("files", "start-folder"), "last")) {
                Settings.setSetting("files", "start-folder-location", CurrentDirectory.get().getAbsolutePath());
            }
        });

        filesPane.setOnContextMenuRequested(event -> {
            FileBox.removeSelection();
            directoryMenu.getItems().get(2).setDisable(ClipboardManager.isClipboardEmpty());
            directoryMenu.show(filesPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public static void hideDarkeningPlate() {
        pane.setVisible(false);
        pane.getChildren().clear();
    }

    public static void loadMenu() {
        VBox menu = MenuManager.getMenu();
        menu.prefWidthProperty().bind(filesMenu.widthProperty());
        menu.prefHeightProperty().bind(filesMenu.heightProperty());
        filesMenu.setContent(menu);
    }

    public static void showOptions(Button button) {
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        CheckBox showHiddenFiles = new CheckBox(Translator.translate("navigate.hidden-files"));
        VBox.setMargin(showHiddenFiles, new Insets(5, 10, 5, 10));
        showHiddenFiles.setSelected(Boolean.TRUE.equals(Settings.getSetting("files", "show-hidden")));
        showHiddenFiles.setOnAction(event -> {
            Settings.setSetting("files", "show-hidden", showHiddenFiles.isSelected());
            FilesLoader.refresh();
        });
        Button aboutButton = new Button(Translator.translate("navigate.about-button"));
        aboutButton.setOnMouseClicked(e -> new AboutWindow(new Main().getHostServices()));
        Button settingsButton = new Button(Translator.translate("navigate.settings"));
        settingsButton.setOnMouseClicked(e -> new SettingsWindow().show());
        container.getChildren().addAll(showHiddenFiles, aboutButton, settingsButton);
        popOver.setContentNode(container);
            if (popOver.isShowing()) {
                popOver.hide();
            } else {
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                popOver.show(button);
            }
    }

    public static DirectoryContextMenu directoryMenu = new DirectoryContextMenu();

    public static void showDarkeningPlate() {
        pane.setVisible(true);
    }
}