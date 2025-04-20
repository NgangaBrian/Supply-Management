module root.supplymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires com.google.api.services.storage;
    requires google.cloud.firestore;
    requires google.cloud.storage;
    requires jbcrypt;


    opens root.supplymanagement to javafx.fxml;
    exports root.supplymanagement;
}