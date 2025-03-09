package com.example.aloe;

class MultiFileBoxContextMenu extends ExtendedContextMenu {
    public MultiFileBoxContextMenu() {
        super();

        ExtendedMenuItem copy = new ExtendedMenuItem(Translator.translate("context-menu.copy"), e -> new Main().copySelectedFiles());
        ExtendedMenuItem cut = new ExtendedMenuItem(Translator.translate("context-menu.cut"), e -> new Main().cutSelectedFiles());
        ExtendedMenuItem duplicate = new ExtendedMenuItem(Translator.translate("context-menu.duplicate"), e -> FilesOperations.duplicateFiles(new Main().getSelectedFiles()));
        ExtendedMenuItem moveTo = new ExtendedMenuItem(Translator.translate("context-menu.move-to"), e -> FilesOperations.moveFileTo(new Main().getSelectedFiles()));
        ExtendedMenuItem moveToParent = new ExtendedMenuItem(Translator.translate("context-menu.move-to-parent"), e -> FilesOperations.moveFileToParent(new Main().getSelectedFiles()));
        ExtendedMenuItem moveToTrash = new ExtendedMenuItem(Translator.translate("context-menu.move-to-trash"), e -> FilesOperations.moveFileToTrash(new Main().getSelectedFiles()));
        ExtendedMenuItem compress = new ExtendedMenuItem(Translator.translate("context-menu.compress"), e -> new Main().openCreateArchiveWindow(new Main().getSelectedFiles()));
        ExtendedMenuItem delete = new ExtendedMenuItem(Translator.translate("context-menu.delete"), e -> new Main().deleteSelectedFiles());

        this.getItems().addAll(copy, cut, duplicate, moveTo, moveToParent, moveToTrash, compress, delete);
    }
}