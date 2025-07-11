package com.example.aloe.window;

import com.example.aloe.components.BackButton;
import com.example.aloe.components.HBoxSpacer;
import com.example.aloe.components.VBoxSpacer;
import com.example.aloe.elements.files.FileImage;
import com.example.aloe.files.Checksum;
import com.example.aloe.files.FilesUtils;
import com.example.aloe.files.properties.*;
import com.example.aloe.files.permissions.ACLPermissions;
import com.example.aloe.files.permissions.POSIXPermissions;
import com.example.aloe.files.properties.Properties;
import com.example.aloe.settings.Settings;
import com.example.aloe.utils.ClipboardManager;
import com.example.aloe.utils.ffmpeg.FFmpegDownloader;
import com.example.aloe.utils.ffmpeg.FFmpegChecker;
import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
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
import javafx.stage.Stage;
import javafx.application.Platform;

import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jetbrains.annotations.NotNull;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.software.os.OSFileStore;

public class PropertiesWindow extends Stage {

    private File file;
    private final VBox root = new VBox();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private String hash = "";
    private OSFileStore store;

    public PropertiesWindow(@NotNull File file) {
        this.file = file;
        init();
    }

    public PropertiesWindow(@NotNull OSFileStore store) {
        this.store = store;
        init();
    }

