package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ViewSupplierDataController {

    @FXML
    private TabPane tabPane;
    @FXML
    private ComboBox<String> orderNoComboBox;
    @FXML
    private ImageView closeBtnImage, minimizeBtnImage, maximizeBtnImage;
    @FXML
    private Tab ordersTab, paymentsTab;
    @FXML
    private VBox ordersVbox, paymentsVbox;

    int supplierId = -1; // Default value if supplier is not found
    Map<Integer, String> orderMap = new HashMap<>();
    String suppliername = null;


    public void initialize() {
        loadImages();
        orderNoComboBox.setOnAction(event -> {handleOrderSelection();});
    }

    private void handleOrderSelection() {
        String selectedOrderNo = orderNoComboBox.getValue(); // Get selected order number
        paymentsVbox.getChildren().clear();

        if ("All".equals(selectedOrderNo)) {
            // If "All" is selected, load all payments for the supplier
            loadPaymentsDataForSupplier(suppliername);
        } else {
            // Fetch the corresponding order_id from the ComboBox mapping
            int selectedOrderId = getOrderIdByOrderNo(selectedOrderNo);
            if (selectedOrderId != -1) {
                loadPaymentsData(selectedOrderId, supplierId);
            }
        }
    }

    public void setSupplierName(String supplierName) {
        this.suppliername = supplierName;
    }

    public void loadPaymentsDataForSupplier(String supplierName) {
        int supplierId = getSupplierId(supplierName);

        String query = "select * from payments where supplier_id = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, supplierId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
//                    int supplierId = resultSet.getInt("supplier_id");
                    double paidAmount = resultSet.getDouble("paidAmount");
                    String paymentsMethod = resultSet.getString("paymentMethod");
                    String referenceNo = resultSet.getString("referenceNo");
                    Date datePaid = resultSet.getDate("date");
                    String additionalNotes = resultSet.getString("additionalNotes");

                    setDetailsToView(orderId, supplierId, paidAmount, paymentsMethod, referenceNo, datePaid, additionalNotes);

                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void loadOrderNumbersComboBox(String supplierName) {
        String query = "SELECT id, orderNo FROM orders WHERE supplier = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, supplierName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                orderNoComboBox.getItems().clear();
                orderNoComboBox.getItems().addAll("All");
                orderNoComboBox.setValue("All");

                while (resultSet.next()) {
                    int orderId = resultSet.getInt("id");
                    String orderNo = resultSet.getString("orderNo");
                    orderMap.put(orderId, orderNo);
                    orderNoComboBox.getItems().addAll(orderNo);                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log this error
        }

        // Store the map somewhere accessible if needed
    }


    private int getSupplierId(String supplierName) {
        String query = "SELECT id FROM suppliers WHERE name = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, supplierName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    supplierId = resultSet.getInt("id");

                } else {
                    System.out.println("Supplier not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log this error
        }
        return supplierId;
    }

    private int getOrderIdByOrderNo(String orderNo) {
        for (Map.Entry<Integer, String> entry : orderMap.entrySet()) {
            if (entry.getValue().equals(orderNo)) {
                return entry.getKey();
            }
        }
        return -1; // Return -1 if not found
    }

    private void loadPaymentsData(int orderId, int supplierId) {
        String query = "SELECT * FROM payments WHERE order_id = ? AND supplier_id = ?";


        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {


                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, supplierId);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                   // int orderId = resultSet.getInt("order_id");
                    //int supplierId = resultSet.getInt("supplier_id");
                    double paidAmount = resultSet.getDouble("paidAmount");
                    String paymentsMethod = resultSet.getString("paymentMethod");
                    String referenceNo = resultSet.getString("referenceNo");
                    Date datePaid = resultSet.getDate("date");
                    String additionalNotes = resultSet.getString("additionalNotes");

                    setDetailsToView(orderId, supplierId, paidAmount, paymentsMethod, referenceNo, datePaid, additionalNotes);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log this error instead of printing
        }
    }

    private void setDetailsToView(int orderId, int supplierId, double paidAmount, String paymentsMethod, String referenceNo, Date datePaid, String additionalNotes) {
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
                controller.setPaymentData(orderNo, supplierName, paidAmount, paymentsMethod,
                        referenceNo, datePaid, additionalNotes);
                paymentsVbox.getChildren().add(node);
            }
            preparedStatement.close();
            connection.close();
        } catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }

    public void loadOrdersData(String supplierName) {
        ordersVbox.getChildren().clear(); // Clear existing items

        String query = "SELECT * FROM orders WHERE supplier = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, supplierName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ordersItem.fxml"));
                    Node node = loader.load();
                    OrdersItemController controller = loader.getController();

                    controller.setOrderData(
                            resultSet.getString("orderNo"),
                            resultSet.getString("supplier"),
                            resultSet.getDouble("totalAmount"),
                            resultSet.getDouble("paidAmount"),
                            resultSet.getDouble("balance"),
                            LocalDate.from(resultSet.getTimestamp("createdAt").toLocalDateTime())
                    );

                    ordersVbox.getChildren().add(node);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Ideally, log this error instead of printing
        }
    }

    public void loadImages(){
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
