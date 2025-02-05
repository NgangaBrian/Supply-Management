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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewSuppliers implements Initializable {

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
        File addSupplierFile = new File("Images/add-user.png");
        Image addSupplierImage = new Image(addSupplierFile.toURI().toString());
        addBtnImage.setImage(addSupplierImage);

        File exitFile = new File("Images/closeBtn.png");
        Image exitImage = new Image(exitFile.toURI().toString());
        closeBtnImage.setImage(exitImage);

        File minimizeFile = new File("Images/minimizeBtn.png");
        Image minimizeImage = new Image(minimizeFile.toURI().toString());
        minimizeBtnImage.setImage(minimizeImage);

        File maximizeFile = new File("Images/maximizeBtn.png");
        Image maximizeImage = new Image(maximizeFile.toURI().toString());
        maximizeBtnImage.setImage(maximizeImage);
    }

    private void loadSupplierData() {
        Node[] nodes = new Node[10];
        for (int i = 0; i < nodes.length; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("suppliersItem.fxml"));
                Node node = loader.load();

                // Get controller and set supplier data
                SuppliersItemController controller = loader.getController();
                controller.setSupplierData("Brian Mutwiri", "254790944432", "123 - 60103, Runyenjes", "ngangabrian724@gmail.com");

                supplierItems.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
