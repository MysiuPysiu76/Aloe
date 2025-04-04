package com.example.aloe.window;

import com.example.aloe.*;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.files.properties.FileProperties;
import com.example.aloe.files.properties.ImageProperties;
import com.example.aloe.files.permissions.ACLPermissions;
import com.example.aloe.files.permissions.POSIXPermissions;
import com.example.aloe.settings.SettingsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jetbrains.annotations.NotNull;

public class PropertiesWindow extends Stage {

    private final File file;
    private final VBox root = new VBox();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private String hash = "";

    public PropertiesWindow(@NotNull File file) {
        this.file = file;
        this.setMinHeight(430);
        this.setMinWidth(350);

        root.getChildren().addAll(WindowComponents.getSpacer(), WindowComponents.getSpacer());

        this.setScene(new Scene(root, 300, 430));
        this.show();
        loadProperties();
    }

    private void loadChecksumButtonBar() {
        Button properties = getNavigateButton("window.properties", false);
        properties.setOnAction(e -> loadProperties());
        this.root.getChildren().set(0, new HBox(WindowComponents.getSpacer(), properties));
    }

    private void loadPropertiesButtonBar() {
        HBox bar = new HBox();
        if (this.file.isFile()) {
            Button checksum = getNavigateButton("window.properties.checksum", true);
            checksum.setOnAction(event -> loadChecksum());
            bar.getChildren().add(checksum);
        }
        Button permissions = getNavigateButton("window.properties.permissions", false);
        permissions.setOnAction(event -> {
            loadPermissions();
        });
        bar.getChildren().addAll(WindowComponents.getSpacer(), permissions);
        this.root.getChildren().set(0, bar);
    }

    private void loadPermissionsButtonBar() {
        Button properties = getNavigateButton("window.properties", true);
        properties.setOnAction(e -> loadProperties());
        this.root.getChildren().set(0, new HBox(properties, WindowComponents.getSpacer()));
    }

    private void loadProperties() {
        loadPropertiesButtonBar();
        VBox content = new VBox();

        ImageView icon = getIcon(file, false);
        icon.setFitHeight(77);
        icon.setFitWidth(77);
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(25, 10, 10, 2));

        GridPane fileData = new GridPane();
        fileData.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(fileData, new Insets(30, 15, -20, 0));
        FileProperties properties = new FileProperties(file);

