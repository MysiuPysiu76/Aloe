package com.example.aloe;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends Application {

    private List<File> directoryHistory = new ArrayList<>();
    private ListView<String> filesList;

    private HBox navigationPanel = new HBox();
    private VBox filesBox = new VBox();
    private SplitPane filesPanel = new SplitPane();
    private ScrollPane filesMenu = new ScrollPane();
    private ScrollPane filesPane = new ScrollPane();
    private Scene scene;

    private Button parrentDir = getNavigateParentButton();

    private int directoryHistoryPosition = -1;
    private boolean isGridView = true;
    private boolean isHiddenFilesShow = false;
    private boolean isMenuHidden = false;
    private VBox root = new VBox();

    private FlowPane grid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root.getStyleClass().add("root");

        scene = new Scene(root, 935, 500);

        navigationPanel.getStyleClass().add("navigation-panel");
        filesPanel.getStyleClass().add("navigation-panel");

        filesPane.getStyleClass().add("files-pane");
        filesPanel.getStyleClass().add("files-panel");
        filesMenu.getStyleClass().add("files-menu");

        getDirectoryOptions();
        getMenuOptions();
        initDirectoryListInMenu();
        filesPanel.setMinHeight(root.getHeight() - navigationPanel.getHeight());

        filesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                directoryMenu.hide();
            }
        });

        filesPanel.getItems().addAll(filesMenu, filesPane);
        navigationPanel.setPadding(new Insets(6));
        SplitPane.setResizableWithParent(filesMenu, false);

        // Change display to grid or list
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

        CheckBox showFilesMenu = new CheckBox("Show Menu");
        showFilesMenu.setSelected(true);
        showFilesMenu.setOnAction(event -> {
            if(isMenuHidden) {
                filesPanel.getItems().addFirst(filesMenu);
                filesMenu.setMaxWidth(160);
            } else {
                filesPanel.getItems().remove(filesMenu);
            }
            isMenuHidden = !isMenuHidden;
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

        filesPanel.setDividerPositions(0.2);
        filesMenu.setMinWidth(100);
        filesMenu.setMaxWidth(270);
        filesMenu.setPrefWidth(160);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navigationPanel.getChildren().addAll(getNavigatePrevButton(), parrentDir, getNavigateNextButton(), getReloadButton(), spacer, getNavigateOptionsButton());
        root.getChildren().addAll(navigationPanel, filesPanel);
        filesList = new ListView<>();

        loadDirectoryContents(FilesOperations.getCurrentDirectory(), true);

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight() - navigationPanel.getHeight());
        });

        scene.getStylesheets().add(getClass().getResource("/assets/css/style.css").toExternalForm());

        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
    }

    private Button getNavigateNextButton() {
        Button button = new Button();
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-next")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getRightArrow());
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

    private CheckBox getShowHiddenFilesButton() {
        CheckBox showHiddenFiles = new CheckBox(Translator.translate("navigate.hidden-files"));
        VBox.setMargin(showHiddenFiles, new Insets(5, 10, 5, 10));
        showHiddenFiles.setSelected(false);
        showHiddenFiles.setOnAction(event -> {
            isHiddenFilesShow = !isHiddenFilesShow;
            refreshCurrentDirectory();
        });
        return showHiddenFiles;
    }

    private Button getNavigateOptionsButton() {
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        popOver.setDetachable(true);
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        Button aboutButton = new Button(Translator.translate("navigate.about-button"));
        aboutButton.setOnMouseClicked(e -> openWindowButton());
        container.getChildren().addAll(getShowHiddenFilesButton(), aboutButton);
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

        Label version = new Label("0.3.2");
        version.getStyleClass().add("about-version");

        Label description = new Label(Translator.translate("window.about.description"));
        description.getStyleClass().add("about-description");

        Hyperlink link = new Hyperlink(Translator.translate("window.about.website"));
        link.setOnAction(event -> {
            getHostServices().showDocument("https://github.com/Meiroth73/Aloe");
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
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-prev")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getLeftArrow());
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
        button.setTooltip(new Tooltip(Translator.translate("tooltip.navigate-parent")));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getTopArrow());
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

    private List<VBox> selectedFiles = new ArrayList<>();

    private void loadDirectoryContents(File directory, boolean addToHistory) {
        removeSelectionFromFiles();
        FilesOperations.setCurrentDirectory(directory);
        filesPane.setVvalue(0);


        checkParentDirectory();

        grid = new FlowPane();
        grid.setPadding(new Insets(5));
        grid.getStyleClass().add("files-grid");
        filesPane.setPadding(new Insets(5, 10, 10, 10));
        FlowPane.setMargin(grid, new Insets(5, 10, 10, 10));

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
                if (!isHiddenFilesShow) {
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

            if (isGridView) {
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
            FilesOperations.pasteFile();
            refreshCurrentDirectory();
        });
        directoryMenu.getItems().addAll(newDirectory, newFile, paste);
        filesPane.setOnContextMenuRequested(event -> {
            removeSelectionFromFiles();
            paste.setDisable(FilesOperations.isClipboardEmpty());
            directoryMenu.show(filesPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private boolean isSelected(VBox fileBox) {
        return selectedFiles.contains(fileBox);
    }

    private void getFileOptions(VBox item, String fileName) {
        ContextMenu fileMenu = new ContextMenu();
        MenuItem open = new MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            openFileInOptions(new File(FilesOperations.getCurrentDirectory(), fileName));
        });

        MenuItem copy = new MenuItem(Translator.translate("context-menu.copy"));
        copy.setOnAction(event -> {
            FilesOperations.copyFile(new File(FilesOperations.getCurrentDirectory(), fileName));
            refreshCurrentDirectory();
        });

        MenuItem rename = new MenuItem(Translator.translate("context-menu.rename"));
        rename.setOnAction(event -> {
            renameFile(new File(FilesOperations.getCurrentDirectory(), fileName));
            refreshCurrentDirectory();
        });

        MenuItem delete = new MenuItem(Translator.translate("context-menu.delete"));
        delete.setOnAction(event -> {
            FilesOperations.deleteFile(new File(FilesOperations.getCurrentDirectory(), fileName));
            refreshCurrentDirectory();
        });
        fileMenu.getItems().addAll(open, copy, rename, delete);

        item.setOnContextMenuRequested(event -> {
            if(isSelected(item) && selectedFiles.size() == 1) {
                fileMenu.show(item, event.getScreenX(), event.getScreenY());
            } else {
                fileMenu.show(item, event.getScreenX(), event.getScreenY());
                removeSelectionFromFiles();
            }
            directoryMenu.hide();
            event.consume();
        });
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

        renameButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
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
            return "Name cannot be empty";
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

        newDirectoryButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
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

        newFileButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
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

    private Map<String, String> directoryListInMenu = new LinkedHashMap<>();

    private void initDirectoryListInMenu() {
        directoryListInMenu.put(System.getProperty("user.home"), "Home");
        directoryListInMenu.put(System.getProperty("user.home") + "/Desktop", "Desktop");
        directoryListInMenu.put(System.getProperty("user.home") + "/Documents", "Documents");
        directoryListInMenu.put(System.getProperty("user.home") + "/Downloads", "Downloads");
        directoryListInMenu.put(System.getProperty("user.home") + "/Music", "Music");
        directoryListInMenu.put(System.getProperty("user.home") + "/Pictures", "Pictures");
        directoryListInMenu.put(System.getProperty("user.home") + "/Videos", "Videos");
        loadDirectoryListInMenu();
    }

    private void loadDirectoryListInMenu() {
        VBox containers = new VBox();
        filesMenu.getStyleClass().add("menu");
        for (Map.Entry<String, String> entry : directoryListInMenu.entrySet()) {
            Button button = new Button(entry.getValue());
            button.getStyleClass().add("menu-option");
            button.setAlignment(Pos.CENTER_LEFT);
            button.setPrefWidth(filesMenu.getHeight());
            getMenuItemsOptions(button, entry.getKey(), entry.getValue());
            button.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.PRIMARY) {
                    loadDirectoryContents(new File(entry.getKey()), true);
                }
            });
            filesMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
                button.setMinWidth(newValue.doubleValue());
            });
            containers.getChildren().add(button);
        }
        filesMenu.setContent(containers);
    }

    private void getMenuItemsOptions(Node item, String key, String value) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem open = new MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            loadDirectoryContents(new File(key), true);
        });
        MenuItem edit = new MenuItem(Translator.translate("context-menu.edit"));
        edit.setOnAction(event -> {
            editDirectoryInMenu(key, value);
            loadDirectoryListInMenu();
        });
        MenuItem remove = new MenuItem(Translator.translate("context-menu.remove"));
        remove.setOnAction(event -> {
            removeDirectoryFromMenu(key);
            loadDirectoryListInMenu();
        });
        contextMenu.getItems().addAll(open, edit, remove);
        item.setOnContextMenuRequested(event -> {
            contextMenu.show(item, event.getScreenX(), event.getScreenY());
            menuOptions.hide();
            event.consume();
        });
    }

    ContextMenu menuOptions = new ContextMenu();

    private void getMenuOptions() {
        menuOptions.getItems().clear();
        MenuItem add = new MenuItem(Translator.translate("context-menu.add"));
        add.setOnAction(event -> {
            addDirectoryInMenu();
            loadDirectoryListInMenu();
        });
        menuOptions.getItems().add(add);
        filesMenu.setOnContextMenuRequested(event -> {
            menuOptions.show(filesMenu, event.getScreenX(), event.getScreenY());
        });
        filesMenu.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                menuOptions.hide();
            }
        });
    }

    private void addDirectoryInMenu() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Directory");

        VBox dialogContent = new VBox();
        dialogContent.setPadding(new Insets(5));
        TextField name = new TextField("Name");
        TextField path = new TextField("/path/to/directory");
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");
        dialogContent.getChildren().addAll(name, path, error);
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);

        addButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            addDirectoryListInMenu(path.getText(), name.getText());
        });

        dialog.showAndWait();
    }

    private void addDirectoryListInMenu(String key, String value) {
        directoryListInMenu.put(key, value);
        loadDirectoryListInMenu();
    }

    private void editDirectoryInMenu(String key, String value) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Directory");

        VBox dialogContent = new VBox();
        dialogContent.setPadding(new Insets(5));
        TextField name = new TextField(value);
        TextField path = new TextField(key);
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");
        dialogContent.getChildren().addAll(name, path, error);
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);
        Button editButton = (Button) dialog.getDialogPane().lookupButton(editButtonType);

        editButton.setOnAction(event -> {
            replaceItemInDirectoryList(key, path.getText().trim(), name.getText().trim());
        });

        dialog.showAndWait();
    }

    private void removeDirectoryFromMenu(String key) {
        directoryListInMenu.remove(key);
    }

    private void replaceItemInDirectoryList(String oldKey, String newKey, String newValue) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : directoryListInMenu.entrySet()) {
            if(entry.getKey().equals(oldKey)) {
                tempMap.put(newKey, newValue);
            } else {
                tempMap.put(entry.getKey(), entry.getValue());
            }
        }
        directoryListInMenu.clear();
        directoryListInMenu.putAll(tempMap);
    }

    private void getParentDirectory() {
        if(FilesOperations.getCurrentDirectory().getPath() != "/") {
            loadDirectoryContents(new File(FilesOperations.getCurrentDirectory().getParent()), true);
        }
    }
}