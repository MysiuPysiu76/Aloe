module com.example.aloe {
    requires javafx.controls;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires org.apache.tika.core;
    requires javafx.swing;
    requires zip4j;
    requires org.apache.commons.compress;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires junrar;
    requires org.apache.commons.io;
    requires metadata.extractor;

    opens com.example.aloe to javafx.fxml;
    exports com.example.aloe;
    exports com.example.aloe.archive;
    exports com.example.aloe.components;
    opens com.example.aloe.components to javafx.fxml;
    exports com.example.aloe.menu;
    opens com.example.aloe.menu to javafx.fxml;
}