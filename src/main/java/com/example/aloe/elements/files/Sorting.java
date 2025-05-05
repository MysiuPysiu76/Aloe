package com.example.aloe.elements.files;

public enum Sorting {
    NAMEASC, NAMEDESC, DATEASC, DATEDESC, SIZEASC, SIZEDESC;

    public static Sorting safeValueOf(String name) {
        try {
            return Sorting.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Sorting.NAMEASC;
        }
    }
}
