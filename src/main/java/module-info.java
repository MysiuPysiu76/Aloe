module com.example.aloe {
    requires javafx.controls;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
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
    requires com.github.kokorin.jaffree;
    requires com.github.oshi;

    opens com.example.aloe to javafx.fxml;
    exports com.example.aloe;
    exports com.example.aloe.files;
    opens com.example.aloe.files to javafx.fxml;
    exports com.example.aloe.utils;
    opens com.example.aloe.utils to javafx.fxml;
    exports com.example.aloe.utils.ffmpeg;
    opens com.example.aloe.utils.ffmpeg to javafx.fxml;
}