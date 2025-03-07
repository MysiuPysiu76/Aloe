package com.example.aloe;

import com.example.aloe.archive.ArchiveHandler;
import com.example.aloe.archive.ArchiveParameters;
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
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.aloe.archive.ArchiveType.*;

public class Main extends Application {

    private List<File> directoryHistory = new ArrayList<>();
    private HBox navigationPanel = new HBox();
    private VBox filesBox = new VBox();
    private SplitPane filesPanel = new SplitPane();
    public static ScrollPane filesMenu = new ScrollPane();
    private static ScrollPane filesPane = new ScrollPane();
    public static Scene scene;
    private Button parrentDir = getNavigateParentButton();

    private int directoryHistoryPosition = -1;
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
            filesPanel.setDividerPositions((double) SettingsManager.getSetting("menu", "divider-position"));
        }

        navigationPanel.setPadding(new Insets(6));
        SplitPane.setResizableWithParent(filesMenu, false);

        filesPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            removeSelectionFromFiles();
        });

        CheckBox darkMode = new CheckBox(Translator.translate("navigate.dark-mode"));
        darkMode.setOnAction(event -> {
            if (darkMode.isSelected()) {
                darkMode.setText(Translator.translate("navigate.light-mode"));
                scene.getStylesheets().add(getClass().getResource("/assets/styles/style_dark.css").toExternalForm());
            } else {
                darkMode.setText(Translator.translate("navigate.dark-mode"));
                scene.getStylesheets().remove(getClass().getResource("/assets/styles/style_dark.css").toExternalForm());
            }
        });

        filesMenu.setMinWidth(10);
        filesMenu.setPrefWidth(160);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        navigationPanel.getChildren().addAll(getNavigatePrevButton(), parrentDir, getNavigateNextButton(), getReloadButton(), spacer, getNavigateOptionsButton());
        root.getChildren().addAll(navigationPanel, filesPanel);

        if (!Objects.equals(SettingsManager.getSetting("files", "start-folder"), "home")) {
            loadDirectoryContents(new File((String) Objects.requireNonNull(SettingsManager.getSetting("files", "start-folder-location"))), true);
        } else {
            loadDirectoryContents(new File(System.getProperty("user.home")), true);
        }

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight() - navigationPanel.getHeight());
        });

        scene.getStylesheets().add(getClass().getResource("/assets/styles/style.css").toExternalForm());

        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            SettingsManager.setSetting("menu", "divider-position", filesPanel.getDividerPositions());
            if (Objects.equals(SettingsManager.getSetting("files", "start-folder"), "last")) {
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
        aboutButton.setOnMouseClicked(e -> new AboutWindow(getHostServices()));
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
        parrentDir.setDisable(FilesOperations.getCurrentDirectory().getPath().equals("/"));
    }

    private static List<VBox> selectedFiles = new ArrayList<>();

    public void loadDirectoryContents(File directory, boolean addToHistory) {
        removeSelectionFromFiles();
        FilesOperations.setCurrentDirectory(directory);
        filesPane.setVvalue(0);
        checkParentDirectory();
        createMultiSelectionFilesContextMenu();


        grid = new FlowPane();
        grid.setPadding(new Insets(5));
        grid.getStyleClass().add("files-grid");
        filesPane.setPadding(new Insets(5, 10, 10, 10));
        FlowPane.setMargin(grid, new Insets(5, 10, 10, 10));
        ListView<String> filesListView = new ListView<>();
        filesListView.getItems().clear();

        if (addToHistory) {
            if (directoryHistoryPosition != directoryHistory.size() - 1) {
                directoryHistory = new ArrayList<>(directoryHistory.subList(0, directoryHistoryPosition + 1));
            }
            directoryHistory.add(directory);
            directoryHistoryPosition++;
        }

        File[] files = FilesOperations.getCurrentDirectory().listFiles();
        if (files != null) {
            List<String> filesList = new ArrayList<>();
            List<String> directories = new ArrayList<>();
            List<String> normalFiles = new ArrayList<>();
            boolean displayDirectoriesBeforeFiles = SettingsManager.getSetting("files", "display-directories-before-files");
            for (File file : files) {
                if (!(boolean) SettingsManager.getSetting("files", "show-hidden")) {
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                }
                if (displayDirectoriesBeforeFiles) {
                    if (file.isDirectory()) {
                        directories.add(file.getName());
                    } else {
                        normalFiles.add(file.getName());
                    }
                } else {
                    filesList.add(file.getName());
                }
            }
            Collections.sort(directories);
            Collections.sort(normalFiles);
            Collections.sort(filesList);

            if ((Objects.requireNonNull(SettingsManager.getSetting("files", "view"))).equals("grid")) {
                if (displayDirectoriesBeforeFiles) {
                    for (String dirName : directories) {
                        VBox box = createFileBox(dirName, true);
                        box.setOnMouseClicked(event -> {
                            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
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
                            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                FilesOperations.openFileInBackground(new File(FilesOperations.getCurrentDirectory(), fileName));
                            } else {
                                selectFile(box, event);
                                event.consume();
                            }
                        });
                        grid.getChildren().add(box);
                    }
                } else {
                    File currentDirectory = FilesOperations.getCurrentDirectory();
                    for (String fileName : filesList) {
                        VBox box;
                        if (new File(currentDirectory, fileName).isDirectory()) {
                            box = createFileBox(fileName, true);
                            box.setOnMouseClicked(event -> {
                                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                    loadDirectoryContents(new File(FilesOperations.getCurrentDirectory(), fileName), true);
                                } else {
                                    selectFile(box, event);
                                    event.consume();
                                }
                            });
                        } else {
                            box = createFileBox(fileName, false);
                            box.setOnMouseClicked(event -> {
                                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                    FilesOperations.openFileInBackground(new File(FilesOperations.getCurrentDirectory(), fileName));
                                } else {
                                    selectFile(box, event);
                                    event.consume();
                                }
                            });
                        }
                        grid.getChildren().add(box);
                    }
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
                filesListView.getItems().addAll(directories);
                filesListView.getItems().addAll(normalFiles);
                filesBox.getChildren().add(filesListView);
                filesPane.setContent(filesBox);

                filesListView.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        String selectedItem = filesListView.getSelectionModel().getSelectedItem();
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
        if (!event.isControlDown() && event.getButton() == MouseButton.PRIMARY) {
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
        double scale = SettingsManager.getSetting("files", "file-box-size");
        fileBox.setMinWidth(100 * scale);
        fileBox.setPrefWidth(100 * scale);
        fileBox.setMaxWidth(100 * scale);
        fileBox.setMinHeight(120 * scale);
        fileBox.setMaxHeight(120 * scale);
        fileBox.setPadding(new Insets(125, 0, 0, 0));
        fileBox.setAlignment(Pos.TOP_CENTER);
        fileBox.setSpacing(5 * scale);
        fileBox.getStyleClass().add("file-box");

        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        if (isDirectory) {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        } else {
            switch (FilesOperations.getExtension(name).toLowerCase()) {
                case "jpg", "jpeg", "png", "gif" -> {
                    if (SettingsManager.getSetting("files", "display-thumbnails")) {
                        icon.setImage(new Image(new File(FilesOperations.getCurrentDirectory(), name).toURI().toString()));
                    } else {
                        icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/image.png")));
                    }
                }
                case "mp4" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/video.png")));
                case "mp3", "ogg" ->
                        icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/music.png")));
                case "iso" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/cd.png")));
                default -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/file.png")));
            }
        }
        icon.setFitHeight(60 * scale);
        icon.setFitWidth(60 * scale);
        VBox.setMargin(icon, new Insets(5, 2, 5, 2));

        Label fileName = new Label(name);
        fileName.setWrapText(true);
        fileName.setMaxWidth(90 * scale);
        fileName.setAlignment(Pos.TOP_CENTER);
        fileName.setTooltip(new Tooltip(name));
        fileName.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");

        VBox box = new VBox(icon);
        box.setAlignment(Pos.BOTTOM_CENTER);
        box.setMinHeight(70 * scale);

        fileBox.getChildren().addAll(box, fileName);
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
            new PropertiesWindow(FilesOperations.getCurrentDirectory());
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
        if (thisFile.isFile() && (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".tar.gz") || fileName.endsWith(".rar") || fileName.endsWith(".7z") || fileName.endsWith(".jar"))) {
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
            new PropertiesWindow(thisFile);
        });
        fileMenu.getItems().addAll(open, copy, rename, duplicate, moveTo, moveToParent, archive, moveToTrash, delete, properties);
        if (thisFile.isDirectory()) {
            MenuItem addToMenu = new MenuItem(Translator.translate("context-menu.add-to-menu"));
            addToMenu.setOnAction(event -> {
                MenuManager.addItemToMenu(thisFile.getPath(), thisFile.getName(), "FOLDER_OPEN_O");
            });
            fileMenu.getItems().add(9, addToMenu);
        }
        item.setOnContextMenuRequested(event -> {
            if (isSelected(item) && selectedFiles.size() == 1) {
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
        archiveType.setValue(ZIP);
        List<ArchiveType> filteredList = Arrays.stream(ArchiveType.values()).filter(type -> type != ArchiveType.RAR).toList();
        archiveType.setItems(FXCollections.observableArrayList(filteredList));
        Label error = new Label();
        error.setMinWidth(210);
        error.setStyle("-fx-font-size: 14px; -fx-text-alignment: start");
        error.setStyle("-fx-text-fill: red");
        error.setPadding(new Insets(-2, 0, 0, 0));

        CheckBox compress = new CheckBox(Translator.translate("window.archive.compress"));
        CheckBox password = new CheckBox(Translator.translate("window.archive.password"));
        TextField passwordText = new TextField();
        passwordText.setPadding(new Insets(5, 7, 5, 7));
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
            if (validationError != null) {
                error.setText(validationError);
                create.setDisable(true);
            } else {
                error.setText("");
                create.setDisable(false);
            }
        });
        archiveType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == ZIP) {
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
            String validationError = validateFileName(fileName.getText() + archiveType.getValue().toString());
            if (validationError != null) {
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
            if (password.isSelected()) {
                ArchiveHandler.compress(new ArchiveParameters(files, archiveType.getValue(), fileName.getText() + archiveType.getValue().getExtension(), compress.isSelected(), passwordText.getText()));
            } else {
                ArchiveHandler.compress(new ArchiveParameters(files, archiveType.getValue(), fileName.getText() + archiveType.getValue().getExtension(), compress.isSelected()));
            }
            refreshCurrentDirectory();
        });

        Scene scene = new Scene(root, 350, 140);
        window.setScene(scene);
        window.initOwner(stage);
        window.show();
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
        if (file.isDirectory()) {
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

            if (file.renameTo(newFile)) {
                refreshCurrentDirectory();
            } else {
                error.setText("Could not rename: " + newName);
                event.consume();
            }
        });
        dialog.showAndWait();
    }

    private String validateFileName(String name) {
        if (name.isEmpty()) {
            return Translator.translate("validator.empty-name");
        }
        if (new File(FilesOperations.getCurrentDirectory(), name).exists()) {
            return Translator.translate("validator.used-name");
        }
        if (name.contains("/")) {
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
                newFile.mkdir();
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
                    newFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        dialog.showAndWait();
    }

    private void getParentDirectory() {
        if (!FilesOperations.getCurrentDirectory().getPath().equals("/")) {
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