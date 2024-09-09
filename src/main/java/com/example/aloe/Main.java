package com.example.aloe;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Main extends Application {

    private File currentDirectory = new File(System.getProperty("user.home"));
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

        loadDirectoryContents(currentDirectory, true);

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight() - navigationPanel.getHeight());
        });

//        root.widthProperty().addListener((observable, oldValue, newValue) -> {
//            grid.setMinHeight(grid.getHeight() + 65);
//            grid.setMaxHeight(grid.getHeight() + 75);
//        });

        scene.getStylesheets().add(getClass().getResource("/assets/css/style.css").toExternalForm());

        stage.setTitle(Translator.translate("root.title"));
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.setScene(scene);
        stage.show();
    }

    private Button getNavigateNextButton() {
        Button button = new Button();
        Tooltip tooltip = new Tooltip(Translator.translate("tooltip.navigate-next"));

        button.setTooltip(tooltip);
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
            loadDirectoryContents(currentDirectory, false);
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
        container.setAlignment(Pos.TOP_CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        icon.setFitHeight(120);
        icon.setFitWidth(120);
        VBox.setMargin(icon, new Insets(25, 10, 25, 10));

        Label name = new Label("Aloe");
        name.getStyleClass().add("about-name");
        name.setPadding(new Insets(25, 10, 5, 10));

        Label version = new Label("0.2.7");
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
        warranty.getStyleClass().add("about-warranty");
        warranty.setWrapText(true);

        container.getChildren().addAll(icon, name, version, description, link, warranty);

        Scene scene = new Scene(container, 300  , 370);
        scene.getStylesheets().add(getClass().getResource("/assets/css/style_about.css").toExternalForm());
        window.setScene(scene);
        window.setTitle(Translator.translate("window.about.title"));
        window.show();
    }

    private Button getNavigatePrevButton() {
        Button button = new Button();
        Tooltip tooltip = new Tooltip(Translator.translate("tooltip.navigate-prev"));

        button.setTooltip(tooltip);
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
        Tooltip tooltip = new Tooltip(Translator.translate("tooltip.navigate-parent"));

        button.setTooltip(tooltip);
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
        if (currentDirectory.getPath() == "/") {
            parrentDir.setDisable(true);
        } else {
            parrentDir.setDisable(false);
        }
    }

    private void loadDirectoryContents(File directory, boolean addToHistory) {
        currentDirectory = directory;
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

        File[] files = currentDirectory.listFiles();

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
                            loadDirectoryContents(new File(currentDirectory, dirName), true);
                        }
                    });
                    grid.getChildren().add(box);
                }

                for (String fileName : normalFiles) {
                    VBox box = createFileBox(fileName, false);
                    box.setOnMouseClicked(event -> {
                        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                            openFileInBackground(new File(currentDirectory, fileName));
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
                            File selectedFile = new File(currentDirectory, selectedItem);
                            if (selectedFile.isDirectory()) {
                                loadDirectoryContents(selectedFile, true);
                            } else {
                                openFileInBackground(selectedFile);
                            }
                        }
                    }
                });
            }
        }
    }

    ChangeListener<Number> heightListener;

    private VBox createFileBox(String name, boolean isDirectory) {
        VBox fileBox = new VBox();

        fileBox.setMinWidth(100);
        fileBox.setPrefWidth(100);
        fileBox.setMaxWidth(100);
        fileBox.setAlignment(Pos.TOP_CENTER);
        fileBox.setSpacing(10);
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
        fileBox.setPadding(new Insets(5));
        fileBox.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px;");
        getFileOptions(fileBox, name);
        return fileBox;
    }

    private void openFileInBackground(File file) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                openFile(file);
                return null;
            }
        };
        new Thread(task).start();
    }

    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported on this system.");
        }
    }

    private void refreshCurrentDirectory() {
        loadDirectoryContents(currentDirectory, false);
    }

    private void deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
        }
        file.delete();
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
            try {
                pasteFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            refreshCurrentDirectory();
        });
        directoryMenu.getItems().addAll(newDirectory, newFile, paste);
        filesPane.setOnContextMenuRequested(event -> {
            paste.setDisable(isClipboardNull());
            directoryMenu.show(filesPane, event.getScreenX(), event.getScreenY());
        });
    }

    private void getFileOptions(Node item, String fileName) {
        ContextMenu fileMenu = new ContextMenu();
        MenuItem open = new MenuItem(Translator.translate("context-menu.open"));
        open.setOnAction(event -> {
            openFileInOptions(new File(currentDirectory, fileName));
        });

        MenuItem copy = new MenuItem(Translator.translate("context-menu.copy"));
        copy.setOnAction(event -> {
           copyFile(new File(currentDirectory, fileName));
           refreshCurrentDirectory();
        });

        MenuItem rename = new MenuItem(Translator.translate("context-menu.rename"));
        rename.setOnAction(event -> {
            renameFile(new File(currentDirectory, fileName));
            refreshCurrentDirectory();
        });

        MenuItem delete = new MenuItem(Translator.translate("context-menu.delete"));
        delete.setOnAction(event -> {
            deleteFile(new File(currentDirectory, fileName));
           refreshCurrentDirectory();
        });
        fileMenu.getItems().addAll(open, copy, rename, delete);

        item.setOnContextMenuRequested(event -> {
            fileMenu.show(item, event.getScreenX(), event.getScreenY());
            directoryMenu.hide();
            event.consume();
        });
    }

    private void openFileInOptions(File file) {
        if (file.isDirectory()) {
            loadDirectoryContents(file, true);
        } else {
            openFileInBackground(file);
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
            File newFile = new File(currentDirectory, newName);

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
            File newFile = new File(currentDirectory, newName);
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
            File newFile = new File(currentDirectory, newName);
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

    private File clipboardOfCopiedFile = null;

    private void copyFile(File file) {
        clipboardOfCopiedFile = file;
    }

    private void pasteFile() throws IOException {
        if(!isClipboardNull()) {
            File newFile = new File(currentDirectory, clipboardOfCopiedFile.getName());
            if(clipboardOfCopiedFile.isDirectory()) {
                copyDirectoryToDestination(clipboardOfCopiedFile, newFile);
            } else {
                copyFileToDestination(clipboardOfCopiedFile, newFile);
            }
        }
    }

    private void copyFileToDestination(File source, File destination) throws IOException {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void copyDirectoryToDestination(File source, File destination) throws IOException {
        if(!destination.exists()) {
            destination.mkdir();
        }
        for (String file : Objects.requireNonNull(source.list())) {
            File sourceFile = new File(source, file);
            File destinationFile = new File(destination, file);
            if (sourceFile.isDirectory()) {
                copyDirectoryToDestination(sourceFile, destinationFile);
            } else {
                copyFileToDestination(sourceFile, destinationFile);
            }
        }
    }

    private boolean isClipboardNull() {
        return clipboardOfCopiedFile == null;
    }

    private void getParentDirectory() {
        if(currentDirectory.getPath() != "/") {
            loadDirectoryContents(new File(currentDirectory.getParent()), true);
        }
    }
}
