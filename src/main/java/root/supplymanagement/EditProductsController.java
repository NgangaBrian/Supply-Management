package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProductsController {
    @FXML
    private TextField nameTF;
    @FXML
    private Button uploadImageBtn, saveBtn;
    @FXML
    private Label imageNameLB;

    private File selectedFile;

    public void addProduct(){
        String productName = nameTF.getText();

        String querry = "insert into products (name) values (?)";
        DBConnection con = new DBConnection();
        Connection connection = con.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querry);
            preparedStatement.setString(1, productName);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Product added successfully!");
                alert.showAndWait();
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

    public void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            imageNameLB.setText(selectedFile.getName());
        }
    }
}
