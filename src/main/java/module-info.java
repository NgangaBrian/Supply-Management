module root.supplymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens root.supplymanagement to javafx.fxml;
    exports root.supplymanagement;
}