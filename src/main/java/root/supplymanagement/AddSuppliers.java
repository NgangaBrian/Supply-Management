package root.supplymanagement;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.sql.*;

public class AddSuppliers {

    @FXML
    private Label supplierLabel, phoneNoLabel, emailLabel, addressLabel, titleLabel;
    @FXML
    private TextField supplierNameTF, phoneNoTF, emailTF, addressTF;
    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;
    @FXML
    private Button saveBtn, cancelBtn;

    private Boolean isEditMode = false;
    private ViewSuppliersController viewSuppliersController;

    public void setViewSuppliersController(ViewSuppliersController viewSuppliersController) {
        this.viewSuppliersController = viewSuppliersController;
    }

    public void setIsEditMode(String name, String phone, String email, String address) {
        isEditMode = true;

        supplierNameTF.setText(name);
        phoneNoTF.setText(phone);
        emailTF.setText(email);
        addressTF.setText(address);

        saveBtn.setText("Update");
        titleLabel.setText("Edit Supplier");
    }

    public void initialize(){
        loadImages();
        setupFloatingLabels(supplierNameTF, supplierLabel);
        setupFloatingLabels(phoneNoTF, phoneNoLabel);
        setupFloatingLabels(emailTF, emailLabel);
        setupFloatingLabels(addressTF, addressLabel);

        supplierNameTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                phoneNoTF.requestFocus();
            }
        });

        phoneNoTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                emailTF.requestFocus();
            }
        });

        emailTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addressTF.requestFocus();
            }
        });

        addressTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveDetails();
            }
        });
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

        String supplierName = supplierNameTF.getText();
        String phoneNo = phoneNoTF.getText();
        String email = emailTF.getText();
        String address = addressTF.getText();

        if(supplierName.isEmpty() || phoneNo.isEmpty() || email.isEmpty() || address.isEmpty()){
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setContentText("All fields are required!");
            alert.showAndWait();
        }
        else{
            if(isEditMode){
                updateSupplier(supplierName, phoneNo, email, address);
            } else {
                addNewSupplier(supplierName, phoneNo, email, address);
            }


        }

    }
    private void addNewSupplier(String supplierName, String phoneNo, String email, String address) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Details");

        DBConnection connect = new DBConnection();
        Connection connection1 = connect.getConnection();

        String insertSupplier = "insert into suppliers(name, phonenumber, email, address) values(?,?,?,?)";

        try {
            PreparedStatement preparedStatement = connection1.prepareStatement(insertSupplier);
            preparedStatement.setString(1, supplierName);
            preparedStatement.setString(2, phoneNo);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);

            int rowsaffected = preparedStatement.executeUpdate();

            if(rowsaffected>0){
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Details have been saved successfully!");
                alert.showAndWait();
                supplierNameTF.clear();
                phoneNoTF.clear();
                emailTF.clear();
                addressTF.clear();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save details!");
                alert.showAndWait();
            }
            preparedStatement.close();
            connection1.close();
        } catch (SQLIntegrityConstraintViolationException e){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Supplier with the same phone number or email already exists!");
            alert.showAndWait();
        }catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Failed. Try again later.");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void updateSupplier(String supplierName, String phoneNo, String email, String address){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Supplier Details");

        DBConnection connect = new DBConnection();
        Connection connection = connect.getConnection();

        String updateSupplier = "update suppliers set name = ?, phonenumber = ?, address = ? where email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateSupplier);
            preparedStatement.setString(1, supplierName);
            preparedStatement.setString(2, phoneNo);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected>0){
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Supplier has been updated successfully!");
                alert.showAndWait();

                closePage();

                if (viewSuppliersController != null) {
                    viewSuppliersController.loadSupplierData();
                } else {
                    System.out.println("Warning: viewSuppliersController is null. Data will not refresh.");
                }
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Failed to update details!");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Failed! Try again later.");
            alert.showAndWait();
            e.getCause();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
    }
}
