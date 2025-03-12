package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Label invalidMessageLabel;
    @FXML
    private Button loginButton, cancelButton;
    @FXML
    private ImageView brandingImageView, lockImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        Image brandingImage = new Image(getClass().getResource("/Images/kimsalogo.png").toExternalForm());
        brandingImageView.setImage(brandingImage);

        File lockFile = new File("Images/lock.png");
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lockImage);

        usernameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordTextField.requestFocus();
            }
        });

        passwordTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
                    invalidMessageLabel.setText("Fields are empty");
                    invalidMessageLabel.setVisible(true);
                } else {
                    validateLogin(new ActionEvent(passwordTextField, null));
                }
            }
        });

    }
    @FXML
    public void loginButton(ActionEvent actionEvent) {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            invalidMessageLabel.setText("Fields are empty");
            invalidMessageLabel.setVisible(true);
        } else {
            validateLogin(actionEvent);
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin(ActionEvent event) {

        DBConnection connect = new DBConnection();
        Connection connection1 = connect.getConnection();

        String verifyLogin = "SELECT count(1) from users where username = ? AND password = ?";

        try {
            PreparedStatement preparedStatement = connection1.prepareStatement(verifyLogin);
            preparedStatement.setString(1, usernameTextField.getText());
            preparedStatement.setString(2, passwordTextField.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1){
                    invalidMessageLabel.setText("Logged in successfully");
                    invalidMessageLabel.setVisible(true);
                    invalidMessageLabel.setStyle("-fx-text-fill: green");

                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
                        Stage stage = new Stage();
                        Scene scene = new Scene(root, 780, 550);
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.show();

                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                    }
                } else {
                    invalidMessageLabel.setText("Login failed, try again");
                    invalidMessageLabel.setVisible(true);
                    invalidMessageLabel.setStyle("-fx-text-fill: red");
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }
}