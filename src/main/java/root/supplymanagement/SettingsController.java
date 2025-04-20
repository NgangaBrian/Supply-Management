package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SettingsController {
    @FXML
    private BorderPane borderPane;
    @FXML
    private ImageView maximizeBtnImage, closeBtnImage, minimizeBtnImage;
    @FXML
    private Label nameLB;

    private User loggedInUser;

    public void initialize() {
        loadImages();
        editProducts(null);
    }

    public void setUserData(User user) {
        this.loggedInUser = user;
        nameLB.setText(user.getFirstname() + " " + user.getLastname());
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

    // Method to load different views dynamically
    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newView = loader.load();
            borderPane.setCenter(newView); // Replace the center content with the new view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPasswordView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newView = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setUserData(loggedInUser);
            controller.showUser();

            borderPane.setCenter(newView); // Replace the center content with the new view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editProducts(javafx.scene.input.MouseEvent mouseEvent) {
        loadView("editProducts.fxml");
    }

    public void editPaymentMethods(javafx.scene.input.MouseEvent mouseEvent) {
        loadView("paymentMethods.fxml");
    }

    public void changePassword(javafx.scene.input.MouseEvent mouseEvent){
        loadPasswordView("change-password.fxml");
    }

    public void generateReport(javafx.scene.input.MouseEvent mouseEvent) {
        loadView("generate-report.fxml");
    }

    private void loadImages() {
        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
    }
}
