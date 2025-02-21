package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewSuppliersController implements Initializable {

    @FXML
    private VBox supplierItems;
    @FXML
    private ImageView maximizeBtnImage, minimizeBtnImage, closeBtnImage, addBtnImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        loadSupplierData();

    }

    private void loadImages() {
        Image addSupplierImage = new Image(getClass().getResource("/Images/add-user.png").toExternalForm());
        addBtnImage.setImage(addSupplierImage);

        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
    }

    public void loadSupplierData() {
        supplierItems.getChildren().clear(); // Clear existing items

        DBConnection connect = new DBConnection();
        Connection connection = connect.getConnection();

        String query = "SELECT name, phonenumber, address, email FROM suppliers";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phonenumber");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("suppliersItem.fxml"));
                Node node = loader.load();

                // Get controller and set supplier data
                SuppliersItemController controller = loader.getController();
                controller.setSupplierData(name, phone, address, email);
                controller.setViewSuppliersController(this);

                supplierItems.getChildren().add(node);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleCloseBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) closeBtnImage.getScene().getWindow();
        stage.close();
    }

    public void handleMinimizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                ((Stage) window).setIconified(true); // Minimize each stage
            }
        }
    }

    public void handleMaximizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) maximizeBtnImage.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
}
