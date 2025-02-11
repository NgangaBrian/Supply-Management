module root.supplymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;


    opens root.supplymanagement to javafx.fxml;
    exports root.supplymanagement;
}