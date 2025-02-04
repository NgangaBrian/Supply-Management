package root.supplymanagement;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;

public class AddSuppliers {

    @FXML
    private Label supplierLabel, phoneNoLabel, emailLabel, addressLabel;
    @FXML
    private TextField supplierNameTF, phoneNoTF, emailTF, addressTF;
    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;
    @FXML
    private Button saveBtn, cancelBtn;

    public void initialize(){
        loadImages();
        setupFloatingLabels(supplierNameTF, supplierLabel);
        setupFloatingLabels(phoneNoTF, phoneNoLabel);
        setupFloatingLabels(emailTF, emailLabel);
        setupFloatingLabels(addressTF, addressLabel);
    }

    private void setupFloatingLabels(TextField textField, Label label){
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(200), label);
        moveUp.setToY(-20);

        TranslateTransition moveDown = new TranslateTransition(Duration.millis(200), label);
        moveDown.setToY(1);

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                moveUp.play();
                label.getStyleClass().add("floating-label-focused");
            } else if (supplierNameTF.getText().isEmpty()) {
                moveDown.play();
                label.getStyleClass().remove("floating-label-focused");
            }
        });
    }

    public void saveDetails(){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Details");
            alert.setHeaderText("Details");
            alert.setContentText("Details have been saved successfully!");

            alert.showAndWait(); // Shows the alert and waits for the user to close it
    }

    public void closePage(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
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

    public void loadImages(){
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
}
