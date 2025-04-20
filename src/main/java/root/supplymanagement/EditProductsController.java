package root.supplymanagement;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
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
            }
        }
    }

    private List<Product> getProductsFromDatabase() {
        List<Product> products = new ArrayList<>();

        System.out.println("PRODUCTS FROM DATABASE");

        String query = "SELECT name FROM products";
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



    public void addProduct(){
        String productName = nameTF.getText();
        // String imageUrl = uploadImage(productName);

        String querry = "insert into products (name) values (?)";
        DBConnection con = new DBConnection();
        Connection connection = con.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querry);
            preparedStatement.setString(1, productName);
            //preparedStatement.setString(2, imageUrl);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Product added successfully!");
                alert.showAndWait();
                loadProductsToView();
                nameTF.clear();
                imageNameLB.setText("");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Product could not be added!");
                alert.showAndWait();
            }
        }catch (SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed. Please try again!");
            alert.showAndWait();
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
// todo; Key points to stay happy
// One act of kindness
// Exercise
// Meditation
// Gratitude -3 things you are grateful for today
//