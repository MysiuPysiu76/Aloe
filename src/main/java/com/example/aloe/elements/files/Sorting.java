package com.example.aloe.elements.files;

/**
 * Defines the available sorting strategies for file elements within the application.
 * <p>
 * This enumeration is used to determine the order in which files are presented
 * based on different criteria such as name, date, or size, each in ascending or descending order.
 * </p>
 *
 * <p>
 * The available sorting options are:
 * <ul>
 *     <li>{@code NAMEASC} – Sort by file name in ascending (A-Z) order.</li>
 *     <li>{@code NAMEDESC} – Sort by file name in descending (Z-A) order.</li>
 *     <li>{@code DATEASC} – Sort by modification date from oldest to newest.</li>
 *     <li>{@code DATEDESC} – Sort by modification date from newest to oldest.</li>
 *     <li>{@code SIZEASC} – Sort by file size from smallest to largest.</li>
 *     <li>{@code SIZEDESC} – Sort by file size from largest to smallest.</li>
 * </ul>
 * </p>
 *
 * @since 2.5.5
 */
public enum Sorting {
    NAMEASC,
    NAMEDESC,
    DATEASC,
    DATEDESC,
    SIZEASC,
    SIZEDESC;

    /**
     * Safely converts a string to a corresponding {@code Sorting} enum constant.
     * <p>
     * If the provided name is {@code null}, empty, or does not match any constant,
     * the default value {@link #NAMEASC} is returned.
     * </p>
     *
     * @param name the name of the sorting strategy, case-sensitive
     * @return the matching {@code Sorting} constant, or {@code NAMEASC} if invalid or {@code null}
     */
    public static Sorting safeValueOf(String name) {
        try {
            return Sorting.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Sorting.NAMEASC;
        }
    }
}
