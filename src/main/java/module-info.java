module com.example.aloe {
    requires javafx.controls;
    requires javafx.fxml;
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

    opens com.example.aloe to javafx.fxml;
    exports com.example.aloe;
}