package com.example.aloe;

import com.example.aloe.archive.ArchiveHandler;
import com.example.aloe.archive.ArchiveType;
import com.example.aloe.menu.MenuManager;
import com.example.aloe.settings.SettingsManager;
import com.example.aloe.settings.SettingsWindow;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import org.apache.tika.Tika;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    private List<File> directoryHistory = new ArrayList<>();
    private static ListView<String> filesList;
    public static List<FontAwesome> menuIcons;

    private HBox navigationPanel = new HBox();
    private VBox filesBox = new VBox();
    private SplitPane filesPanel = new SplitPane();
    public static ScrollPane filesMenu = new ScrollPane();
    private static ScrollPane filesPane = new ScrollPane();
    public static Scene scene;
    private Button parrentDir = getNavigateParentButton();

    private int directoryHistoryPosition = -1;
    private static boolean isGridView = true;
    private VBox root = new VBox();
    public static Stage stage;
    private static FlowPane grid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root.getStyleClass().add("root");
        this.stage = stage;
        scene = new Scene(root, 935, 500);

        navigationPanel.getStyleClass().add("navigation-panel");
        filesPanel.getStyleClass().add("navigation-panel");

        filesPane.getStyleClass().add("files-pane");
        filesPanel.getStyleClass().add("files-panel");
        filesMenu.getStyleClass().add("files-menu");

        getDirectoryOptions();
        filesPanel.setMinHeight(root.getHeight() - navigationPanel.getHeight());

        filesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                directoryMenu.hide();
            }
        });

        filesPanel.getItems().add(filesPane);

        if (SettingsManager.getSetting("menu", "use-menu")) {
            loadMenu();
            if (SettingsManager.getSetting("menu", "position").equals("right")) {
                filesPanel.getItems().addLast(filesMenu);
            } else {
                filesPanel.getItems().addFirst(filesMenu);
            }
            filesPanel.setDividerPositions((double)SettingsManager.getSetting("menu", "divider-position"));
        }

        navigationPanel.setPadding(new Insets(6));
        SplitPane.setResizableWithParent(filesMenu, false);

        Button changeDisplay = new Button("List");
        changeDisplay.setOnMouseClicked(event -> {
            isGridView = !isGridView;
            refreshCurrentDirectory();
            if(isGridView) {
                changeDisplay.setText("List");
            } else {
                changeDisplay.setText("Grid");
            }
        });

        filesPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            removeSelectionFromFiles();
        });

        CheckBox darkMode = new CheckBox(Translator.translate("navigate.dark-mode"));
        darkMode.setOnAction(event -> {
            if(darkMode.isSelected()) {
                darkMode.setText(Translator.translate("navigate.light-mode"));
                scene.getStylesheets().add(getClass().getResource("/assets/css/style_dark.css").toExternalForm());
            } else {
                darkMode.setText(Translator.translate("navigate.dark-mode"));
                scene.getStylesheets().remove(getClass().getResource("/assets/css/style_dark.css").toExternalForm());
            }
        });

        filesMenu.setMinWidth(10);
        filesMenu.setPrefWidth(160);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navigationPanel.getChildren().addAll(getNavigatePrevButton(), parrentDir, getNavigateNextButton(), getReloadButton(), spacer, getNavigateOptionsButton());
        root.getChildren().addAll(navigationPanel, filesPanel);
        filesList = new ListView<>();

        if (!SettingsManager.getSetting("files", "start-folder").equals("home")) {
            loadDirectoryContents(new File((String)SettingsManager.getSetting("files", "start-folder-location")), true);
        } else {
            loadDirectoryContents(new File(System.getProperty("user.home")), true);
        }

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight() - navigationPanel.getHeight());
        });

        scene.getStylesheets().add(getClass().getResource("/assets/css/style.css").toExternalForm());
        createMultiSelectionFilesContextMenu();

        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            SettingsManager.setSetting("menu", "divider-position", filesPanel.getDividerPositions());
            if (SettingsManager.getSetting("files", "start-folder").equals("last")) {
                SettingsManager.setSetting("files", "start-folder-location", FilesOperations.getCurrentDirectory().getAbsolutePath());
            }
        });
    }

    public static void loadMenu() {
        VBox menu = MenuManager.getMenu();
        menu.prefWidthProperty().bind(filesMenu.widthProperty());
        menu.prefHeightProperty().bind(filesMenu.heightProperty());
        filesMenu.setContent(menu);
    }

    private Button getNavigateNextButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-next")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getArrow(ArrowLoader.ArrowDirection.RIGHT));
        button.setPadding(new Insets(7, 13, 10, 10));
        button.getStyleClass().addAll("next-directory", "navigate-button");
        button.setOnMouseClicked(event -> {
            if (directoryHistoryPosition < directoryHistory.size() - 1) {
                directoryHistoryPosition++;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });
        return button;
    }

    private Button getReloadButton() {
        Button reload = new Button(Translator.translate("navigate.reload"));
        reload.getStyleClass().add("reload-button");
        reload.setOnMouseClicked(event -> {
            loadDirectoryContents(FilesOperations.getCurrentDirectory(), false);
        });
        reload.setPadding(new Insets(5, 10, 5, 10));
        HBox.setMargin(reload, new Insets(5, 15, 5, 15));
        return reload;
    }

    private Button getNavigateOptionsButton() {
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        popOver.setDetachable(true);
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        CheckBox showHiddenFiles = new CheckBox(Translator.translate("navigate.hidden-files"));
        VBox.setMargin(showHiddenFiles, new Insets(5, 10, 5, 10));
        showHiddenFiles.setSelected(SettingsManager.getSetting("files", "show-hidden"));
        showHiddenFiles.setOnAction(event -> {
            SettingsManager.setSetting("files", "show-hidden", showHiddenFiles.isSelected());
            refreshCurrentDirectory();
        });
        Button aboutButton = new Button(Translator.translate("navigate.about-button"));
        aboutButton.setOnMouseClicked(e -> openWindowButton());
        Button settingsButton = new Button(Translator.translate("navigate.settings"));
        settingsButton.setOnMouseClicked(e -> new SettingsWindow().show());
        container.getChildren().addAll(showHiddenFiles, aboutButton, settingsButton);
        popOver.setContentNode(container);
        Button button = new Button("options");
        HBox.setMargin(button, new Insets(5, 10, 5, 10));
        button.setOnMouseClicked(event -> {
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
        });
        return button;
    }

    private void openWindowButton() {
        Stage window = new Stage();
        window.setResizable(false);
        window.initStyle(StageStyle.UNIFIED);

        VBox container = new VBox();
        container.getChildren().addAll(getSegmentedButtons(container, getAboutContainer(), getCreatorContainer()), getAboutContainer());

        Scene scene = new Scene(container, 300  , 390);
        scene.getStylesheets().add(getClass().getResource("/assets/css/style_about.css").toExternalForm());
        window.setScene(scene);
        window.setTitle(Translator.translate("window.about.title"));
        window.show();
    }

    private SegmentedButton getSegmentedButtons(VBox container, VBox aboutContainer, VBox creatorContainer) {
        ToggleButton aboutButton = new ToggleButton(Translator.translate("window.about.about"));
        aboutButton.setSelected(true);
        aboutButton.setMinWidth(150);

        ToggleButton creatorButton = new ToggleButton(Translator.translate("window.about.creator"));
        creatorButton.setMinWidth(150);

        SegmentedButton segmentedButton = new SegmentedButton(aboutButton, creatorButton);

        aboutButton.setOnMouseClicked(event -> {
            container.getChildren().clear();
            container.getChildren().addAll(segmentedButton, aboutContainer);
        });
        creatorButton.setOnMouseClicked(event -> {
            container.getChildren().clear();
            container.getChildren().addAll(segmentedButton, creatorContainer);
        });

        return segmentedButton;
    }

    private VBox getAboutContainer() {
        VBox aboutContainer = new VBox();
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        icon.setFitHeight(120);
        icon.setFitWidth(120);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().add("about-name");
        name.setPadding(new Insets(25, 10, 5, 10));

        Label version = new Label("0.3.5");
        version.getStyleClass().add("about-version");

        Label description = new Label(Translator.translate("window.about.description"));
        description.getStyleClass().add("about-description");

        Hyperlink link = new Hyperlink(Translator.translate("window.about.website"));
        link.setOnAction(event -> {
            getHostServices().showDocument("https://github.com/MysiuPysiu76/Aloe");
        });

        Label warranty = new Label(Translator.translate("window.about.warranty"));
        warranty.setTextOverrun(OverrunStyle.CLIP);
        warranty.setMaxWidth(250);
        warranty.setAlignment(Pos.CENTER);
        warranty.getStyleClass().addAll("about-warranty", "text-center");
        warranty.setWrapText(true);

        aboutContainer.getChildren().addAll(icon, name, version, description, link, warranty);
        aboutContainer.setAlignment(Pos.TOP_CENTER);
        return aboutContainer;
    }

    private VBox getCreatorContainer() {
        VBox creatorContainer = new VBox();
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/icons/file.png")));
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().add("about-name");
        name.setPadding(new Insets(25, 10, 5, 10));

        Label inspiration = new Label(Translator.translate("window.about.inspiration"));
        inspiration.setPadding(new Insets(25, 10, 10, 10));
        inspiration.setTextOverrun(OverrunStyle.CLIP);
        inspiration.setMaxWidth(250);
        inspiration.setAlignment(Pos.CENTER);
        inspiration.getStyleClass().add("text-center");
        inspiration.setWrapText(true);

        Hyperlink linkCreator = new Hyperlink(Translator.translate("window.about.creator-website"));
        linkCreator.getStyleClass().add("text-center");
        linkCreator.setPadding(new Insets(5, 10, 10, 10));
        linkCreator.setOnAction(event -> {
            getHostServices().showDocument("https://github.com/Meiroth73");
        });

        Label usedIcons = new Label(Translator.translate("window.about.used-icons"));

        Hyperlink linkIcons = new Hyperlink("Flaticon");
        linkIcons.setOnAction(event -> {
            getHostServices().showDocument("https://www.flaticon.com/");
        });

        creatorContainer.getChildren().addAll(icon, name, inspiration, linkCreator, usedIcons, linkIcons);
        creatorContainer.setAlignment(Pos.TOP_CENTER);
        return creatorContainer;
    }

    private Button getNavigatePrevButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-prev")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getArrow(ArrowLoader.ArrowDirection.LEFT));
        button.setPadding(new Insets(7, 13, 10, 10));
        button.getStyleClass().addAll("prev-directory", "navigate-button");

        button.setOnMouseClicked(event -> {
            if (directoryHistoryPosition > 0) {
                directoryHistoryPosition--;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });
        return button;
    }

    private Button getNavigateParentButton() {
        Button button = new Button();
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-parent")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getArrow(ArrowLoader.ArrowDirection.TOP));
        button.setPadding(new Insets(9, 11, 9, 8));
        button.getStyleClass().addAll("parent-directory", "navigate-button");

        button.setOnMouseClicked(event -> {
            getParentDirectory();
        });
        return button;
    }

    private void checkParentDirectory() {
        if (FilesOperations.getCurrentDirectory().getPath().equals("/")) {
            parrentDir.setDisable(true);
        } else {
            parrentDir.setDisable(false);
        }
    }

    private static List<VBox> selectedFiles = new ArrayList<>();

    public void loadDirectoryContents(File directory, boolean addToHistory) {
        removeSelectionFromFiles();
        FilesOperations.setCurrentDirectory(directory);
        filesPane.setVvalue(0);
        checkParentDirectory();

        grid = new FlowPane();
        grid.setPadding(new Insets(5));
        grid.getStyleClass().add("files-grid");
        filesPane.setPadding(new Insets(5, 10, 10, 10));
        FlowPane.setMargin(grid, new Insets(5, 10, 10, 10));
        ListView<String> filesList = new ListView<>();
        filesList.getItems().clear();

        if (addToHistory) {
            if (directoryHistoryPosition != directoryHistory.size() - 1) {
                directoryHistory = new ArrayList<>(directoryHistory.subList(0, directoryHistoryPosition + 1));
            }
            directoryHistory.add(directory);
            directoryHistoryPosition++;
        }

        File[] files = FilesOperations.getCurrentDirectory().listFiles();
        if (files != null) {
            List<String> directories = new ArrayList<>();
            List<String> normalFiles = new ArrayList<>();

            for (File file : files) {
                if (!(boolean)SettingsManager.getSetting("files", "show-hidden")) {
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                }
                if (file.isDirectory()) {
                    directories.add(file.getName());
                } else {
                    normalFiles.add(file.getName());
                }
            }
            Collections.sort(directories);
            Collections.sort(normalFiles);

            if (Main.isGridView) {
                for (String dirName : directories) {
                    VBox box = createFileBox(dirName, true);
                    box.setOnMouseClicked(event -> {
                        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            loadDirectoryContents(new File(FilesOperations.getCurrentDirectory(), dirName), true);
                        } else {
                            selectFile(box, event);
                            event.consume();
                        }
                    });
                    grid.getChildren().add(box);
                }

                for (String fileName : normalFiles) {
                    VBox box = createFileBox(fileName, false);
                    box.setOnMouseClicked(event -> {
                        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            FilesOperations.openFileInBackground(new File(FilesOperations.getCurrentDirectory(), fileName));
                        } else {
                            selectFile(box, event);
                            event.consume();
                        }
                    });
                    grid.getChildren().add(box);
                }

                filesPane.setFitToWidth(true);
                filesPane.setContent(grid);
                filesPane.getStyleClass().add("files-pane");
                heightListener = new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        grid.setMinHeight(grid.getHeight() + 65);
                        grid.setMaxHeight(grid.getHeight() + 75);
                        grid.heightProperty().removeListener(heightListener);
                    }
                };
                grid.heightProperty().addListener(heightListener);
            } else {
                filesList.getItems().addAll(directories);
                filesList.getItems().addAll(normalFiles);
                filesBox.getChildren().add(filesList);
                filesPane.setContent(filesBox);

                filesList.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                        String selectedItem = filesList.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            File selectedFile = new File(FilesOperations.getCurrentDirectory(), selectedItem);
                            if (selectedFile.isDirectory()) {
                                loadDirectoryContents(selectedFile, true);
                            } else {
                                FilesOperations.openFileInBackground(selectedFile);
                            }
                        }
                    }
                });
            }
        }
    }

    private void selectFile(VBox fileBox, MouseEvent event) {
        if(!event.isControlDown() && event.getButton() == MouseButton.PRIMARY) {
            removeSelectionFromFiles();
        }

        selectedFiles.add(fileBox);
        fileBox.getStyleClass().add("selected-file");
    }

    private void removeSelectionFromFiles() {
        for (VBox file : selectedFiles) {
            file.getStyleClass().remove("selected-file");
        }
        selectedFiles.clear();
    }

    ChangeListener<Number> heightListener;

    private VBox createFileBox(String name, boolean isDirectory) {
        VBox fileBox = new VBox();

        fileBox.setMinWidth(100);
        fileBox.setPrefWidth(100);
        fileBox.setMaxWidth(100);
        fileBox.setMaxHeight(200);
        fileBox.setPadding(new Insets(125, 0, 0, 0));
        fileBox.setAlignment(Pos.TOP_CENTER);
        fileBox.setSpacing(5);
        fileBox.getStyleClass().add("file-box");

        ImageView icon = new ImageView();
        if (isDirectory) {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        } else {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/file.png")));
        }
        icon.setFitHeight(60);
        icon.setFitWidth(60);
        VBox.setMargin(icon, new Insets(5, 2, 5, 2));

        Label fileName = new Label(name);
        fileName.setWrapText(true);
        fileName.setTextOverrun(OverrunStyle.CLIP);
        fileName.setMaxWidth(90);
        fileName.setAlignment(Pos.CENTER);
        fileName.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");

        fileBox.getChildren().addAll(icon, fileName);
        fileBox.setPadding(new Insets(0, 5, 15, 5));
        fileBox.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px;");
        getFileOptions(fileBox, name);

        fileBox.setOnDragDetected(event -> {
            Dragboard db = fileBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            List<String> fileNamesToDrag = new ArrayList<>();
            if (selectedFiles.contains(fileBox)) {
                for (VBox selectedFile : selectedFiles) {
                    Label fileNameLabel = (Label) selectedFile.getChildren().get(1);
                    fileNamesToDrag.add(fileNameLabel.getText());
                }
            } else {
                fileNamesToDrag.add(name);
            }

            content.putString(String.join(",", fileNamesToDrag));
            db.setContent(content);
            event.consume();
        });

        fileBox.setOnDragOver(event -> {
            if (event.getGestureSource() != fileBox && event.getDragboard().hasString() && isDirectory) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        fileBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String[] draggedFileNames = db.getString().split(",");
                File targetDirectory = new File(FilesOperations.getCurrentDirectory(), name);
                if (targetDirectory.isDirectory()) {
                    for (String draggedFileName : draggedFileNames) {
                        File draggedFile = new File(FilesOperations.getCurrentDirectory(), draggedFileName);
                        if (draggedFile.exists()) {
                            draggedFile.renameTo(new File(targetDirectory, draggedFile.getName()));
                        }
                    }
                    refreshCurrentDirectory();
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return fileBox;
    }

    private void refreshCurrentDirectory() {
        loadDirectoryContents(FilesOperations.getCurrentDirectory(), false);
    }

    ContextMenu directoryMenu = new ContextMenu();

    private void getDirectoryOptions() {
        directoryMenu.getItems().clear();
        MenuItem newDirectory = new MenuItem(Translator.translate("context-menu.new-folder"));
        newDirectory.setOnAction(event -> {
            createDirectory();
            refreshCurrentDirectory();
        });
        MenuItem newFile = new MenuItem(Translator.translate("context-menu.new-file"));
        newFile.setOnAction(event -> {
            createFile();
            refreshCurrentDirectory();
        });
        MenuItem paste = new MenuItem(Translator.translate("context-menu.paste"));
        paste.setOnAction(event -> {
            FilesOperations.pasteFilesFromClipboard();
            refreshCurrentDirectory();
        });
        MenuItem selectAll = new MenuItem(Translator.translate("context-menu.select-all"));
        selectAll.setOnAction(event -> {
            selectAllFiles();
        });
        MenuItem properties = new MenuItem(Translator.translate("context-menu.properties"));
        properties.setOnAction(event -> {
            try {
                openPropertiesWindow(new VBox(), FilesOperations.getCurrentDirectory());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        directoryMenu.getItems().addAll(newDirectory, newFile, paste, selectAll, properties);
        filesPane.setOnContextMenuRequested(event -> {
            removeSelectionFromFiles();
            paste.setDisable(FilesOperations.isClipboardEmpty());
            directoryMenu.show(filesPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void selectAllFiles() {
        selectedFiles.clear();
        selectedFiles = grid.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .map(node -> (VBox) node)
                .collect(Collectors.toList());
        selectedFiles.forEach(fileBox -> fileBox.getStyleClass().add("selected-file"));
    }

    private boolean isSelected(VBox fileBox) {
        return selectedFiles.contains(fileBox);
    }

    private void getFileOptions(VBox item, String fileName) {
        ContextMenu fileMenu = new ContextMenu();
        File thisFile = new File(FilesOperations.getCurrentDirectory(), fileName);
        MenuItem open = new MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            openFileInOptions(new File(FilesOperations.getCurrentDirectory(), fileName));
        });
        MenuItem copy = new MenuItem(Translator.translate("context-menu.copy"));
        copy.setOnAction(event -> {
            FilesOperations.copyFile(thisFile);
        });
        MenuItem rename = new MenuItem(Translator.translate("context-menu.rename"));
        rename.setOnAction(event -> {
            renameFile(thisFile);
            refreshCurrentDirectory();
        });
        MenuItem duplicate = new MenuItem(Translator.translate("context-menu.duplicate"));
        duplicate.setOnAction(event -> {
            FilesOperations.duplicateFiles(new ArrayList<>(List.of(thisFile)));
            refreshCurrentDirectory();
        });
        MenuItem moveTo = new MenuItem(Translator.translate("context-menu.move-to"));
        moveTo.setOnAction(event -> {
            FilesOperations.moveFileTo(new ArrayList<>(List.of(thisFile)));
            refreshCurrentDirectory();
        });
        MenuItem moveToParent = new MenuItem(Translator.translate("context-menu.move-to-parent"));
        moveToParent.setOnAction(event -> {
            FilesOperations.moveFileToParent(thisFile);
            refreshCurrentDirectory();
        });
        MenuItem archive;
        if(thisFile.isFile() && (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".tar.gz") || fileName.endsWith(".rar"))) {
            archive = new MenuItem(Translator.translate("context-menu.extract"));
            archive.setOnAction(event -> {
                ArchiveHandler.extract(thisFile);
                refreshCurrentDirectory();
            });
        } else {
            archive = new MenuItem(Translator.translate("context-menu.compress"));
            archive.setOnAction(event -> {
                openCreateArchiveWindow(new ArrayList<>(List.of(thisFile)));
                refreshCurrentDirectory();
            });
        }
        MenuItem moveToTrash = new MenuItem(Translator.translate("context-menu.move-to-trash"));
        moveToTrash.setOnAction(event -> {
            FilesOperations.moveFileToTrash(thisFile);
           refreshCurrentDirectory();
        });
        MenuItem delete = new MenuItem(Translator.translate("context-menu.delete"));
        delete.setOnAction(event -> {
            FilesOperations.deleteFile(thisFile);
            refreshCurrentDirectory();
        });
        MenuItem properties = new MenuItem(Translator.translate("context-menu.properties"));
        properties.setOnAction(event -> {
            try {
                openPropertiesWindow(item, thisFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fileMenu.getItems().addAll(open, copy, rename, duplicate, moveTo, moveToParent, archive,moveToTrash, delete, properties);
        if (thisFile.isDirectory()) {
            MenuItem addToMenu = new MenuItem(Translator.translate("context-menu.add-to-menu"));
            addToMenu.setOnAction(event -> {
                MenuManager.addItemToMenu(thisFile.getPath(), thisFile.getName(), "FOLDER_OPEN_O");
            });
            fileMenu.getItems().add(9, addToMenu);
        }
        item.setOnContextMenuRequested(event -> {
            if(isSelected(item) && selectedFiles.size() == 1) {
                fileMenu.show(item, event.getScreenX(), event.getScreenY());
            } else if (isSelected(item)) {
                multiSelectionFilesContextMenu.show(item, event.getScreenX(), event.getScreenY());
            } else {
                fileMenu.show(item, event.getScreenX(), event.getScreenY());
                directoryMenu.hide();
                removeSelectionFromFiles();
            }
            event.consume();
        });
    }

    private void openCreateArchiveWindow(List<File> files) {
        Stage window = new Stage();
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setMinWidth(450);
        window.setMinHeight(206);
        window.setMinWidth(460);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label(Translator.translate("window.archive.title"));
        title.setPadding(new Insets(15, 10, 10, 10));
        title.setStyle("-fx-font-size: 20px");

        Label name = new Label(Translator.translate("window.archive.file-name"));
        name.setPadding(new Insets(1, 345, 7, 0));
        name.setStyle("-fx-font-size: 14px");

        TextField fileName = new TextField();
        fileName.setStyle("-fx-font-size: 15px");
        fileName.setMinWidth(330);
        fileName.setPadding(new Insets(7, 10, 7, 10));

        ComboBox<ArchiveType> archiveType = new ComboBox<>();
        archiveType.setValue(ArchiveType.ZIP);
        archiveType.setItems(FXCollections.observableArrayList(ArchiveType.values()));
        Label error = new Label();
        error.setMinWidth(210);
        error.setStyle("-fx-font-size: 14px; -fx-text-alignment: start");
        error.setStyle("-fx-text-fill: red");
        error.setPadding(new Insets(-2, 0, 0, 0));

        CheckBox compress = new CheckBox(Translator.translate("window.archive.compress"));
        CheckBox password = new CheckBox(Translator.translate("window.archive.password"));
        TextField passwordText = new TextField();
        passwordText.setPadding(new Insets(5, 7, 5 , 7));
        passwordText.setMaxWidth(250);

        compress.setSelected(true);
        Button cancel = new Button(Translator.translate("button.cancel"));
        cancel.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");
        Button create = new Button(Translator.translate("button.create"));
        create.setStyle("-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-padding: 7px 15px;");

        HBox nameHBox = new HBox(fileName, archiveType);
        nameHBox.setSpacing(10);
        nameHBox.setAlignment(Pos.CENTER);
        HBox optionsHBox = new HBox(password, compress);
        optionsHBox.setPadding(new Insets(10));
        optionsHBox.setSpacing(10);
        optionsHBox.setAlignment(Pos.CENTER);
        HBox bottomHBox = new HBox(error, cancel, create);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(12, 15, 5, 10));
        root.getChildren().addAll(title, name, nameHBox, optionsHBox, bottomHBox);

        fileName.textProperty().addListener((observable, oldValue, newValue) -> {
            String validationError = validateFileName(fileName.getText() + archiveType.getValue().toString());
            if(validationError != null) {
                error.setText(validationError);
                create.setDisable(true);
            } else {
                error.setText("");
                create.setDisable(false);
            }
        });
        archiveType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == ArchiveType.ZIP) {
                if (!root.getChildren().contains(optionsHBox)) {
                    root.getChildren().add(3, optionsHBox);
                    window.setMaxHeight(206);
                    window.setMinHeight(206);
                }
            } else {
                if (password.isSelected()) {
                    root.getChildren().remove(passwordText);
                    password.setSelected(false);
                }
                root.getChildren().remove(optionsHBox);
                window.setMaxHeight(166);
                window.setMinHeight(166);
            }
            String validationError = validateFileName(fileName.getText() +  archiveType.getValue().toString());
            if(validationError != null) {
                error.setText(validationError);
                create.setDisable(true);
            } else {
                error.setText("");
                create.setDisable(false);
            }
        });
        password.setOnAction(event -> {
            if (password.isSelected()) {
                root.getChildren().add(4, passwordText);
                window.setMaxHeight(230);
                window.setMinHeight(230);
            } else {
                root.getChildren().remove(4);
                window.setMaxHeight(206);
                window.setMinHeight(206);
            }
        });
        cancel.setOnAction(event -> {
            window.close();
        });
        create.setOnAction(event -> {
            window.close();
            ArchiveHandler.compress(files, fileName.getText(), compress.isSelected(), password.isSelected(), passwordText.getText(), archiveType.getValue());
            refreshCurrentDirectory();
        });

        Scene scene = new Scene(root, 350, 140);
        window.setScene(scene);
        window.initOwner(stage);
        window.show();
    }

    private ArrayList<String> getFilePropertiesNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.file-name"));
        names.add(Translator.translate("window.properties.file-path"));
        names.add(Translator.translate("window.properties.file-type"));
        names.add(Translator.translate("window.properties.file-size"));
        names.add(Translator.translate("window.properties.file-parent"));
        names.add(Translator.translate("window.properties.file-created"));
        names.add(Translator.translate("window.properties.file-modified"));
        names.add(Translator.translate("window.properties.free-space"));
        return names;
    }

    private ArrayList<String> getFilePropertiesValues(File file) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        values.add(file.getName());
        values.add(file.getPath());
        if(file.isDirectory()) {
            long directorySize = calculateDirectorySize(file);
            values.add(convertBytesByUnit(directorySize) + " (" + directorySize + Translator.translate("units.bytes") + ")");
        } else {
            values.add(convertBytesByUnit(file.length()) + " (" + file.length() + Translator.translate("units.bytes") + ")");
        }
        values.add(file.getParent());
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime creationTime = attrs.creationTime();
        String creationTimeString = creationTime.toString();
        values.add(OffsetDateTime.parse(creationTimeString).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        LocalDateTime modifiedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
        values.add(modifiedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        values.add(convertBytesByUnit(file.getFreeSpace()));
        return values;
    }

    private long calculateDirectorySize(File file) {
        long size = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for(File f : files) {
                if(f.isFile()) {
                    size += f.length();
                } else {
                    size += calculateDirectorySize(f);
                }
            }
        }
        return size;
    }

    private String convertBytesByUnit(long size) {
        if (SettingsManager.getSetting("files", "use-binary-units")) {
            return convertBytesToGiB(size);
        } else {
            return convertBytesToGB(size);
        }
    }

    private String convertBytesToGiB(long size) {
        if(size < 1024) {
            return "";
        } else if(size < 1024 * 1024) {
            return String.format("%.1f KiB ", size / 1024.0);
        } else if(size < 1024 * 1024 * 1024) {
            return String.format("%.1f MiB ", size / (1024.0 * 1024.0));
        } else if(size < 1024L * 1024 * 1024 * 1024) {
            return String.format("%.1f GiB ", size / (1024.0 * 1024.0 * 1024.0));
        } else if(size < 1024L * 1024 * 1024 * 1024 * 1024) {
            return String.format("%.1f TiB ", size / (1024 * 1024.0 * 1024.0 * 1024));
        } else {
            return String.format("%.1f PiB ", size / (1024 * 1024.0 * 1024.0 * 1024 * 1024));
        }
    }

    private String convertBytesToGB(long size) {
        if(size < 1000) {
            return "";
        } else if(size < 1000 * 1000) {
            return String.format("%.1f KB ", size / 1000.0);
        } else if(size < 1000 * 1000 * 1000) {
            return String.format("%.1f MB ", size / (1000.0 * 1000.0));
        } else if(size < 1000L * 1000 * 1000 * 1000) {
            return String.format("%.1f GB ", size / (1000.0 * 1000.0 * 1000.0));
        } else if(size < 1000L * 1000 * 1000 * 1000 * 1000) {
            return String.format("%.1f TB ", size / (1000 * 1000.0 * 1000.0 * 1000));
        } else {
            return String.format("%.1f PB ", size / (1000 * 1000.0 * 1000.0 * 1000 * 1000));
        }
    }

    public void openPropertiesWindow(VBox box, File file) throws IOException {
        Stage window = new Stage();
        VBox root = new VBox();
        window.setMinHeight(380);
        window.setMinWidth(330);
        window.initModality(Modality.WINDOW_MODAL);
        window.initStyle(StageStyle.UNIFIED);

        ImageView icon = new ImageView();
        icon.setFitHeight(75);
        icon.setFitWidth(75);
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(30, 10, 10, 2));

        List<String> names = getFilePropertiesNames();
        List<String> values = getFilePropertiesValues(file);

        if (file.isFile()) {
            window.setTitle(Translator.translate("window.properties.file-properties"));
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/file.png")));
            values.add(2, new Tika().detect(file));
        } else {
            window.setTitle(Translator.translate("window.properties.folder-properties"));
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
            values.add(2, Translator.translate("window.properties.folder"));
            names.add(5, Translator.translate("window.properties.folder-contents"));
            values.add(5,  Files.list(Path.of(file.getPath())).count() + Translator.translate("window.properties.items"));
        }
        GridPane fileData = new GridPane();
        VBox.setMargin(fileData, new Insets(20, 0, 0, 0));

        for (int i = 0; i < names.size(); i++) {
            Label name = new Label(names.get(i));
            name.setAlignment(Pos.CENTER_RIGHT);
            name.setPadding(new Insets(4, 10, 4, 0));
            name.getStyleClass().add("name");
            name.setMinWidth(110);
            name.setMaxWidth(110);
            Label value = new Label(values.get(i));
            fileData.add(name, 0, i);
            fileData.add(value, 1, i);
        }
        root.getChildren().addAll(iconWrapper, fileData);
        Scene scene = new Scene(root, 330, 390);
        window.setScene(scene);
        window.initOwner(stage);
        window.showAndWait();
    }

    private ContextMenu multiSelectionFilesContextMenu;

    public void createMultiSelectionFilesContextMenu() {
        multiSelectionFilesContextMenu = new ContextMenu();
        MenuItem copy = new MenuItem(Translator.translate("context-menu.copy"));
        copy.setOnAction(event -> {
            copySelectedFiles();
        });
        MenuItem duplicate = new MenuItem(Translator.translate("context-menu.duplicate"));
        duplicate.setOnAction(event -> {
            FilesOperations.duplicateFiles(getSelectedFiles());
            refreshCurrentDirectory();
        });
        MenuItem moveTo = new MenuItem(Translator.translate("context-menu.move-to"));
        moveTo.setOnAction(event -> {
           FilesOperations.moveFileTo(getSelectedFiles());
           refreshCurrentDirectory();
        });
        MenuItem moveToParent = new MenuItem(Translator.translate("context-menu.move-to-parent"));
        moveToParent.setOnAction(event -> {
            FilesOperations.moveFileToParent(getSelectedFiles());
            refreshCurrentDirectory();
        });
        MenuItem moveToTrash = new MenuItem(Translator.translate("context-menu.move-to-trash"));
        moveToTrash.setOnAction(event -> {
            FilesOperations.moveFileToTrash(getSelectedFiles());
            refreshCurrentDirectory();
        });
        MenuItem compress = new MenuItem(Translator.translate("context-menu.compress"));
        compress.setOnAction(event -> {
            openCreateArchiveWindow(getSelectedFiles());
           refreshCurrentDirectory();
        });
        MenuItem delete = new MenuItem(Translator.translate("context-menu.delete"));
        delete.setOnAction(event -> {
            deleteSelectedFiles();
            refreshCurrentDirectory();
        });
        multiSelectionFilesContextMenu.getItems().addAll(copy, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }

    public void deleteSelectedFiles() {
        for (VBox fileBox : selectedFiles) {
            Label fileName = (Label) fileBox.getChildren().get(1);
            FilesOperations.deleteFile(new File(FilesOperations.getCurrentDirectory(), fileName.getText()));
        }
    }

    private void copySelectedFiles() {
        if (selectedFiles.isEmpty()) {
            return;
        }

        List<File> filesToCopy = new ArrayList<>();
        for (VBox fileBox : selectedFiles) {
            Label fileNameLabel = (Label) fileBox.getChildren().get(1);
            String fileName = fileNameLabel.getText();

            File file = new File(FilesOperations.getCurrentDirectory(), fileName);
            filesToCopy.add(file);
        }
        FilesOperations.copyFilesToClipboard(filesToCopy);
    }

    private void openFileInOptions(File file) {
        if (file.isDirectory()) {
            loadDirectoryContents(file, true);
        } else {
            FilesOperations.openFileInBackground(file);
        }
    }

    private void renameFile(File file) {
        Dialog<String> dialog = new Dialog<>();
        if(file.isDirectory()) {
            dialog.setTitle("Rename Directory");
        } else {
            dialog.setTitle("Rename File");
        }

        VBox dialogContent = new VBox();
        dialogContent.setPadding(new Insets(5));

        TextField name = new TextField(file.getName());
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");

        dialogContent.getChildren().addAll(name, error);
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType renameButtonType = new ButtonType("Rename", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(renameButtonType, ButtonType.CANCEL);

        Button renameButton = (Button) dialog.getDialogPane().lookupButton(renameButtonType);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            String validationError = validateFileName(newValue);
            if (validationError != null) {
                error.setText(validationError);
                renameButton.setDisable(true);
            } else {
                error.setText("");
                renameButton.setDisable(false);
            }
        });

        renameButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newName = name.getText().trim();
            File newFile = new File(FilesOperations.getCurrentDirectory(), newName);

            if(file.renameTo(newFile)) {
                refreshCurrentDirectory();
            } else {
                error.setText("Could not rename: " + newName);
                event.consume();
            }
        });
        dialog.showAndWait();
    }

    private String validateFileName(String name) {
        if(name.isEmpty()) {
            return Translator.translate("validator.empty-name");
        }
        if(new File(FilesOperations.getCurrentDirectory(), name).exists()) {
            return Translator.translate("validator.used-name");
        }
        if(name.contains("/")) {
            return Translator.translate("validator.contains-slash");
        }
        return null;
    }

    private void createDirectory() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create Folder");

        VBox dialogContent = new VBox();
        dialogContent.setPadding(new Insets(5));
        TextField name = new TextField("New Folder");
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");
        dialogContent.getChildren().addAll(name, error);
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType createDirectoryButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createDirectoryButtonType, ButtonType.CANCEL);
        Button newDirectoryButton = (Button) dialog.getDialogPane().lookupButton(createDirectoryButtonType);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            String validationError = validateFileName(newValue);
            if (validationError != null) {
                error.setText(validationError);
                newDirectoryButton.setDisable(true);
            } else {
                error.setText("");
                newDirectoryButton.setDisable(false);
            }
        });

        newDirectoryButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newName = name.getText().trim();
            File newFile = new File(FilesOperations.getCurrentDirectory(), newName);
            if (!newFile.exists()) {
                if(!newFile.mkdir()) {
                }
            }
        });
        dialog.showAndWait();
    }

    private void createFile() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create File");

        VBox dialogContent = new VBox();
        dialogContent.setPadding(new Insets(5));
        TextField name = new TextField("New File.txt");
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");
        dialogContent.getChildren().addAll(name, error);
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType createFileButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createFileButtonType, ButtonType.CANCEL);
        Button newFileButton = (Button) dialog.getDialogPane().lookupButton(createFileButtonType);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            String validationError = validateFileName(newValue);
            if (validationError != null) {
                error.setText(validationError);
                newFileButton.setDisable(true);
            } else {
                error.setText("");
                newFileButton.setDisable(false);
            }
        });

        newFileButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newName = name.getText().trim();
            File newFile = new File(FilesOperations.getCurrentDirectory(), newName);
            if (!newFile.exists()) {
                try {
                    if(!newFile.createNewFile()) {

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        dialog.showAndWait();
    }

    private void getParentDirectory() {
        if(FilesOperations.getCurrentDirectory().getPath() != "/") {
            loadDirectoryContents(new File(FilesOperations.getCurrentDirectory().getParent()), true);
        }
    }

    public static List<File> getSelectedFiles() {
        List<File> files = new ArrayList<>();
        for (VBox selectedFile : selectedFiles) {
            files.add(new File(FilesOperations.getCurrentDirectory().getPath(), ((Label) selectedFile.getChildren().get(1)).getText()));
        }
        return files;
    }
}