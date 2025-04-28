package com.example.aloe.utils;

import com.example.aloe.settings.Settings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * The {@code Translator} class is responsible for loading and managing language translations
 * from JSON files based on the user's language setting.
 * <p>
 * Translations are expected to be located in the {@code /assets/lang/} directory
 * with filenames corresponding to the language code (e.g., {@code en.json}, {@code pl.json}).
 * <p>
 * Example usage:
 * <pre>
 *     String translatedText = Translator.translate("window.example.item");
 * </pre>
 * If the key does not exist, the key itself will be returned as a fallback.
 *
 * @since 1.7.2
 */
public class Translator {

    /** The root JSON node containing the translations. */
    private static JsonNode jsonNode;

    static {
        init();
    }

    /**
     * Reloads the translations by reinitializing the language file.
     * Useful if the language settings are changed during runtime.
     */
    public static void reload() {
        init();
    }

    /**
     * Initializes the translation system by loading the language file
     * based on the current setting or falling back to English.
     */
    private static void init() {
        loadLanguageFile(Settings.getSetting("language", "lang"));
    }

    /**
     * Retrieves the root JSON node containing the translation mappings.
     *
     * @return the root {@code JsonNode} object
     */
    private static JsonNode getJsonContent() {
        return jsonNode;
    }

    /**
     * Loads the language JSON file into memory based on the specified language code.
     * If the specified language file cannot be found, it defaults to English ({@code en.json}).
     *
     * @param lang the language code (e.g., {@code "en"}, {@code "pl"})
     * @throws RuntimeException if an error occurs while reading the language file
     */
    private static void loadLanguageFile(String lang) {
        try {
            InputStream inputStream = Translator.class.getResourceAsStream("/assets/lang/" + lang + ".json");
            if (inputStream == null) {
                inputStream = Translator.class.getResourceAsStream("/assets/lang/en.json");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Translates the given key into the appropriate language-specific text.
     * <p>
     * Example:
     * <pre>
     *     Translator.translate("window.example.item");
     * </pre>
     * If the key is not found in the loaded language file, the key itself is returned.
     *
     * @param key the translation key (e.g., {@code "window.example.item"})
     * @return the translated text or the original key if not found
     */
    public static String translate(String key) {
        JsonNode node = Translator.getJsonContent().get(key);
        return (node != null) ? node.asText() : key;
    }
}
