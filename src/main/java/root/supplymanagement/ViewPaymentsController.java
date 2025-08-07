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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewPaymentsController implements Initializable {


    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;
    @FXML
    private VBox paymentItems;
    @FXML
    private ComboBox<String> filter;
    @FXML
    private Label balanceLB, amountPaidLB, pendingPaymentsLB;

    private final ObservableList<String> filterItems = FXCollections.observableArrayList("All", "Last 7 days", "Last 30 days", "Last 90 days");

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        getTotalBalance();
        getTotalAmountPaid();
        getTotalPendingInvoices();
        filter.setItems(filterItems);
        filter.setValue("All");

        loadPaymentData(null);
        filter.setOnAction(event -> loadPaymentData(filter.getValue()));

    }

    public void getTotalAmountPaid() {
        String query = "SELECT SUM(paidAmount) FROM payments";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double paidAmount = rs.getDouble(1);
                amountPaidLB.setText(String.valueOf(moneyFormat.format(paidAmount)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTotalPendingInvoices() {
        String query = "SELECT COUNT(*) FROM orders WHERE balance > 0";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int pendingPayments = rs.getInt(1);
                pendingPaymentsLB.setText(String.valueOf(moneyFormat.format(pendingPayments)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTotalBalance() {
        String query = "SELECT SUM(balance) FROM orders WHERE balance > 0";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double totalBalance = rs.getDouble(1);
                balanceLB.setText(String.valueOf(moneyFormat.format(totalBalance)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentData(String filterOption) {
        String query = "SELECT * FROM payments";
        boolean hasFilter = filterOption != null && !"All".equals(filterOption);

        if (hasFilter) {
            query += " WHERE createdAt >= NOW() - INTERVAL ? DAY";
        }

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (hasFilter) {
                int days = switch (filterOption) {
                    case "Last 30 days" -> 30;
                    case "Last 90 days" -> 90;
                    default -> 7;
                };
                preparedStatement.setInt(1, days);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    int orderId = resultSet.getInt("order_id");
                    int supplierId = resultSet.getInt("supplier_id");
                    double paidAmount = resultSet.getDouble("paidAmount");
                    String currency = resultSet.getString("currency");
                    String paymentsMethod = resultSet.getString("paymentMethod");
                    String referenceNo = resultSet.getString("referenceNo");
                    Date datePaid = resultSet.getDate("date");
                    String additionalNotes = resultSet.getString("additionalNotes");

                    setDetailsToView(orderId, supplierId, paidAmount, currency, paymentsMethod, referenceNo, datePaid, additionalNotes);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log this error instead of printing
        }
    }

    private void setDetailsToView(int orderId, int supplierId, double paidAmount, String currency, String paymentsMethod, String referenceNo, Date datePaid, String additionalNotes) {

        String query = "SELECT orderNo, supplier FROM orders WHERE id = ?";

        // todo; use supplier id after changing the table orders

        Connection connection = new DBConnection().getConnection();


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderId);
            // todo; set supplierId to prepared statements after the todo above

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                String orderNo = resultSet.getString("orderNo");
                String supplierName = resultSet.getString("supplier");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("paymentItem.fxml"));

                Node node = loader.load();
                PaymentItemController controller = loader.getController();
                controller.setPaymentData(orderNo, supplierName, paidAmount, currency, paymentsMethod,
                        referenceNo, datePaid, additionalNotes);
                paymentItems.getChildren().add(node);
            }
            preparedStatement.close();
            connection.close();
        } catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void loadImages() {
        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
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