    private void init() {
        this.setMinHeight(440);
        this.setMinWidth(350);

        root.getChildren().addAll(new Pane(), new Pane());
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, 300, 430);
        scene.getStylesheets().add(getClass().getResource("/assets/styles/" + Settings.getTheme() + "/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/assets/styles/structural/global.css").toExternalForm());
        scene.getStylesheets().add(String.format("data:text/css, .list-cell:selected, .confirm { -fx-background-color: %s; }", Settings.getColor()));
        this.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icons/folder.png")));
        this.setScene(scene);
        this.show();
        loadProperties();
    }

    private void loadChecksumButtonBar() {
        BackButton propertiesButton = new BackButton(Translator.translate("window.properties"), false);
        propertiesButton.setColor(Settings.getColor());
        propertiesButton.setOnMouseClicked(e -> loadProperties());
        HBox bar = new HBox(new HBoxSpacer(), propertiesButton);
        bar.getStyleClass().add("background");
        this.root.getChildren().set(0, bar);
    }

    private void loadPropertiesButtonBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("background");
        if (this.file.isFile()) {
            BackButton checksumButton = new BackButton(Translator.translate("window.properties.checksum"), true);
            checksumButton.setColor(Settings.getColor());
            checksumButton.setOnMouseClicked(e -> loadChecksum());
            bar.getChildren().add(checksumButton);
        }
        BackButton permissionsButton = new BackButton(Translator.translate("window.properties.permissions"), false);
        permissionsButton.setColor(Settings.getColor());
        permissionsButton.setOnMouseClicked(e -> loadPermissions());
        bar.getChildren().addAll(new HBoxSpacer(), permissionsButton);
        this.root.getChildren().set(0, bar);
    }

    private void loadPermissionsButtonBar() {
        BackButton propertiesButton = new BackButton(Translator.translate("window.properties"), true);
        propertiesButton.setColor(Settings.getColor());
        propertiesButton.setOnMouseClicked(e -> loadProperties());
        HBox bar = new HBox(propertiesButton, new HBoxSpacer());
        bar.getStyleClass().add("background");
        this.root.getChildren().set(0, bar);
    }

    private void loadDiskButtonBar() {
        BackButton propertiesButton = new BackButton(Translator.translate("window.properties.disk-properties"), true);
        propertiesButton.setColor(Settings.getColor());
        propertiesButton.setOnMouseClicked(e -> loadDiskProperties());
        HBox bar = new HBox(propertiesButton, new HBoxSpacer());
        bar.getStyleClass().add("background");
        this.root.getChildren().set(0, bar);
    }

    private void loadProperties() {
        if (this.store != null) {
            loadPartitionProperties();
            return;
        }
        loadPropertiesButtonBar();
        VBox content = new VBox();
        VBox.setVgrow(content, Priority.ALWAYS);
        content.getStyleClass().add("background");

        ImageView icon = getIcon(file);
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(26, 10, 10, 2));

        GridPane fileData = new GridPane();
        fileData.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(fileData, new Insets(30, 15, 0, 0));
        content.getChildren().addAll(iconWrapper, fileData);
        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties"));
        calculateFilesSizes();
        FileProperties properties = new FileProperties(file);

        try {
            Files.list(file.toPath());
        } catch (AccessDeniedException e) {
            Label label = new Label(Translator.translate("windows.properties.access-denied"));
            label.getStyleClass().add("text");
            fileData.add(label, 0, 0);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte index = 0;
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            Label title = getPropertiesLabel(entry.getKey());
            Label value = new Label(entry.getValue());
            value.getStyleClass().add("text");
            fileData.add(title, 0, index);
            fileData.add(value, 1, index);
            index++;
        }

        tryAddOtherProperties(fileData);
    }

    private void loadPartitionProperties() {
        VBox content = new VBox();
        loadDiskButtonBar();
        VBox.setVgrow(content, Priority.ALWAYS);
        content.getStyleClass().add("background");

        ImageView icon = getIcon("disk");
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(26, 10, 10, 2));

        GridPane fileData = new GridPane();
        fileData.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(fileData, new Insets(30, 15, 0, 0));
        content.getChildren().addAll(iconWrapper, fileData);
        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties.partition-properties"));
        calculateFilesSizes();
        PartitionProperties properties = new PartitionProperties(this.store);

        try {
            Files.list(Path.of(store.getMount()));
        } catch (AccessDeniedException e) {
            Label label = new Label(Translator.translate("windows.properties.access-denied"));
            label.getStyleClass().add("text");
            fileData.add(label, 0, 0);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte index = 0;
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            Label title = getPropertiesLabel(entry.getKey());
            Label value = new Label(entry.getValue());
            value.getStyleClass().add("text");
            fileData.add(title, 0, index);
            fileData.add(value, 1, index);
            index++;
        }
    }

    private void loadDiskProperties() {
        VBox content = new VBox();
        VBox.setVgrow(content, Priority.ALWAYS);
        content.getStyleClass().add("background");

        ImageView icon = getIcon("disk");
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(26, 10, 10, 2));

        GridPane fileData = new GridPane();
        fileData.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(fileData, new Insets(30, 15, 0, 0));
        content.getChildren().addAll(iconWrapper, fileData);
        this.root.getChildren().clear();
        this.root.getChildren().add(content);
        this.setTitle(Translator.translate("window.properties.disk-properties"));
        HWDiskStore disk = findDiskForFileStore(this.store);
        DiskProperties properties = new DiskProperties(disk);

        try {
            Files.list(Path.of(store.getMount()));
        } catch (AccessDeniedException e) {
            Label label = new Label(Translator.translate("windows.properties.access-denied"));
            label.getStyleClass().add("text");
            fileData.add(label, 0, 0);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte index = 0;
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            Label title = getPropertiesLabel(entry.getKey());
            Label value = new Label(entry.getValue());
            value.getStyleClass().add("text");
            fileData.add(title, 0, index);
            fileData.add(value, 1, index);
            index++;
        }

        List<OSFileStore> fileStores = new SystemInfo().getOperatingSystem().getFileSystem().getFileStores();
        for (HWPartition store : disk.getPartitions()) {
            OSFileStore matching = fileStores.stream()
                    .filter(fsItem -> fsItem.getMount().equals(store.getMountPoint()) || fsItem.getVolume().equals(store.getName()) || fsItem.getName().contains(store.getName()))
                    .findFirst()
                    .orElse(null);

            Button button = getLinkButton(Translator.translate("window.properties.file.show"));
            button.setStyle(button.getStyle() + String.format("-fx-text-fill: %s;", Settings.getColor()));
            button.setOnAction(event -> {
                this.store = matching;
                loadPartitionProperties();
            });

            fileData.add(getPropertiesLabel(String.format("%s %s: ", Translator.translate("window.properties.partition"), matching.getVolume())), 0, index);
            fileData.add(button, 1, index);
            index++;
        }
    }

    private static HWDiskStore findDiskForFileStore(OSFileStore fileStore) {
        for (HWDiskStore disk : new SystemInfo().getHardware().getDiskStores()) {
            for (HWPartition part : disk.getPartitions()) {
                if (part.getMountPoint() != null &&
                        part.getMountPoint().equals(fileStore.getMount())) {
                    return disk;
                }
            }
        }
        return null;
    }

    private Label getPropertiesLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text");
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPadding(new Insets(4, 10, 4, 0));
        label.setMinWidth(110);
        label.setMaxWidth(160);
        return label;
    }

    private void tryAddOtherProperties(GridPane grid) {
        String type = ((Label) grid.getChildren().get(5)).getText();

        if (Arrays.asList(new String[]{"image/jpeg", "image/png", "image/tiff", "image/gif", "image/bmp", "image/webp"}).contains(type)) {
            Button button = getLinkButton(Translator.translate("window.properties.file.show"));
            button.setStyle(button.getStyle() + String.format("-fx-text-fill: %s;", Settings.getColor()));
            button.setOnAction(event -> loadOtherProperties(new ImageProperties(this.file), "image"));
            grid.add(getPropertiesLabel(Translator.translate("window.properties.image")), 0, grid.getRowCount());
            grid.add(button, 1, grid.getRowCount() - 1);
        } else if (Arrays.asList(new String[]{"video/mp4", "video/x-matroska", "video/quicktime", "video/x-msvideo", "video/webm", "video/x-flv", "video/mpeg"}).contains(type)) {
            Button button = getLinkButton(Translator.translate("window.properties.file.show"));
            button.setStyle(button.getStyle() + String.format("-fx-text-fill: %s;", Settings.getColor()));
            button.setOnAction(event -> {
                if (FFmpegChecker.isAvailable() || FFmpegChecker.isDownloaded()) {
                    loadOtherProperties(new VideoProperties(this.file), "video");
                } else {
                    new ConfirmWindow(Translator.translate("window.properties.confirm.title"), Translator.translate("window.properties.confirm.description"), Translator.translate("button.download"), (e) -> {
                        try {
                            FFmpegDownloader.download();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            });
            grid.add(getPropertiesLabel(Translator.translate("window.properties.video")), 0, grid.getRowCount());
            grid.add(button, 1, grid.getRowCount() - 1);
        }
    }

    private Button getLinkButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("link-button");
        button.setStyle(String.format("-fx-text-fill: %s;", Settings.getColor()));
        return button;
    }

    private void calculateFilesSizes() {
        GridPane pane = (GridPane) ((VBox) (root.getChildren().get(1))).getChildren().get(1);
        CompletableFuture.supplyAsync(() -> UnitConverter.convert(file.isFile() ? file.length() : FilesUtils.calculateFileSize(this.file)), executor).thenAccept(result -> Platform.runLater(() -> ((Label) pane.getChildren().get(7)).setText(result)));
        CompletableFuture.supplyAsync(() -> UnitConverter.convert(file.getFreeSpace()), executor).thenAccept(result -> Platform.runLater(() -> ((Label) pane.getChildren().get(file.isFile() ? 15 : 17)).setText(result)));
    }

    private ImageView getIcon(File file) {
        ImageView icon = new ImageView();
        icon.setFitHeight(77);
        icon.setFitWidth(77);
        if (file.isDirectory()) {
            icon.setImage(loadIcon("/assets/icons/folder.png"));
        } else {
            icon.setImage(FileImage.from(file));
        }
        return icon;
    }

    private ImageView getIcon(String fileName) {
        ImageView icon = new ImageView();
        icon.setFitHeight(77);
        icon.setFitWidth(77);
        icon.setImage(loadIcon("/assets/icons/" + fileName + ".png"));
        return icon;
    }

    private Image loadIcon(String path) {
        return new Image(Objects.requireNonNull(PropertiesWindow.class.getResourceAsStream(path)));
    }

    private void loadChecksum() {
        loadChecksumButtonBar();

        VBox content = new VBox();
        content.getStyleClass().add("background");
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
        contentPane.getStyleClass().add("background");
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        Label choseAlgorithmLabel = new Label(Translator.translate("window.properties.checksum.chose-algorithm"));
        choseAlgorithmLabel.setPadding(new Insets(5));
        choseAlgorithmLabel.setStyle("-fx-font-size: 14.5px");
        choseAlgorithmLabel.getStyleClass().add("text");
        ComboBox<String> comboBox = getChecksumAlgorithmComboBox();
        setColorToComboBoxArrow(comboBox);

        Label enterChecksum = new Label(Translator.translate("window.properties.checksum.enter-checksum"));
        enterChecksum.setStyle("-fx-font-size: 14.5px");
        enterChecksum.getStyleClass().add("text");
        VBox.setMargin(enterChecksum, new Insets(0, 0, 10, 0));

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setStyle("-fx-border-color: " + Settings.getColor() + ";");

        Label infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 14px; -fx-padding: 4px");
        infoLabel.getStyleClass().add("text");

        Button verifyChecksum = new Button(Translator.translate("window.properties.checksum.verify-checksum"));
        verifyChecksum.getStyleClass().add("btn");
        verifyChecksum.setOnAction(e -> verifyChecksum(infoLabel, comboBox.getSelectionModel().getSelectedItem(), textArea.getText()));
        HBox buttonPanel = new HBox(verifyChecksum);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, enterChecksum, textArea, infoLabel, new HBoxSpacer(), new VBoxSpacer(), buttonPanel);
        TitledPane titledPane = new TitledPane(Translator.translate("window.properties.checksum.verify"), contentPane);
        VBox.setMargin(titledPane, new Insets(0, 0, 333, 0));
        return titledPane;
    }

    private TitledPane getGenerateChecksum() {
        VBox contentPane = new VBox();
        contentPane.getStyleClass().add("background");
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        Label choseAlgorithmLabel = new Label(Translator.translate("window.properties.checksum.chose-algorithm"));
        choseAlgorithmLabel.setPadding(new Insets(5));
        choseAlgorithmLabel.setStyle("-fx-font-size: 14.5px");
        choseAlgorithmLabel.getStyleClass().add("text");
        ComboBox<String> comboBox = getChecksumAlgorithmComboBox();
        setColorToComboBoxArrow(comboBox);

        Label checksumLabel = new Label(Translator.translate("window.properties.checksum") + ": ");
        checksumLabel.setStyle("-fx-font-size: 14.5px; -fx-padding: 5px 5px 1px 5px");
        checksumLabel.getStyleClass().add("text");
        Label hash = new Label();
        hash.getStyleClass().add("text");
        hash.setWrapText(true);
        hash.setStyle("-fx-font-size: 13.5px; -fx-padding: 4px 10px 10px 10px");

        Button copy = new Button(Translator.translate("button.copy"));
        copy.getStyleClass().add("btn");
        copy.setOnAction(e -> ClipboardManager.copyTextToClipboard(this.hash));
        Button generate = new Button(Translator.translate("window.properties.checksum.generate-hash"));
        generate.getStyleClass().add("btn");
        generate.setOnAction(e -> {
            hash.setText(Translator.translate("window.properties.checksum.generating"));
            generateHash(hash, comboBox.getSelectionModel().getSelectedItem());
        });
        HBox buttonPanel = new HBox(copy, generate);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setSpacing(10);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, checksumLabel, hash, new VBoxSpacer(), buttonPanel);
        TitledPane titledPane = new TitledPane(Translator.translate("window.properties.checksum.generate"), contentPane);
        titledPane.getStyleClass().add("background");
        return titledPane;
    }

    private void setColorToComboBoxArrow(ComboBox comboBox) {
        comboBox.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node arrow = comboBox.lookup(".arrow");
                if (arrow != null) {
                    arrow.setStyle("-fx-background-color: " + Settings.getColor() + ";");
                }
            });
        });

        String color = Settings.getSetting("appearance", "theme").equals("light") ? "#2e2f2f" : "#f6f6f6";
        comboBox.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                    setStyle("");
                }
            };

            cell.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
                if (isHovered) {
                    cell.setStyle("-fx-text-fill: " + Settings.getColor() + ";");
                } else {
                    cell.setStyle("-fx-text-fill: " + color);
                }
            });
            return cell;
        });
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
        applyToSubdirectories.setStyle("-fx-mark-color: " + Settings.getColor() + ";");

        Button updatePermissions = new Button(Translator.translate("window.properties.permissions.update"));
        updatePermissions.getStyleClass().add("btn");
        updatePermissions.setOnAction(e -> {
            permissions.setRecursively(applyToSubdirectories.isSelected());
            permissions.savePermissions(getSelectedPermissions());
        });

        VBox content = new VBox(permissionsGrid, applyToSubdirectories, updatePermissions);
        content.getStyleClass().add("background");
        content.setAlignment(Pos.TOP_CENTER);
        content.setMinWidth(330);
        VBox.setVgrow(content, Priority.ALWAYS);

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
        Line line = new Line(0, 0, 300, 0);
        line.getStyleClass().add("line");
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
        checkBox.setStyle("-fx-mark-color: " + Settings.getColor() + ";");
        checkBox.setSelected(isSelected);
        GridPane.setHalignment(checkBox, HPos.CENTER);
        GridPane.setValignment(checkBox, VPos.CENTER);
        return checkBox;
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text");
        label.setPadding(new Insets(10, 13, 10, 13));
        return label;
    }

    private void loadACLPermissions() {
        VBox content = new VBox();
        content.getStyleClass().add("background");
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

        Label choseUserLabel = getLabel(Translator.translate("window.properties.permissions.chose-user"));
        Label modifyPermissionsLabel = getLabel(Translator.translate("window.properties.permissions.modify-permissions"));

        Button updatePermissions = new Button(Translator.translate("window.properties.permissions.update"));
        updatePermissions.getStyleClass().addAll("btn", "confirm-btn");
        updatePermissions.setOnAction(e -> {
            permissions.savePermissions(readACLPermissions());
        });
        HBox.setMargin(updatePermissions, new Insets(10, 25, 10, 25));
        HBox bottomButtonWrapper = new HBox(new HBoxSpacer(), updatePermissions);

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
        permissionsPane.setPrefHeight(190);
        permissionsPane.setMaxHeight(540);

        VBox container = new VBox(choseUserLabel, usersListView, modifyPermissionsLabel, permissionsPane, bottomButtonWrapper);
        container.setMaxWidth(400);
        content.getChildren().addAll(container);
        VBox.setVgrow(permissionsPane, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);

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
        return (ScrollPane) ((VBox) ((VBox) (root.getChildren().get(1))).getChildren().getFirst()).getChildren().get(3);
    }

    private void loadACLPermissionsPane(List<String> permissionsList, List<Boolean> permissions) {
        ScrollPane pane = getScrollPane();
        pane.getStyleClass().add("scroll-pane-properties");
        GridPane permissionsGrid = new GridPane();
        permissionsGrid.getStyleClass().add("grid-properties");
        permissionsGrid.setPadding(new Insets(0, 0, 0, 50));

        for (byte i = 0; i < permissions.size(); i++) {
            permissionsGrid.addRow(i, getLabel(Translator.translate("window.properties.permissions.acl." + permissionsList.get(i).toLowerCase().replace('_', '-'))), getCheckBox(permissions.get(i)));
        }

        pane.setVvalue(0);
        pane.setFitToWidth(true);
        pane.setFitToHeight(true);
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

    private void loadOtherProperties(Properties properties, String type) {
        loadPermissionsButtonBar();
        VBox content = new VBox();
        content.getStyleClass().add("background");
        VBox.setVgrow(content, Priority.ALWAYS);

        GridPane data = new GridPane();
        data.setPadding(new Insets(5, 25, 5, 10));
        data.setAlignment(Pos.TOP_CENTER);

        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            Label value = new Label(entry.getValue());
            value.getStyleClass().add("text");
            data.addRow(data.getRowCount(), getPropertiesLabel(entry.getKey()), value);
        }

        content.getChildren().add(data);
        this.root.getChildren().set(1, content);
        this.setTitle(Translator.translate("window.properties." + type + "-properties"));
    }

    @Override
    public void close() {
        super.close();
        executor.shutdown();
    }
}
