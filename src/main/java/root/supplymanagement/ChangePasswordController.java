package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML
    private PasswordField oldPassword, newPassword, confirmPassword;
    @FXML
    private Button saveButton;

    private User loggedInUser;



    private String getCurrentPasswordFromDB() {
        String query = "SELECT password FROM users WHERE id = 1"; // Change logic to get user ID dynamically
        DBConnection connect = new DBConnection();
        try (Connection conn = connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve password.");
        }
        return null;
    }

    public void setUserData(User user) {
        this.loggedInUser = user;
    }

    private boolean updatePasswordInDatabase(String newPass) {
        String query = "UPDATE users SET password = ? WHERE id = 1"; // Change logic to get user ID dynamically
        DBConnection connect = new DBConnection();
        try (Connection conn = connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPass);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update password.");
        }
        return false;
    }

    @FXML
    private void changePassword() {
        String oldPass = oldPassword.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();
        String currentPass = getCurrentPasswordFromDB();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (currentPass == null || !oldPass.equals(currentPass)) {
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
        oldPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
    }
}
