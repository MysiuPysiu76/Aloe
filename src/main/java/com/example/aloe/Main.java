package com.example.aloe;

import javafx.application.Application;
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
import javafx.util.Duration;

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
    private final VBox filesBox = new VBox();
    private final SplitPane filesPanel = new SplitPane();
    private final ScrollPane filesPane = new ScrollPane();
    private final VBox filesMenu = new VBox();

    private int directoryHistoryPosition = -1;
    private boolean isGridView = true;
    private boolean isHiddenFilesShow = false;
    private boolean isMenuHidden = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        HBox navigationPanel = new HBox();

        getDirectoryOptions();
        initDirectoryListInMenu();

        filesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                directoryMenu.hide();
            }
        });

        filesPanel.getItems().addAll(filesMenu, filesPane);

        navigationPanel.setPadding(new Insets(6));

        SplitPane.setResizableWithParent(filesMenu, false);

        // Reload files list button
        Button reload = new Button("Reload");
        reload.setOnMouseClicked(event -> {
            loadDirectoryContents(currentDirectory, false);
        });
        reload.setPadding(new Insets(5, 10, 5, 10));

        // Show hidden files button
        CheckBox showHiddenFiles = new CheckBox("Hidden files");
        showHiddenFiles.setSelected(false);
        showHiddenFiles.setOnAction(event -> {
            isHiddenFilesShow = !isHiddenFilesShow;
            refreshCurrentDirectory();
        });

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

        filesPanel.setDividerPositions(0.2);
        filesMenu.setMinWidth(100);
        filesMenu.setMaxWidth(270);
        filesMenu.setPrefWidth(160);

        navigationPanel.getChildren().addAll(getNavigatePrevButton(), getNavigateParentButton(), getNavigateNextButton(), reload, showHiddenFiles, changeDisplay, showFilesMenu);
        root.getChildren().addAll(navigationPanel, filesPanel);

        filesList = new ListView<>();

        loadDirectoryContents(currentDirectory, true);

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight());
        });

        HBox.setMargin(reload, new Insets(5, 15, 5, 15));

        Scene scene = new Scene(root, 935, 550);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Files");
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.show();
    }

    private Button getNavigateNextButton() {
        Button button = new Button();
        Tooltip tooltip = new Tooltip("Next Directory");

        button.setTooltip(tooltip);
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getRightArrow());
        button.setPadding(new Insets(7, 13, 10, 10));
        button.getStyleClass().add("next-directory");

        button.setOnMouseClicked(event -> {
            if (directoryHistoryPosition < directoryHistory.size() - 1) {
                directoryHistoryPosition++;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });

        tooltip.setShowDelay(Duration.seconds(1));
        tooltip.setHideDelay(Duration.seconds(10));
        button.setOnMouseExited(event -> tooltip.hide());

        return button;
    }

    private Button getNavigatePrevButton() {
        Button button = new Button();
        Tooltip tooltip = new Tooltip("Prev Directory");

        button.setTooltip(tooltip);
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getLeftArrow());
        button.setPadding(new Insets(7, 13, 10, 10));
        button.getStyleClass().add("prev-directory");

        button.setOnMouseClicked(event -> {
            if (directoryHistoryPosition > 0) {
                directoryHistoryPosition--;
                loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
            }
        });

        tooltip.setShowDelay(Duration.seconds(1));
        tooltip.setHideDelay(Duration.seconds(10));
        button.setOnMouseExited(event -> tooltip.hide());

        return button;
    }

    private Button getNavigateParentButton() {
        Button button = new Button();
        Tooltip tooltip = new Tooltip("Parent Directory");

        button.setTooltip(tooltip);
        button.setAlignment(Pos.CENTER);
        button.setGraphic(ArrowLoader.getTopArrow());
        button.setPadding(new Insets(9, 11, 9, 8));
        button.getStyleClass().add("parent-directory");

        button.setOnMouseClicked(event -> {
            getParentDirectory();
        });

        tooltip.setShowDelay(Duration.seconds(1));
        tooltip.setHideDelay(Duration.seconds(10));
        button.setOnMouseExited(event -> tooltip.hide());

        return button;
    }

    private void adjustSpacing(FlowPane grid, double width) {
        int elementWidth = 90;
        int gap = 20;

        int elementsInRow = (int) (width / (elementWidth + gap));
        if (elementsInRow > 0) {
            double totalElementWidth = elementsInRow * elementWidth + (elementsInRow - 1) * gap;
            double remainingSpace = width - totalElementWidth;

            if (elementsInRow == 1) {
                grid.setPadding(new Insets(10, remainingSpace / 2, 10, remainingSpace / 2));
            } else {
                grid.setPadding(new Insets(10, 0, 10, 0));
            }
        } else {
            grid.setPadding(new Insets(10));
        }
    }

    private void loadDirectoryContents(File directory, boolean addToHistory) {
        currentDirectory = directory;
        filesPane.setVvalue(0);

        FlowPane grid = new FlowPane();
        grid.setPadding(new Insets(10, 10, 100, 10));

        // Clear previous contents to avoid duplication
        filesBox.getChildren().clear();
        filesList.getItems().clear();

        // Add directory to history
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
            } else {
                filesList.getItems().addAll(directories);
                filesList.getItems().addAll(normalFiles);
                filesBox.getChildren().add(filesList);
                filesPane.setContent(filesBox);
            }
        }

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
            icon.setImage(new Image(getClass().getResourceAsStream("/icons/folder.png")));
        } else {
            icon.setImage(new Image(getClass().getResourceAsStream("/icons/file.png")));
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
        MenuItem newDirectory = new MenuItem("New Folder");
        newDirectory.setOnAction(event -> {
           createDirectory();
           refreshCurrentDirectory();
        });
        MenuItem newFile = new MenuItem("New file");
        newFile.setOnAction(event -> {
           createFile();
           refreshCurrentDirectory();
        });
        MenuItem paste = new MenuItem("Paste");
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
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(event -> {
           copyFile(new File(currentDirectory, fileName));
           refreshCurrentDirectory();
        });

        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(event -> {
            renameFile(new File(currentDirectory, fileName));
            refreshCurrentDirectory();
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            deleteFile(new File(currentDirectory, fileName));
           refreshCurrentDirectory();
        });
        fileMenu.getItems().addAll(copy, rename, delete);

        item.setOnContextMenuRequested(event -> {
            fileMenu.show(item, event.getScreenX(), event.getScreenY());
            directoryMenu.hide();
            event.consume();
        });
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
        filesMenu.getChildren().clear();
        for (Map.Entry<String, String> entry : directoryListInMenu.entrySet()) {
            Button button = new Button(entry.getValue());
            button.getStyleClass().add("menu-option");
            button.setAlignment(Pos.CENTER_LEFT);
            button.setPrefWidth(filesMenu.getWidth());
            getMenuItemsOptions(button, entry.getKey(), entry.getValue());
            button.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.PRIMARY) {
                    loadDirectoryContents(new File(entry.getKey()), true);
                }
            });
            filesMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
                button.setMinWidth(newValue.doubleValue());
            });
            filesMenu.getChildren().add(button);
        }
    }

    private void getMenuItemsOptions(Node item, String key, String value) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> {
            loadDirectoryContents(new File(key), true);
        });
        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(event -> {
            editDirectoryInMenu(key, value);
            loadDirectoryListInMenu();
        });
        MenuItem remove = new MenuItem("Remove");
        contextMenu.getItems().addAll(open, edit, remove);
        remove.setOnAction(event -> {
            removeDirectoryFromMenu(key);
            loadDirectoryListInMenu();
        });
        item.setOnContextMenuRequested(event -> {
            contextMenu.show(item, event.getScreenX(), event.getScreenY());
            event.consume();
        });
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
