package com.example.aloe;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    private File currentDirectory = new File(System.getProperty("user.home"));
    private List<File> directoryHistory = new ArrayList<>();
    private ListView<String> filesList;
    private VBox filesBox;
    private SplitPane filesPanel = new SplitPane();
    private ScrollPane filesPane = new ScrollPane();
    private VBox filesMenu = new VBox();

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

        filesPanel.getItems().addAll(filesMenu, filesPane);

        filesBox = new VBox();

        navigationPanel.setPadding(new Insets(6));

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
        filesMenu.setMinWidth(120);
        filesMenu.setMaxWidth(270);
        filesMenu.setPrefWidth(160);

        ListView<String> filesMenuList = new ListView<>();
        filesMenuList.getItems().addAll("Desktop", "Documents", "Downloads", "Music", "Pictures", "Videos");
        filesMenu.getChildren().add(filesMenuList);

        filesMenuList.setOnMouseClicked((event) -> {
            String selectedItem = filesMenuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                File selectedFile = new File(System.getProperty("user.home"), selectedItem);
                if (selectedFile.isDirectory()) {
                    loadDirectoryContents(selectedFile, true);
                } else {
                    openFileInBackground(selectedFile);
                }
            }
        });

        filesMenuList.setPadding(new Insets(5));

        navigationPanel.getChildren().addAll(getNavigateButton("prev"), getNavigateButton("next"), reload, showHiddenFiles, changeDisplay, showFilesMenu);
        root.getChildren().addAll(navigationPanel, filesPanel);

        filesList = new ListView<>();

        loadDirectoryContents(currentDirectory, true);
        VBox.setVgrow(filesMenuList, Priority.ALWAYS);

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            filesPanel.setMinHeight(stage.getHeight());
        });

        navigationPanel.setMargin(reload, new Insets(5, 15, 5, 15));

        Scene scene = new Scene(root, 935, 550);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Files");
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(700);
        stage.show();
    }

    private Button getNavigateButton(String type) {
        Line line1;
        Line line2;
        Button button = new Button();

        if(type.equals("prev")) {
            line1 = new Line(10, 5 , 5,10);
            line2 = new Line(5, 10, 10, 15);

            button.setOnMouseClicked(event -> {
                if (directoryHistoryPosition > 0) {
                    directoryHistoryPosition--;
                    loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
                }
            });
            button.getStyleClass().add("prev-directory");
        } else if (type.equals("next")) {
            line1 = new Line(5, 5, 10, 10);
            line2 = new Line(10, 10, 5, 15);

            button.setOnMouseClicked(event -> {
                if (directoryHistoryPosition < directoryHistory.size() - 1) {
                    directoryHistoryPosition++;
                    loadDirectoryContents(directoryHistory.get(directoryHistoryPosition), false);
                }
            });

            button.getStyleClass().add("next-directory");
        } else {
            throw new IllegalArgumentException("Incorrect button type");
        }

        line1.setStroke(Color.BLACK);
        line2.setStroke(Color.BLACK);
        line1.setStrokeWidth(2.5);
        line2.setStrokeWidth(2.5);

        button.setPadding(new Insets(7, 13, 10, 10));
        button.setAlignment(Pos.CENTER);
        button.setGraphic(new Pane(line1, line2));

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
                        loadDirectoryContents(new File(currentDirectory, dirName), true);
                    });
                    grid.getChildren().add(box);
                }

                for (String fileName : normalFiles) {
                    VBox box = createFileBox(fileName, false);
                    box.setOnMouseClicked(event -> {
                        openFileInBackground(new File(currentDirectory, fileName));
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

    private void getFileOptions(Node item, String name) {
        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        menu.getItems().addAll(deleteItem);
        deleteItem.setOnAction(event -> {
            deleteFile(new File(currentDirectory, name));
            refreshCurrentDirectory();
        });

        item.setOnContextMenuRequested(event -> {
            menu.show(item, event.getScreenX(), event.getScreenY());
        });
    }
}
