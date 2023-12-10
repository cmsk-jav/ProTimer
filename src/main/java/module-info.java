module com.sk.protimer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.groovy;
    requires java.desktop;
    requires java.prefs;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    requires  com.fasterxml.jackson.annotation;
    requires jnativehook;

    opens com.sk.protimer.listener;
    opens com.sk.protimer.controller;
    opens com.sk.protimer;
    opens res.image;
    opens res.css;
    opens res.font;
    opens res.icons;
    opens res.layouts;
    exports com.sk.protimer to javafx.fxml, javafx.controls, javafx.graphics, java.desktop;
    exports com.sk.protimer.controller to javafx.fxml, javafx.controls, javafx.graphics;
}