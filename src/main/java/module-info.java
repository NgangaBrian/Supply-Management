module root.supplymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;
    requires com.google.auth.oauth2;
    requires jbcrypt;
    requires dynamicreports.core;
    requires jasperreports;


    opens root.supplymanagement to javafx.fxml;
    exports root.supplymanagement;
}