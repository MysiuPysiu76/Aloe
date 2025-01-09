package com.example.aloe;

import java.util.Map;

public class Utils {
    public static int getKeyIndex(Map<String, ? extends Object> map, String key) {
        int index = 0;
        for (String k : map.keySet()) {
            if (k.equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }
}