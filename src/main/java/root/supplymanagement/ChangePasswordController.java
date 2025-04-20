package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML
    private PasswordField oldPassword, newPassword, confirmPassword;
    @FXML
    private TextField usernameTF;
    @FXML
    private Button saveButton;

    private User loggedInUser;

    public void initialize() {
        usernameTF.requestFocus();
    }

    private String getCurrentPasswordFromDB(String username) {
        String query = "SELECT password FROM users WHERE id = ?"; // Change logic to get user ID dynamically
        DBConnection connect = new DBConnection();
        try {
            Connection conn = connect.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            System.out.println("User ID: "+loggedInUser.getId());
            stmt.setInt(1, loggedInUser.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String pass = rs.getString("password");
                System.out.println("Password: "+pass);
                return pass;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve password.");
        }
        return null;
    }

    public void setUserData(User user) {
        this.loggedInUser = user;
    }

    public void showUser(){
        String name = loggedInUser.getFirstname();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Welcome "  + loggedInUser.getFirstname() + " " + loggedInUser.getLastname() + " " + loggedInUser.getId());
        alert.showAndWait();
    }

    private boolean updatePasswordInDatabase(String newPass) {
        String hashedPass = BCrypt.hashpw(newPass, BCrypt.gensalt());

        String query = "UPDATE users SET password = ? WHERE id = ?"; // Change logic to get user ID dynamically
        DBConnection connect = new DBConnection();
        try (Connection conn = connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedPass);
            stmt.setInt(2, loggedInUser.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update password.");
        }
        return false;
    }

    @FXML
    private void changePassword() {
        String username = usernameTF.getText();
        String oldPass = oldPassword.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();
        String currentPass = getCurrentPasswordFromDB(username);

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (currentPass == null || !BCrypt.checkpw(oldPass, currentPass)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Old password is incorrect.");
            return;
        }


        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirmation do not match.");
            return;
        }

        if (newPass.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password must be at least 6 characters long.");
            return;
        }

        if (updatePasswordInDatabase(newPass)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password. Try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameTF.clear();
        oldPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
    }
}
