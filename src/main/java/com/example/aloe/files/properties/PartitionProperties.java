package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
import oshi.software.os.OSFileStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record PartitionProperties(OSFileStore store) implements Properties {

    public String getName() {
        return store.getName();
    }

    public String getMountPoint() {
        return store.getMount();
    }

    public String getVolume() {
        return store.getVolume();
    }

    public String getType() {
        return store.getType();
    }

    public String getOptions() {
        return store.getOptions();
    }

    public String getUUID() {
        return store.getUUID();
    }

    public String getDescription() {
        return store.getDescription();
    }

    public String getFreeSpace() {
        return UnitConverter.convert(store.getFreeSpace());
    }

    public String getLogicalVolume() {
        return store.getLogicalVolume();
    }

    public String getTotalSpace() {
        return UnitConverter.convert(store.getTotalSpace());
    }

    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.partition.name"));
        names.add(Translator.translate("window.properties.partition.mount"));
        names.add(Translator.translate("window.properties.partition.type"));
        names.add(Translator.translate("window.properties.partition.free-space"));
        names.add(Translator.translate("window.properties.partition.total-space"));
        names.add(Translator.translate("window.properties.partition.options"));
        names.add(Translator.translate("window.properties.partition.uuid"));
        return names;
    }

    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getVolume());
        values.add(getMountPoint());
        values.add(getType());
        values.add(getFreeSpace());
        values.add(getTotalSpace());
        values.add(getOptions());
        values.add(getUUID());
        return values;
    }

    @Override
    public Map<String, String> getProperties() {
        List<String> names = getPropertiesNames();
        List<String> values = getPropertiesValues();

        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(names.size(), values.size()); i++) {
            map.put(names.get(i), values.get(i));
        }
        return map;
    }
}