package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class SuppliersItemController {

    @FXML
    private Label supplierName, supplierPhone, supplierAddress, supplierEmail;
    @FXML
    private ImageView editSupplier, deleteSupplier;
    @FXML
    private HBox supplierItemView;

    private ViewSuppliersController viewSuppliersController;

    // Method to set the reference
    public void setViewSuppliersController(ViewSuppliersController controller) {
        this.viewSuppliersController = controller;
    }

    public void initialize(){
        deleteSupplier.setOnMouseClicked(this::handleDeleteSupplier);
        editSupplier.setOnMouseClicked(this::handleEditSupplier);

        supplierItemView.setOnMouseClicked(event -> {handleItemClick();});
    }

    public void setSupplierData(String name, String phone, String address, String email) {
        supplierName.setText(name);
        supplierPhone.setText(phone);
        supplierAddress.setText(address);
        supplierEmail.setText(email);

        Image editImage = new Image(getClass().getResource("/Images/editBtn.png").toExternalForm());
        editSupplier.setImage(editImage);

        Image deleteImage = new Image(getClass().getResource("/Images/deleteBtn.png").toExternalForm());
        deleteSupplier.setImage(deleteImage);

    }

    @FXML
    private void handleItemClick() {
        System.out.println("HBox clicked! Opening view-ordered-items.fxml...");
        try {
            String suppliername = supplierName.getText();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("view-supplier-data.fxml"));
            Parent root = loader.load();

            // Get controller of the new FXML file
            ViewSupplierDataController controller = loader.getController();

            // Pass data to the new controller
            controller.loadOrdersData(supplierName.getText());
            controller.loadOrderNumbersComboBox(supplierName.getText());
            controller.loadPaymentsDataForSupplier(supplierName.getText());

            // Show new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 876, 601));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteSupplier(MouseEvent event) {
        String email = supplierEmail.getText();
        String name = supplierName.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Supplier");
        alert.setHeaderText("Are you sure you want to delete supplier: " + name + " (" + email + ")?");
        alert.setContentText("This action cannot be undone.");

        // Wait for user response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteSupplierFromDatabase(email)) {
                if (supplierItemView.getParent() != null) {
                    ((Pane) supplierItemView.getParent()).getChildren().remove(supplierItemView);
                }
            }
        } else {
            // Show error alert
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Deletion Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to delete the supplier from the database.");
            alert.showAndWait();
        }
    }

    private void handleEditSupplier(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-suppliers.fxml"));
        try {
            Parent root = loader.load();
            AddSuppliers controller = loader.getController();


            // Pass the ViewSuppliersController reference to AddSuppliers
            controller.setViewSuppliersController(viewSuppliersController);

            // Set the form to edit mode with current values
            controller.setIsEditMode(supplierName.getText(), supplierPhone.getText(), supplierEmail.getText(), supplierAddress.getText());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 450));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteSupplierFromDatabase(String email) {
        String sql = "DELETE FROM suppliers WHERE email = ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
