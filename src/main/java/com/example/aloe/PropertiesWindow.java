package com.example.aloe;

import com.example.aloe.settings.SettingsManager;
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

import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import org.apache.tika.Tika;

public class PropertiesWindow extends Stage {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private String hash = "";
    private final File file;
    private GridPane permissions;
    private final short height;

    public PropertiesWindow(File file) {
        this.file = file;
        height = (short) (file.isFile() ? 385 : 410);
        this.setMinHeight(height);
        this.setMinWidth(330);
        loadProperties();
        this.show();
    }

    private void loadProperties() {
        VBox root = new VBox();
        HBox navigate = new HBox();

        if (file.isFile()) {
            Button checksum = getNavigateButton("window.properties.checksum", true);
            checksum.setOnAction(event -> loadChecksum());
            VBox checksumWrapper = new VBox(checksum);
            checksumWrapper.setAlignment(Pos.CENTER_LEFT);
            navigate.getChildren().add(checksumWrapper);
        }

        Button permissions = getNavigateButton("window.properties.permissions", false);
        permissions.setOnAction(event -> loadPermissions());
        VBox permissionWrapper = new VBox(permissions);
        permissionWrapper.setAlignment(Pos.CENTER_RIGHT);
        navigate.getChildren().addAll(WindowComponents.getSpacer(), permissionWrapper);

        ImageView icon = getIcon(file, false);
        icon.setFitHeight(75);
        icon.setFitWidth(75);
        VBox iconWrapper = new VBox();
        iconWrapper.setAlignment(Pos.TOP_CENTER);
        iconWrapper.getChildren().add(icon);
        VBox.setMargin(icon, new Insets(20, 10, 10, 2));

        List<String> names = getFilePropertiesNames();
        List<String> values = getFilePropertiesValues(file);

        if (file.isFile()) {
            this.setTitle(Translator.translate("window.properties.file-properties"));
            try {
                values.add(2, new Tika().detect(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.setTitle(Translator.translate("window.properties.folder-properties"));
            values.add(2, Translator.translate("window.properties.folder"));
            names.add(5, Translator.translate("window.properties.folder-contents"));
            try {
                values.add(5, Files.list(Path.of(file.getPath())).count() + Translator.translate("window.properties.items"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        GridPane fileData = new GridPane();
        VBox.setMargin(fileData, new Insets(25, 0, -20, 0));

        List<Label> valueLabels = new ArrayList<>();
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
            valueLabels.add(value);
        }
        root.getChildren().addAll(navigate, iconWrapper, fileData);
        Scene scene = new Scene(root, 330, height);
        this.setScene(scene);
        CompletableFuture.supplyAsync(() -> getFileSize(file), executor).thenAccept(result -> Platform.runLater(() -> valueLabels.get(3).setText(result)));
        CompletableFuture.supplyAsync(() -> Utils.convertBytesByUnit(file.getFreeSpace()), executor).thenAccept(result -> Platform.runLater(() -> valueLabels.get(file.isFile() ? 7 : 8).setText(result)));
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

    private List<String> getFilePropertiesValues(File file) {
        List<String> values = new ArrayList<>();
        values.add(file.getName());
        values.add(file.getPath());
        values.add(Translator.translate("window.properties.calculating"));
        values.add(file.getParent());
        values.add(getCreationTime(file));
        values.add(getModifiedTime(file));
        values.add(Translator.translate("window.properties.calculated"));
        return values;
    }

    private String getFileSize(File file) {
        if (file.isDirectory()) {
            long directorySize = FilesOperations.calculateDirectorySize(file);
            return Utils.convertBytesByUnit(directorySize) + " (" + directorySize + Translator.translate("units.bytes") + ")";
        } else {
            return Utils.convertBytesByUnit(file.length()) + " (" + file.length() + Translator.translate("units.bytes") + ")";
        }
    }

    private String getCreationTime(File file) {
        try {
            FileTime creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();
            return OffsetDateTime.parse(creationTime.toString()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getModifiedTime(File file) {
        LocalDateTime modifiedDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(file.lastModified()),
                ZoneId.systemDefault()
        );
        return modifiedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
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
        return switch (FilesOperations.getExtension(file).toLowerCase()) {
            case "jpg", "jpeg", "png", "gif" -> useThumbnails && Boolean.TRUE.equals(SettingsManager.getSetting("files", "display-thumbnails")) ? new Image(new File(FilesOperations.getCurrentDirectory(), file.getName()).toURI().toString()) : loadIcon("/assets/icons/image.png");
            case "mp4" -> loadIcon("/assets/icons/video.png");
            case "mp3", "ogg" -> loadIcon("/assets/icons/music.png");
            case "iso" -> loadIcon("/assets/icons/cd.png");
            default -> loadIcon("/assets/icons/file.png");
        };
    }

    private Button getNavigateButton(String key, boolean leftIcon) {
        Button button = WindowComponents.getBackButton(key, leftIcon);
        button.setFont(Font.font(14 * 0.95));
        return button;
    }

    private void loadChecksum() {
        VBox root = new VBox();
        Button backToProperties = getNavigateButton("window.properties", false);
        backToProperties.setOnAction(_ -> loadProperties());
        VBox buttonWrapper = new VBox(backToProperties);
        buttonWrapper.setAlignment(Pos.TOP_RIGHT);

        Accordion checksumAccordion = new Accordion(getVerifyChecksum(), getGenerateChecksum());
        checksumAccordion.setExpandedPane(checksumAccordion.getPanes().get(1));
        VBox.setVgrow(checksumAccordion, Priority.ALWAYS);

        root.getChildren().addAll(buttonWrapper, checksumAccordion);
        this.setScene(new Scene(root, 330, height));
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
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button verifyChecksum = WindowComponents.getButton(Translator.translate("window.properties.checksum.verify-checksum"));
        verifyChecksum.setOnAction(e -> verifyChecksum(infoLabel, comboBox.getSelectionModel().getSelectedItem(), textArea.getText()));
        HBox buttonPanel = new HBox(verifyChecksum);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, enterChecksum, textArea, infoLabel, spacer, buttonPanel);
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
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button copy = WindowComponents.getButton(Translator.translate("button.copy"));
        copy.setOnAction(e -> Utils.copyTextToClipboard(this.hash));
        Button generate = WindowComponents.getButton(Translator.translate("window.properties.checksum.generate-hash"));
        generate.setOnAction(e -> generateHash(hash, comboBox.getSelectionModel().getSelectedItem()));
        HBox buttonPanel = new HBox(copy, generate);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setSpacing(10);

        contentPane.getChildren().addAll(choseAlgorithmLabel, comboBox, checksumLabel, hash, spacer, buttonPanel);
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
        VBox root = new VBox();
        Button backToProperties = getNavigateButton("window.properties", true);
        backToProperties.setOnAction(e -> loadProperties());
        VBox buttonWrapper = new VBox(backToProperties);
        buttonWrapper.setAlignment(Pos.TOP_LEFT);

        loadPermissionsGrid();
        HBox container = new HBox(permissions);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(45, 0, 0, 0));

        CheckBox applyToSubdirectories = new CheckBox(Translator.translate("window.properties.permissions.apply-to-subdirectories"));
        applyToSubdirectories.setPadding(new Insets(25, 0, 0, 30));
        applyToSubdirectories.setDisable(file.isFile());

        Button updatePermissions = WindowComponents.getConfirmButton(Translator.translate("window.properties.permissions.update"));
        updatePermissions.setOnAction(e -> applyPermissions(applyToSubdirectories.isSelected()));
        HBox.setMargin(updatePermissions, new Insets(25));
        HBox bottomButtonWrapper = new HBox(WindowComponents.getSpacer(), updatePermissions);
        VBox content = new VBox(container, applyToSubdirectories, bottomButtonWrapper);
        content.setAlignment(Pos.TOP_LEFT);
        content.setMinWidth(330);
        content.setMaxWidth(330);

        root.getChildren().addAll(buttonWrapper, content);
        root.setAlignment(Pos.TOP_CENTER);
        this.setScene(new Scene(root, 330, height));
        this.setTitle(Translator.translate("window.properties.permissions"));
    }

    private void loadPermissionsGrid() {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file.toPath());
            permissions = new GridPane();

            Label read = getLabel(Translator.translate("window.properties.permissions.read")),
                    write = getLabel(Translator.translate("window.properties.permissions.write")),
                    execute = getLabel(Translator.translate("window.properties.permissions.execute")),
                    owner = getLabel(Translator.translate("window.properties.permissions.owner")),
                    group = getLabel(Translator.translate("window.properties.permissions.group")),
                    others = getLabel(Translator.translate("window.properties.permissions.other"));

            CheckBox ownerRead = getCheckBox(perms.contains(PosixFilePermission.OWNER_READ)),
                    ownerWrite = getCheckBox(perms.contains(PosixFilePermission.OWNER_WRITE)),
                    ownerExecute = getCheckBox(perms.contains(PosixFilePermission.OWNER_EXECUTE)),
                    groupRead = getCheckBox(perms.contains(PosixFilePermission.GROUP_READ)),
                    groupWrite = getCheckBox(perms.contains(PosixFilePermission.GROUP_WRITE)),
                    groupExecute = getCheckBox(perms.contains(PosixFilePermission.GROUP_EXECUTE)),
                    othersRead = getCheckBox(perms.contains(PosixFilePermission.OTHERS_READ)),
                    othersWrite = getCheckBox(perms.contains(PosixFilePermission.OTHERS_WRITE)),
                    othersExecute = getCheckBox(perms.contains(PosixFilePermission.OTHERS_EXECUTE));

            permissions.add(new Label(), 0, 0);
            permissions.add(read, 1, 0);
            permissions.add(write, 2, 0);
            permissions.add(execute, 3, 0);

            Line lineHeader = new Line(0, 0, 280, 0);
            permissions.add(lineHeader, 0, 1);
            GridPane.setColumnSpan(lineHeader, 4);

            permissions.add(owner, 0, 2);
            permissions.add(ownerRead, 1, 2);
            permissions.add(ownerWrite, 2, 2);
            permissions.add(ownerExecute, 3, 2);

            Line line1 = new Line(0, 0, 280, 0);
            permissions.add(line1, 0, 3);
            GridPane.setColumnSpan(line1, 4);

            permissions.add(group, 0, 4);
            permissions.add(groupRead, 1, 4);
            permissions.add(groupWrite, 2, 4);
            permissions.add(groupExecute, 3, 4);

            Line line2 = new Line(0, 0, 280, 0);
            permissions.add(line2, 0, 5);
            GridPane.setColumnSpan(line2, 4);

            permissions.add(others, 0, 6);
            permissions.add(othersRead, 1, 6);
            permissions.add(othersWrite, 2, 6);
            permissions.add(othersExecute, 3, 6);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private boolean[][] readPermissions() {
        boolean[][] perms = new boolean[3][3];
        for (Node node : permissions.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                if (col != null && row != null) {
                    int mappedRow = (row - 2) / 2;
                    int mappedCol = col - 1;
                    if (mappedRow >= 0 && mappedRow < 3 && mappedCol >= 0 && mappedCol < 3) {
                        perms[mappedRow][mappedCol] = checkBox.isSelected();
                    }
                }
            }
        }
        return perms;
    }

    private void applyPermissions(boolean applyToSubdirectories) {
        try {
            boolean[][] permissions = readPermissions();
            Set<PosixFilePermission> permissionsSet = new HashSet<>();

            if (permissions[0][0]) permissionsSet.add(PosixFilePermission.OWNER_READ);
            if (permissions[0][1]) permissionsSet.add(PosixFilePermission.OWNER_WRITE);
            if (permissions[0][2]) permissionsSet.add(PosixFilePermission.OWNER_EXECUTE);
            if (permissions[1][0]) permissionsSet.add(PosixFilePermission.GROUP_READ);
            if (permissions[1][1]) permissionsSet.add(PosixFilePermission.GROUP_WRITE);
            if (permissions[1][2]) permissionsSet.add(PosixFilePermission.GROUP_EXECUTE);
            if (permissions[2][0]) permissionsSet.add(PosixFilePermission.OTHERS_READ);
            if (permissions[2][1]) permissionsSet.add(PosixFilePermission.OTHERS_WRITE);
            if (permissions[2][2]) permissionsSet.add(PosixFilePermission.OTHERS_EXECUTE);

            Files.setPosixFilePermissions(file.toPath(), permissionsSet);

            if (applyToSubdirectories && file.isDirectory()) {
                updatePermissionsRecursively(file, permissionsSet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePermissionsRecursively(File dir, Set<PosixFilePermission> permissionsSet) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File child : files) {
                try {
                    Files.setPosixFilePermissions(child.toPath(), permissionsSet);
                    if (child.isDirectory()) {
                        updatePermissionsRecursively(child, permissionsSet);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void close() {
        super.close();
        executor.shutdown();
    }
}