        byte index = 0;
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            Label title = getPropertiesLabel(entry.getKey());
            Label value = new Label(entry.getValue());
            fileData.add(title, 0, index);
            fileData.add(value, 1, index);
            index++;
        }

        tryAddOtherProperties(fileData);

        content.getChildren().addAll(iconWrapper, fileData);
        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties"));
        calculateFilesSizes();
    }

    private Label getPropertiesLabel(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPadding(new Insets(4, 10, 4, 0));
        label.setMinWidth(110);
        label.setMaxWidth(160);
        return label;
    }

    private void tryAddOtherProperties(GridPane grid) {
        String type = ((Label) grid.getChildren().get(5)).getText();

        if (Arrays.asList(new String[]{"image/jpeg", "image/png", "image/tiff", "image/gif", "image/bmp", "image/webp"}).contains(type)) {
            Button button = getLinkButton(Translator.translate("show"));
            button.setOnAction(event -> loadImageProperties());
            grid.add(getPropertiesLabel(Translator.translate("window.properties.image")), 0, grid.getRowCount());
            grid.add(button, 1, grid.getRowCount() - 1);
        }
    }

    private Button getLinkButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 2 0 2 0; -fx-text-decoration: underline; -fx-text-fill: #24baba; -fx-cursor: pointer;");
        return button;
    }

    private void calculateFilesSizes() {
        GridPane pane = (GridPane) ((VBox) (root.getChildren().get(1))).getChildren().get(1);
        CompletableFuture.supplyAsync(() -> Utils.convertBytesByUnit(file.isFile() ? file.length() : FilesUtils.calculateFileSize(this.file)), executor).thenAccept(result -> Platform.runLater(() -> ((Label) pane.getChildren().get(7)).setText(result)));
        CompletableFuture.supplyAsync(() -> Utils.convertBytesByUnit(file.getFreeSpace()), executor).thenAccept(result -> Platform.runLater(() -> ((Label) pane.getChildren().get(file.isFile() ? 15 : 17)).setText(result)));
    }

    private ImageView getIcon(File file, boolean useThumbnails) {
        ImageView icon = new ImageView();
        if (file.isDirectory()) {
            icon.setImage(loadIcon("/assets/icons/folder.png"));
        } else {
            icon.setImage(loadIconForFile(file, useThumbnails));
        }
        return icon;
    }

    private Image loadIcon(String path) {
        return new Image(Objects.requireNonNull(PropertiesWindow.class.getResourceAsStream(path)));
    }

    private Image loadIconForFile(File file, boolean useThumbnails) {
        return switch (FilesUtils.getExtension(file).toLowerCase()) {
            case "jpg", "jpeg", "png", "gif" ->
                    useThumbnails && Boolean.TRUE.equals(SettingsManager.getSetting("files", "display-thumbnails")) ? new Image(new File(FilesOperations.getCurrentDirectory(), file.getName()).toURI().toString()) : loadIcon("/assets/icons/image.png");
            case "mp4" -> loadIcon("/assets/icons/video.png");
            case "mp3", "ogg" -> loadIcon("/assets/icons/music.png");
            case "iso" -> loadIcon("/assets/icons/cd.png");
            default -> loadIcon("/assets/icons/file.png");
        };
    }

    private Button getNavigateButton(String key, boolean leftIcon) {
        Button button = WindowComponents.getBackButton(key, leftIcon);
        button.setFont(Font.font(14 * 0.95));
        button.setAlignment(Pos.CENTER);
        return button;
    }

    private void loadChecksum() {
        loadChecksumButtonBar();

        VBox content = new VBox();
        Accordion checksumAccordion = new Accordion(getVerifyChecksum(), getGenerateChecksum());
        checksumAccordion.setExpandedPane(checksumAccordion.getPanes().get(1));
        VBox.setVgrow(checksumAccordion, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);
        content.getChildren().add(checksumAccordion);

        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties.checksum"));
    }

    private TitledPane getVerifyChecksum() {
        VBox contentPane = new VBox();
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        Label choseAlgorithmLabel = new Label(Translator.translate("window.properties.checksum.chose-algorithm"));
        choseAlgorithmLabel.setPadding(new Insets(5));
        choseAlgorithmLabel.setStyle("-fx-font-size: 14.5px");
        ComboBox<String> comboBox = getChecksumAlgorithmComboBox();

        Label enterChecksum = new Label(Translator.translate("window.properties.checksum.enter-checksum"));
        enterChecksum.setStyle("-fx-font-size: 14.5px");
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(3);
        textArea.setStyle("-fx-font-size: 14px");

        Label infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 14px; -fx-padding: 4px");

        Button verifyChecksum = WindowComponents.getButton(Translator.translate("window.properties.checksum.verify-checksum"));
        verifyChecksum.setOnAction(e -> verifyChecksum(infoLabel, comboBox.getSelectionModel().getSelectedItem(), textArea.getText()));
        HBox buttonPanel = new HBox(verifyChecksum);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, enterChecksum, textArea, infoLabel, WindowComponents.getSpacer(), buttonPanel);
        return new TitledPane(Translator.translate("window.properties.checksum.verify"), contentPane);
    }

    private TitledPane getGenerateChecksum() {
        VBox contentPane = new VBox();
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        Label choseAlgorithmLabel = new Label(Translator.translate("window.properties.checksum.chose-algorithm"));
        choseAlgorithmLabel.setPadding(new Insets(5));
        choseAlgorithmLabel.setStyle("-fx-font-size: 14.5px");
        ComboBox<String> comboBox = getChecksumAlgorithmComboBox();

        Label checksumLabel = new Label(Translator.translate("window.properties.checksum") + ": ");
        checksumLabel.setStyle("-fx-font-size: 14.5px; -fx-padding: 5px 5px 1px 5px");
        Label hash = new Label();
        hash.setWrapText(true);
        hash.setStyle("-fx-font-size: 13.5px; -fx-padding: 4px 10px 10px 10px");

        Button copy = WindowComponents.getButton(Translator.translate("button.copy"));
        copy.setOnAction(e -> ClipboardManager.copyTextToClipboard(this.hash));
        Button generate = WindowComponents.getButton(Translator.translate("window.properties.checksum.generate-hash"));
        generate.setOnAction(e -> generateHash(hash, comboBox.getSelectionModel().getSelectedItem()));
        HBox buttonPanel = new HBox(copy, generate);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setSpacing(10);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, checksumLabel, hash, WindowComponents.getVBoxSpacer(), buttonPanel);
        return new TitledPane(Translator.translate("window.properties.checksum.generate"), contentPane);
    }

    private void generateHash(Label labelHash, String algorithm) {
        hash = new Checksum(file).generateChecksum(algorithm);
        labelHash.setText(hash);
    }

    private void verifyChecksum(Label info, String algorithm, String hash) {
        if (new Checksum(file).verifyChecksum(algorithm, hash)) {
            info.setText(Translator.translate("window.properties.checksum.verify.equals"));
        } else {
            info.setText(Translator.translate("window.properties.checksum.verify.not-equals"));
        }
    }

    private ComboBox<String> getChecksumAlgorithmComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        List<String> algorithms = new ArrayList<>(java.security.Security.getAlgorithms("MessageDigest"));
        Collections.sort(algorithms);
        comboBox.getItems().addAll(algorithms);
        comboBox.setVisibleRowCount(9);
        comboBox.getSelectionModel().select("SHA-256");
        return comboBox;
    }

    private void loadPermissions() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            loadPOSIXPermissions();
        } else {
            loadACLPermissions();
        }
        loadPermissionsButtonBar();
        this.setTitle(Translator.translate("window.properties.permissions"));
    }

    private void loadPOSIXPermissions() {
        POSIXPermissions permissions = new POSIXPermissions(file);
        GridPane permissionsGrid = loadPOSIXPermissionsGrid(permissions.loadPermissions());
        permissionsGrid.setAlignment(Pos.CENTER);

        CheckBox applyToSubdirectories = new CheckBox(Translator.translate("window.properties.permissions.apply-to-subdirectories"));
        applyToSubdirectories.setPadding(new Insets(35, 10, 15, 10));
        applyToSubdirectories.setDisable(file.isFile());

        Button updatePermissions = WindowComponents.getConfirmButton(Translator.translate("window.properties.permissions.update"));
        updatePermissions.setOnAction(e -> {
            permissions.setRecursively(applyToSubdirectories.isSelected());
            permissions.savePermissions(getSelectedPermissions());
        });

        VBox content = new VBox(permissionsGrid, applyToSubdirectories, updatePermissions);
        content.setAlignment(Pos.TOP_CENTER);
        content.setMinWidth(330);

        root.getChildren().set(1, content);
    }

    private GridPane loadPOSIXPermissionsGrid(List<Boolean> permissionsList) {
        GridPane permissionsGrid = new GridPane();
        permissionsGrid.setAlignment(Pos.CENTER);
        permissionsGrid.setPadding(new Insets(25, 0, 0, 0));

        List<String> title = List.of(Translator.translate("window.properties.permissions.owner"), Translator.translate("window.properties.permissions.group"), Translator.translate("window.properties.permissions.other"));

        Label read = getLabel(Translator.translate("window.properties.permissions.read"));
        Label write = getLabel(Translator.translate("window.properties.permissions.write"));
        Label execute = getLabel(Translator.translate("window.properties.permissions.execute"));

        permissionsGrid.addRow(0, new Label(), read, write, execute);
        addRowSeparator(1, permissionsGrid);

        for (int i = 0; i < 3; i++) {
            Label roleLabel = getLabel(title.get(i));
            CheckBox readBox = getCheckBox(permissionsList.get(i * 3));
            CheckBox writeBox = getCheckBox(permissionsList.get(i * 3 + 1));
            CheckBox executeBox = getCheckBox(permissionsList.get(i * 3 + 2));

            permissionsGrid.addRow(i * 2 + 2, roleLabel, readBox, writeBox, executeBox);
            if (i < 2) {
                addRowSeparator(i * 2 + 3, permissionsGrid);
            }
        }
        return permissionsGrid;
    }

    private void addRowSeparator(int row, GridPane grid) {
        Line line = new Line(0, 0, 290, 0);
        grid.add(line, 0, row);
        GridPane.setColumnSpan(line, 4);
    }

    private GridPane getPermissionsGrid(VBox parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof GridPane) {
                return (GridPane) node;
            }
        }
        return null;
    }

    private List<Boolean> getSelectedPermissions() {
        List<Boolean> permissions = new ArrayList<>();
        GridPane permissionsGrid = getPermissionsGrid((VBox) root.getChildren().get(1));

        for (Node node : permissionsGrid.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                permissions.add(checkBox.isSelected());
            }
        }
        return permissions;
    }

    private CheckBox getCheckBox(boolean isSelected) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(isSelected);
        GridPane.setHalignment(checkBox, HPos.CENTER);
        GridPane.setValignment(checkBox, VPos.CENTER);
        return checkBox;
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        label.setPadding(new Insets(10, 13, 10, 13));
        return label;
    }

    private void loadACLPermissions() {
        VBox content = new VBox();
        content.setAlignment(Pos.TOP_CENTER);
        ListView<String> usersListView = new ListView<>();
        ACLPermissions permissions = new ACLPermissions(this.file);

        usersListView.setItems(getUsersList());
        usersListView.setMinWidth(100);
        usersListView.setPrefWidth(200);
        usersListView.setMaxHeight(75);

        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                permissions.setUserName(newUser);
                loadACLPermissionsPane(permissions.getPermissionsList(), permissions.loadPermissions());
            }
        });

        Label choseUserLabel = new Label(Translator.translate("window.properties.permissions.chose-user"));
        Label modifyPermissionsLabel = new Label(Translator.translate("window.properties.permissions.modify-permissions"));

        Button updatePermissions = WindowComponents.getConfirmButton(Translator.translate("window.properties.permissions.update"));
        updatePermissions.setOnAction(e -> {
            permissions.savePermissions(readACLPermissions());
        });
        HBox.setMargin(updatePermissions, new Insets(10, 25, 10, 25));
        HBox bottomButtonWrapper = new HBox(WindowComponents.getSpacer(), updatePermissions);

        AclFileAttributeView view = Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class);
        List<String> users = new ArrayList<>();
        try {
            for (AclEntry entry : view.getAcl()) {
                users.add(entry.principal().getName());
                Collections.sort(users);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ScrollPane permissionsPane = new ScrollPane();
        VBox.setVgrow(permissionsPane, Priority.ALWAYS);
        permissionsPane.setPrefHeight(200);
        permissionsPane.setMaxHeight(600);

        VBox container = new VBox(choseUserLabel, usersListView, modifyPermissionsLabel, permissionsPane, bottomButtonWrapper);
        container.setMaxWidth(400);
        content.getChildren().addAll(container);
        this.root.getChildren().set(1, content);
        usersListView.getSelectionModel().selectFirst();
    }

    private ObservableList<String> getUsersList() {
        ObservableList<String> usersList = FXCollections.observableArrayList();
        try {
            AclFileAttributeView view = Files.getFileAttributeView(this.file.toPath(), AclFileAttributeView.class);
            if (view == null) {
                return FXCollections.observableArrayList();
            }
            usersList.clear();
            for (AclEntry entry : view.getAcl()) {
                usersList.add(entry.principal().getName());
            }
            usersList.sorted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FXCollections.sort(usersList);
        return usersList;
    }

    private ScrollPane getScrollPane() {
        return (ScrollPane) ((VBox) ((VBox) (root.getChildren().get(1))).getChildren().get(0)).getChildren().get(3);
    }

    private void loadACLPermissionsPane(List<String> permissionsList, List<Boolean> permissions) {
        ScrollPane pane = getScrollPane();
        GridPane permissionsGrid = new GridPane();

        System.out.println(permissions.size());
        for (byte i = 0; i < permissions.size(); i++) {
            permissionsGrid.addRow(i, getLabel(Translator.translate("window.properties.permissions.acl." + permissionsList.get(i).toLowerCase().replace('_', '-'))), getCheckBox(permissions.get(i)));
        }

        pane.setVvalue(0);
        pane.setContent(permissionsGrid);
    }

    private List<Boolean> readACLPermissions() {
        List<Boolean> permissions = new ArrayList<>();
        GridPane permissionsGrid = (GridPane) getScrollPane().getContent();

        for (Node node : permissionsGrid.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                permissions.add(checkBox.isSelected());
            }
        }

        return permissions;
    }

    private void loadImageProperties() {
        loadPermissionsButtonBar();
        VBox content = new VBox();

        GridPane imageData = new GridPane();
        imageData.setPadding(new Insets(5, 25, 5, 10));
        imageData.setAlignment(Pos.TOP_CENTER);
        ImageProperties imageProperties = new ImageProperties(this.file);

        for (Map.Entry<String, String> entry : imageProperties.getProperties().entrySet()) {
            imageData.addRow(imageData.getRowCount(), getPropertiesLabel(entry.getKey()), new Label(entry.getValue()));
        }

        content.getChildren().add(imageData);
        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties.image-properties"));
    }

    @Override
    public void close() {
        super.close();
        executor.shutdown();
    }
}
