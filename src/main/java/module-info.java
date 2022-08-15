module com.licennse.manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires org.controlsfx.controls;
    requires httpmime;
    requires httpcore;
    requires httpclient;
    requires java.desktop;


    opens com.license.manager to javafx.fxml;
    exports com.license.manager;

    opens com.license.manager.Controller to javafx.fxml;
    exports com.license.manager.Controller;
    exports com.license.manager.Controller.Auth;
    opens com.license.manager.Controller.Auth to javafx.fxml;
    exports com.license.manager.Model;
    opens com.license.manager.Model to javafx.fxml;
    exports com.license.manager.LocalDb;
    opens com.license.manager.LocalDb to javafx.fxml;
    exports com.license.manager.Controller.Update;
    opens com.license.manager.Controller.Update to javafx.fxml;
    exports com.license.manager.Method;
    opens com.license.manager.Method to javafx.fxml;
}