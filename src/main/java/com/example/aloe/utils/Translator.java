package com.example.aloe.utils;

import com.example.aloe.settings.SettingsManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class Translator {

    private static JsonNode jsonNode;

    static {
        init();
    }

    public static void reload() {
        init();
    }

    private static void init() {
        loadLanguageFile(SettingsManager.getSetting("language", "lang"));
    }

    private static JsonNode getJsonContent() {
        return jsonNode;
    }

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

    public static String translate(String key) {
        JsonNode node = Translator.getJsonContent().get(key);
        return (node != null) ? node.asText() : key;
    }
}
