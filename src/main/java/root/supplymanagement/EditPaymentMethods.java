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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a payment method");
            alert.showAndWait();
            return;
        }
        String querry = "INSERT INTO paymentmethods (name) VALUES(?)";

        DBConnection con = new DBConnection();
        Connection connection = con.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querry);
            preparedStatement.setString(1, paymentMethod);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected>0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Payment method added successfully");
                alert.showAndWait();
                loadMethodsToView();
                paymentName.clear();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadMethodsToView() {
        // Example: simulate getting data from database
        List<Product> products = getProductsFromDatabase();
        itemsVbox.getChildren().clear();
        for (Product product : products) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item.fxml"));
                Parent itemNode = loader.load();

                // Get controller of productItem.fxml and set the product name
                ItemController controller = loader.getController();
                controller.setItem(product.getName());

                // Add the item to the VBox
                itemsVbox.getChildren().add(itemNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Product> getProductsFromDatabase() {
        List<Product> products = new ArrayList<>();

        String query = "SELECT name FROM paymentmethods";
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                products.add(new Product(name));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed to load products from the database.");
            alert.showAndWait();
        }

        return products;
    }

}
