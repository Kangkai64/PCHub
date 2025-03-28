module PCHub.PCHub {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens application to javafx.fxml;
    exports application;
    exports application.Controllers;
    opens application.Controllers to javafx.fxml;
    exports application.Utils;
    opens application.Utils to javafx.fxml;
}