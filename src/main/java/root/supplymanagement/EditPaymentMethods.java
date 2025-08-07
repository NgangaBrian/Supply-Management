package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditPaymentMethods {

    @FXML
    private TextField paymentName;
    @FXML
    private Button addBtn;
    @FXML
    private VBox itemsVbox;

    public void initialize() {
        loadMethodsToView();
    }

    public void addPaymentMethod(){
        String paymentMethod = paymentName.getText();
        if(paymentMethod.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a payment method.");
            return;
        }
        String querry = "INSERT INTO paymentmethods (name) VALUES(?)";

        DBConnection con = new DBConnection();


        try (Connection connection = con.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querry)){

            preparedStatement.setString(1, paymentMethod);
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected>0){
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment method added successfully.");
                loadMethodsToView();
                paymentName.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment method.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not add payment method.\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void loadMethodsToView() {
        // Example: simulate getting data from database
        List<String> paymentMethods = getPaymentMethodsFromDatabase();
        itemsVbox.getChildren().clear();
        for (String method : paymentMethods) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item.fxml"));
                Parent itemNode = loader.load();

                // Get controller of productItem.fxml and set the product name
                ItemController controller = loader.getController();
                controller.setItem(method);

                // Add the item to the VBox
                itemsVbox.getChildren().add(itemNode);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payment method view.");
            }
        }
    }

    private List<String> getPaymentMethodsFromDatabase() {
        List<String> methods = new ArrayList<>();

        String query = "SELECT name FROM paymentmethods";
        DBConnection dbConnection = new DBConnection();


        try (Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet= statement.executeQuery()){

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                methods.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve payment methods.");
        }

        return methods;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
