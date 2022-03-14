module com.mahara.fxgenerator {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.jfoenix;

    requires spring.jdbc;

    requires java.sql;
    requires java.prefs;
    requires java.naming;
    requires java.management;

    requires druid;

    requires freemarker;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires com.google.common;

    opens com.mahara.fxgenerator.controller to javafx.fxml;
    exports com.mahara.fxgenerator;
}