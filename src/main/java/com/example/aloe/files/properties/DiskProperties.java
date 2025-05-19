package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.example.aloe.utils.UnitConverter;
import org.jetbrains.annotations.NotNull;
import oshi.hardware.HWDiskStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record DiskProperties(@NotNull HWDiskStore disk) implements Properties {

    public String getName() {
        return disk.getName();
    }

    public String getModel() {
        return disk.getModel();
    }

    public String getSerial() {
        return disk.getSerial();
    }

    public String getSize() {
        return UnitConverter.convert(disk.getSize());
    }

    public String getReads() {
        return String.valueOf(disk.getReads());
    }

    public String getReadBytes() {
        return String.valueOf(disk.getReadBytes());
    }

    public String getWrites() {
        return String.valueOf(disk.getWrites());
    }

    public String getWriteBytes() {
        return String.valueOf(disk.getWriteBytes()) ;
    }

    public String getTransferTime() {
        return disk.getTransferTime() + " ms";
    }

    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.disk.name"));
        names.add(Translator.translate("window.properties.disk.model"));
        names.add(Translator.translate("window.properties.disk.serial"));
        names.add(Translator.translate("window.properties.disk.size"));
        names.add(Translator.translate("window.properties.disk.reads"));
        names.add(Translator.translate("window.properties.disk.writes"));
        names.add(Translator.translate("window.properties.disk.transfer"));
        return names;
    }

    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getName());
        values.add(getModel());
        values.add(getSerial());
        values.add(getSize());
        values.add(getReads());
        values.add(getWrites());
        values.add(getTransferTime());
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