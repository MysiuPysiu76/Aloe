package com.example.aloe.settings;

import com.example.aloe.utils.Translator;
import com.example.aloe.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class SettingsManager {

    private static final String DEFAULT_SETTINGS_PATH = "/assets/settings.json";
    private static final String USER_SETTINGS_PATH = System.getProperty("user.dir") + "/settings.json";

    private static Map<String, Object> cachedSettings = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String category;

    static {
        loadSettings();
        objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
    }

    public static String getCategory() {
        return category;
    }

    public static void setCategory(String category) {
        SettingsManager.category = category;
    }

    public static void loadSettings() {
        File settingsFile = new File(USER_SETTINGS_PATH);
        if (!settingsFile.exists() || settingsFile.length() == 0L) {
            initializeSettings();
        }
        try {
            cachedSettings = objectMapper.readValue(settingsFile, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeSettings() {
        try {
            Map<String, Object> defaultSettings = loadDefaultSettings();
            saveSettingsToFile(defaultSettings);
            cachedSettings = defaultSettings;
            trySetLanguage();
            initializeItemsInMenu();
            setTrashLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeItemsInMenu() {
        SettingsManager.setSetting("menu", "items", Arrays.asList(
                Map.of("path", System.getProperty("user.home"), "name", Translator.translate("menu.home"), "icon", "HOME"),
                Map.of("path", System.getProperty("user.home") + "/Desktop", "name", Translator.translate("menu.desktop"), "icon", "DESKTOP"),
                Map.of("path", System.getProperty("user.home") + "/Documents", "name", Translator.translate("menu.documents"), "icon", "FILE_TEXT"),
                Map.of("path", System.getProperty("user.home") + "/Downloads", "name", Translator.translate("menu.downloads"), "icon", "ARROW_DOWN"),
                Map.of("path", System.getProperty("user.home") + "/Music", "name", Translator.translate("menu.music"), "icon", "MUSIC"),
                Map.of("path", System.getProperty("user.home") + "/Pictures", "name", Translator.translate("menu.pictures"), "icon", "PICTURE_O"),
                Map.of("path", System.getProperty("user.home") + "/Videos", "name", Translator.translate("menu.videos"), "icon", "VIDEO_CAMERA"),
                Map.of("path", "%trash%", "name", Translator.translate("menu.trash"), "icon", "TRASH")

        ));
    }

    private static void trySetLanguage() {
        String lang = Locale.getDefault().getLanguage();
        if (Utils.isFileExistsInResources("assets/lang/", lang + ".json")) {
            SettingsManager.setSetting("language", "lang", lang);
        }
        Translator.reload();
    }

    private static void setTrashLocation() {
        SettingsManager.setSetting("files", "trash", new File(System.getProperty("user.home"), ".trash"));
    }

    private static Map<String, Object> loadDefaultSettings() throws IOException {
        return objectMapper.readValue(SettingsManager.class.getResourceAsStream(DEFAULT_SETTINGS_PATH), new TypeReference<>() {});
    }

    private static void saveSettingsToFile(Map<String, Object> settings) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USER_SETTINGS_PATH), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getSetting(String category, String key) {
        try {
            Map<String, Object> categorySettings = (Map<String, Object>) cachedSettings.get(category);
            if (categorySettings != null && categorySettings.containsKey(key)) {
                return (T) categorySettings.get(key);
            } else {
                return addMissingValue(category, key);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T addMissingValue(String category, String key) {
        try {
            Map<String, Object> defaultSettings = loadDefaultSettings();
            Map<String, Object> defaultCategorySettings = (Map<String, Object>) defaultSettings.get(category);
            Map<String, Object> userCategorySettings = (Map<String, Object>) cachedSettings.getOrDefault(category, new HashMap<>());
            T value = (T) defaultCategorySettings.get(key);
            userCategorySettings.put(key, value);
            cachedSettings.put(category, userCategorySettings);
            saveSettingsToFile(cachedSettings);
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setSetting(String category, String key, Object value) {
        try {
            Map<String, Object> categorySettings = (Map<String, Object>) cachedSettings.getOrDefault(category, new HashMap<>());
            categorySettings.put(key, value);
            cachedSettings.put(category, categorySettings);
            saveSettingsToFile(cachedSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
