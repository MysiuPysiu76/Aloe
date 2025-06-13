package com.example.aloe.elements.files;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SelectedFileBoxes {

    private static final Set<FileBox> selectedFileBoxes = new LinkedHashSet<>();

    public static void add(FileBox fb) {
        selectedFileBoxes.add(fb);
    }

    public static void remove(FileBox fb) {
        selectedFileBoxes.remove(fb);
    }

    public static void removeSelection() {
        selectedFileBoxes.forEach(FileBox::removeSelectedStyle);
        selectedFileBoxes.clear();
    }

    public static boolean isSelected(FileBox fb) {
        return selectedFileBoxes.contains(fb);
    }

    public static List<File> getSelectedFiles() {
        return selectedFileBoxes.stream().map(FileBox::getFile).toList();
    }

    public static Set<FileBox> getSelectedFileBoxes() {
        return Set.copyOf(selectedFileBoxes);
    }
}
