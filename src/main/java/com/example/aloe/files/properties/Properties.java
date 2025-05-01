package com.example.aloe.files.properties;

import java.util.List;
import java.util.Map;

public interface Properties {

    public Map<String, String> getProperties();

    public List<String> getPropertiesNames();

    public List<String> getPropertiesValues();
}
