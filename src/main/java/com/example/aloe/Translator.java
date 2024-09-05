package com.example.aloe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Locale;

public class Translator {

    private static JsonNode jsonNode;

    public Translator() {
        loadLanguageFile(Locale.getDefault().getLanguage());
    }

    private static JsonNode getJsonContent() {
        if (jsonNode == null) {
            new Translator();
        }
        return jsonNode;
    }

    private void loadLanguageFile(String lang) {
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

    public static String translate(String text) {
        return Translator.getJsonContent().get(text).asText();
    }
}
