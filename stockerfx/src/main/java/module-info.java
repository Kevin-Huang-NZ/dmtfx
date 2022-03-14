module com.mahara.stocker {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.jfoenix;
//    requires net.synedra.validatorfx;

    requires spring.context;
    requires spring.beans;
    requires spring.jdbc;
    requires spring.tx;

    requires java.sql;
    requires java.prefs;

    requires druid;
//    requires easyexcel;

    requires ahocorasick;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires org.slf4j;

    opens com.mahara.stocker to javafx.fxml;
    // opens to unnamed module, allow spring IOC: @Autowired
    opens com.mahara.stocker.model;
    opens com.mahara.stocker.dao.jt;
    opens com.mahara.stocker.service.impl;
    opens com.mahara.stocker.controller;
    opens com.mahara.stocker.controller.sys;
    opens com.mahara.stocker.controller.user;
    opens com.mahara.stocker.controller.standard;
    opens com.mahara.stocker.controller.project;
    opens com.mahara.stocker.util;
    opens com.mahara.stocker.util.excel;
    opens com.mahara.stocker.util.validate;
    opens com.mahara.stocker.dic;

    // exports to unnamed module, allow spring beans maintains @Component @Controller @Service @Repository
    exports com.mahara.stocker;
    exports com.mahara.stocker.dao;
    exports com.mahara.stocker.service;
    exports com.mahara.stocker.controller;
    exports com.mahara.stocker.controller.sys;
    exports com.mahara.stocker.controller.user;
    exports com.mahara.stocker.controller.standard;
    exports com.mahara.stocker.controller.project;
    exports com.mahara.stocker.util;
    exports com.mahara.stocker.util.excel;
    exports com.mahara.stocker.util.validate;
    opens com.mahara.stocker.util.task;
}