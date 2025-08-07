package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProductsController {
    @FXML
    private TextField nameTF;
    @FXML
    private Button uploadImageBtn, saveBtn;
    @FXML
    private Label imageNameLB;
    @FXML
    private VBox itemsVbox;

    private File selectedFile;

    public void initialize() {
        loadProductsToView();
    }

    public void loadProductsToView() {
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
                showAlert(Alert.AlertType.ERROR, "FXML Error", "Failed to load product view.");
            }
        }
    }

    private List<Product> getProductsFromDatabase() {
        List<Product> products = new ArrayList<>();

        String query = "SELECT name FROM products";
        DBConnection dbConnection = new DBConnection();


        try (Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()){

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                products.add(new Product(name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load products from database.");
        }

        return products;
    }



    public void addProduct(){
        String productName = nameTF.getText();
        // String imageUrl = uploadImage(productName);

        if (productName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Product name cannot be empty.");
            return;
        }

        String querry = "insert into products (name) values (?)";
        DBConnection con = new DBConnection();

        try (Connection connection = con.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querry)){
            preparedStatement.setString(1, productName);
            //preparedStatement.setString(2, imageUrl);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
                nameTF.clear();
                imageNameLB.setText("");
                loadProductsToView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Product could not be added.");
            }
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add product.\n" + e.getMessage());
        }
    }

    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            imageNameLB.setText(selectedFile.getName());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

//    public String uploadImage(String productName) {
//        if(selectedFile == null){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText(null);
//            alert.setContentText("Please select an image file");
//            alert.showAndWait();
//            return null;
//        }
//
//        try {
//            HelloApplication.initializeFirebase(); // Make sure this only runs once
//            Bucket bucket = StorageClient.getInstance().bucket();
//
//            String fileName = productName + "-" + selectedFile.getName();
//            Blob blob = bucket.create("KIMSA Products/" + fileName,
//                    Files.readAllBytes(selectedFile.toPath()),
//                    "image/jpeg");
//
//            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName()
//                    + "/o/KIMSA Products%2F" + fileName + "?alt=media";
//
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText(null);
//            alert.setContentText("Image uploaded successfully!");
//            alert.showAndWait();
//
//            return imageUrl;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText(null);
//            alert.setContentText("Image upload failed.");
//            alert.showAndWait();
//            return null;
//        }
//
//    }

}
