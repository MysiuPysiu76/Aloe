package com.example.aloe;

import com.example.aloe.menu.MenuManager;
import com.example.aloe.settings.SettingsManager;
import com.example.aloe.settings.SettingsWindow;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
    private VBox mainContainer = new VBox();
    private StackPane root = new StackPane();
    public static Pane pane = new Pane();
    public static Stage stage;
    private static FlowPane grid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root.getChildren().addAll(mainContainer, pane);
        mainContainer.getStyleClass().add("root");
        Main.stage = stage;
        scene = new Scene(root, 975, 550);

        navigationPanel.getStyleClass().add("navigation-panel");
        filesPanel.getStyleClass().add("navigation-panel");
        filesPane.getStyleClass().add("files-pane");
        filesPanel.getStyleClass().add("files-panel");
        filesMenu.getStyleClass().add("files-menu");

        VBox.setVgrow(filesPanel, Priority.ALWAYS);

        filesPanel.setMinHeight(mainContainer.getHeight() - navigationPanel.getHeight());
        filesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                directoryMenu.hide();
            }
        });

        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        pane.setVisible(false);

        filesPanel.getItems().add(filesPane);

        if (Boolean.TRUE.equals(SettingsManager.getSetting("menu", "use-menu"))) {
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

        filesMenu.setMinWidth(10);
        filesMenu.setPrefWidth(160);
        navigationPanel.getChildren().addAll(getNavigatePrevButton(), parrentDir, getNavigateNextButton(), getReloadButton(), WindowComponents.getSpacer(), getNavigateOptionsButton());
        mainContainer.getChildren().addAll(navigationPanel, filesPanel);

        if (!Objects.equals(SettingsManager.getSetting("files", "start-folder"), "home")) {
            loadDirectoryContents(new File((String) Objects.requireNonNull(SettingsManager.getSetting("files", "start-folder-location"))), true);
        } else {
            loadDirectoryContents(new File(System.getProperty("user.home")), true);
        }

        mainContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
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

        filesPane.setOnContextMenuRequested(event -> {
            removeSelectionFromFiles();
            directoryMenu.getItems().get(2).setDisable(FilesOperations.isClipboardEmpty());
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
        showHiddenFiles.setSelected(Boolean.TRUE.equals(SettingsManager.getSetting("files", "show-hidden")));
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
            boolean displayDirectoriesBeforeFiles = Boolean.TRUE.equals(SettingsManager.getSetting("files", "display-directories-before-files"));
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
                filesPane.setContent(filesListView);

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

    private void selectFile(VBox fileBox, @NotNull MouseEvent event) {
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
                    if (Boolean.TRUE.equals(SettingsManager.getSetting("files", "display-thumbnails"))) {
                        icon.setImage(new Image(new File(FilesOperations.getCurrentDirectory(), name).toURI().toString()));
                    } else {
                        icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/image.png")));
                    }
                }
                case "mp4" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/video.png")));
                case "mp3", "ogg" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/music.png")));
                case "epub", "mobi" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/book.png")));
                case "pdf" -> icon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/pdf.png")));
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

        FileBoxContextMenu fileBoxContextMenu = new FileBoxContextMenu(new File(FilesOperations.getCurrentDirectory(), name));
        fileBox.setOnContextMenuRequested(e -> {
            fileBoxContextMenu.show(fileBox, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        fileBox.setOnContextMenuRequested(event -> {
            if (isSelected(fileBox) && selectedFiles.size() == 1) {
                fileBoxContextMenu.show(fileBox, event.getScreenX(), event.getScreenY());
            } else if (isSelected(fileBox)) {
                multiFileBoxContextMenu.show(fileBox, event.getScreenX(), event.getScreenY());
            } else {
                fileBoxContextMenu.show(fileBox, event.getScreenX(), event.getScreenY());
                directoryMenu.hide();
                removeSelectionFromFiles();
            }
            event.consume();
        });

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

    MultiFileBoxContextMenu multiFileBoxContextMenu = new MultiFileBoxContextMenu();

    public void refreshCurrentDirectory() {
        loadDirectoryContents(FilesOperations.getCurrentDirectory(), false);
    }

    DirectoryContextMenu directoryMenu = new DirectoryContextMenu();

    public void selectAllFiles() {
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

    public static void openCreateArchiveWindow(List<File> files) {
        pane.getChildren().add(new CompressWindow(files));
        showDarkeningPlate();
        ((HBox) ((VBox) pane.getChildren().getFirst()).getChildren().get(2)).getChildren().getFirst().requestFocus();
    }

    public void deleteSelectedFiles() {
        for (VBox fileBox : selectedFiles) {
            Label fileName = (Label) fileBox.getChildren().get(1);
            FilesOperations.deleteFile(new File(FilesOperations.getCurrentDirectory(), fileName.getText()));
        }
        refreshCurrentDirectory();
    }

    public void copySelectedFiles() {
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

    public void cutSelectedFiles() {
        copySelectedFiles();
        FilesOperations.setIsCut(true);
    }

    public static  void openFileInOptions(File file) {
        if (file.isDirectory()) {
            new Main().loadDirectoryContents(file, true);
        } else {
            FilesOperations.openFileInBackground(file);
        }
    }

    public static void renameFile(File file) {
        pane.getChildren().add(new RenameWindow(file));
        showDarkeningPlate();
    }

    public static String validateFileName(String name) {
        if (name.isEmpty()) {
            return Translator.translate("validator.empty-name");
        }
        if (name.isBlank()) {
            return Translator.translate("validator.blank-name");
        }
        if (name.contains("/")) {
            return Translator.translate("validator.contains-slash");
        }
        if (new File(FilesOperations.getCurrentDirectory(), name).exists()) {
            return Translator.translate("validator.used-name");
        }
        return null;
    }

    public static void validateFileName(Label error, Button button, String text) {
        if (text == null || text.isEmpty()) {
            error.setText("");
            button.setDisable(false);
        } else {
            error.setText(text);
            button.setDisable(true);
        }
    }

    public static void createDirectory() {
        pane.getChildren().add(new DirectoryWindow());
        showDarkeningPlate();
    }

    private static void showDarkeningPlate() {
        pane.setVisible(true);
        ((VBox) pane.getChildren().getFirst()).getChildren().get(2).requestFocus();
    }

    public static void createFile() {
        pane.getChildren().add(new FileWindow());
        showDarkeningPlate();
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