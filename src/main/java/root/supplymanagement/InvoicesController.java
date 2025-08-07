package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class InvoicesController implements Initializable {

    @FXML
    private ImageView maximizeBtnImage, closeBtnImage, minimizeBtnImage;
    @FXML
    private Label overdueInvoicesLB, allInvoicesLB, overpaymentsLB;
    @FXML
    private VBox invoiceItemsVBox;
    @FXML
    private ComboBox<String> filter;

    private final ObservableList<String> filterItems = FXCollections.observableArrayList("All Invoices", "Overdue Invoices", "Overpaid Invoices");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        filter.setItems(filterItems);
        filter.setValue("All Invoices");

        loadInvoiceData(null);
        filter.setOnAction(event -> loadInvoiceData(filter.getValue()));
        updateInvoiceSummary();
    }

    public void updateInvoiceSummary() {
        int totalInvoices = 0;
        int overdueInvoices = 0;
        double overpaidAmount = 0.0;

        String totalQuery = "SELECT COUNT(*) FROM orders";
        String overdueQuery = "SELECT COUNT(*) FROM orders WHERE dueDate < CURDATE() AND balance > 0";
        String overpaidQuery = "SELECT SUM(ABS(balance)) FROM orders WHERE balance < 0";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
             PreparedStatement overdueStmt = conn.prepareStatement(overdueQuery);
             PreparedStatement overpaidStmt = conn.prepareStatement(overpaidQuery)) {

            // Get total invoices count
            try (ResultSet rs = totalStmt.executeQuery()) {
                if (rs.next()) {
                    totalInvoices = rs.getInt(1);
                }
            }

            // Get overdue invoices count
            try (ResultSet rs = overdueStmt.executeQuery()) {
                if (rs.next()) {
                    overdueInvoices = rs.getInt(1);
                }
            }

            // Get total overpaid amount
            try (ResultSet rs = overpaidStmt.executeQuery()) {
                if (rs.next()) {
                    overpaidAmount = rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in production
        }

        // Update labels
        allInvoicesLB.setText(String.valueOf(totalInvoices));
        overdueInvoicesLB.setText(String.valueOf(overdueInvoices));
        overpaymentsLB.setText(String.format("%.2f", overpaidAmount));
    }

    private void loadInvoiceData(String filterOption) {
        String query = "SELECT * FROM orders"; // Changed to 'orders' table
        boolean hasFilter = filterOption != null && !"All Invoices".equals(filterOption);

        if (hasFilter) {
            switch (filterOption) {
                case "Overdue Invoices":
                    query += " WHERE dueDate < CURDATE() AND balance > 0";
                    break;
                case "Overpaid Invoices":
                    query += " WHERE paidAmount > totalAmount";
                    break;
                default:
                    break;
            }
        }

        invoiceItemsVBox.getChildren().clear(); // Clear existing items before adding new ones

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int orderId = resultSet.getInt("id");
                String invoiceNo = resultSet.getString("invoiceNo");
                String supplierName = resultSet.getString("supplier");
                double paidAmount = resultSet.getDouble("paidAmount");
                double balance = resultSet.getDouble("balance");
                Date dueDate = resultSet.getDate("dueDate");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("invoiceItem.fxml"));
                Node node = loader.load();
                InvoiceItemController controller = loader.getController();
                controller.setInvoiceData(invoiceNo, supplierName, paidAmount, balance, dueDate);

                invoiceItemsVBox.getChildren().add(node);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }


    private void loadImages() {
        closeBtnImage.setImage(new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm()));
        minimizeBtnImage.setImage(new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm()));
        maximizeBtnImage.setImage(new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm()));
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
