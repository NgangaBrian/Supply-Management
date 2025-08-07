package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecordPaymentController implements Initializable {

    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;
    @FXML
    private ComboBox<String> supplierCombo, paymentMethodCombo, orderNoCombo;
    @FXML
    private TextField paidAmountTF, referenceNoTF, balanceTF;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea additionalNotesTA;
    @FXML
    private Button saveBtn;

    private ObservableList<String> supplierNames = FXCollections.observableArrayList();
    private ObservableList<String> paymentMethods = FXCollections.observableArrayList();
    Double balance;
    String currency;
    Double initialPaidAmount;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        loadSupplierComboBox();
        loadPaymentMethods();
        datePicker.setValue(LocalDate.now());

        supplierCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               loadOrderNumbers(newValue);

           }
        });
        orderNoCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadBalance(newValue);
            }
        });

        paidAmountTF.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBalance();
        });

        saveBtn.setOnAction(event -> {
            savePaymentDetails();
        });

        supplierCombo.setOnAction(event -> {
            orderNoCombo.requestFocus();
            orderNoCombo.show();
        });

        orderNoCombo.setOnAction(event -> {
            paidAmountTF.clear();
        });

        paidAmountTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                paymentMethodCombo.requestFocus();
                paymentMethodCombo.show();
            }
        });

        paymentMethodCombo.setOnAction(event -> {
            referenceNoTF.requestFocus();
        });

        referenceNoTF.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                datePicker.requestFocus();
                datePicker.setValue(LocalDate.now());
                datePicker.show();
            }
        });

        datePicker.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                savePaymentDetails();
            }
        });
    }



    private void savePaymentDetails(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        if (supplierCombo.getValue() == null || orderNoCombo.getValue() == null) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a supplier and order number");
            alert.showAndWait();
            return;
        }

        // Validate that text fields are not empty before parsing
        if (paidAmountTF.getText().trim().isEmpty() || balanceTF.getText().trim().isEmpty()) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("All fields must be filled");
            alert.showAndWait();
            return;
        }

        try {
            int supplierId = getSelectedSupplierId();
            String supplierName = (supplierCombo.getValue() != null) ? supplierCombo.getValue() : "";
            int orderId = getSelectedOrderId();
            String orderNo = (orderNoCombo.getValue() != null) ? orderNoCombo.getValue() : "";
            Double paidAmount = Double.valueOf(paidAmountTF.getText());

            String balanceText = balanceTF.getText().trim();

            // Extract currency (non-numeric characters before the number)
            String currency = balanceText.replaceAll("[\\d.,\\s-]", ""); // Removes numbers, commas, dot, space, and minus

            // Extract numeric value
            String numericOnly = balanceText.replaceAll("[^\\d.-]", ""); // Keeps digits, dot, minus
            balance = Double.parseDouble(numericOnly);

            String paymentMethod = (paymentMethodCombo.getValue() != null) ? paymentMethodCombo.getValue() : "";
            String referenceNo = referenceNoTF.getText();
            Date date = Date.valueOf(datePicker.getValue().toString());
            String additionalNotes = additionalNotesTA.getText();

            System.out.println(supplierName + supplierId);

            if (paymentMethod.isEmpty() || referenceNo.isEmpty() || date == null) {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setContentText("All fields are required");
                alert.showAndWait();
            } else if (balance <= 0.0){
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setContentText("Order is already fully settled or an overpayment has been made\n Do you wish to continue?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    insertDetails(supplierId, supplierName, orderId, orderNo, paidAmount, currency, paymentMethod, referenceNo, date, additionalNotes, balance);
                }

            } else {
                insertDetails(supplierId, supplierName, orderId, orderNo, paidAmount, currency, paymentMethod, referenceNo, date, additionalNotes, balance);
            }


        } catch (NumberFormatException e){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid number input. Try again");
            alert.showAndWait();
            paidAmountTF.clear();
        }

    }

    private void insertDetails(int supplierId, String supplierName, int orderId, String orderNo, Double paidAmount, String currency, String paymentMethod, String referenceNo, Date date, String additionalNotes, double balance) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        String confirmationMessage = String.format(
                "Supplier: %s\nOrder No: %s\nPaid Amount: %s\nPayment Method: %s\nReference No: %s\n" +
                        "Date: %s\nAdditional Note: %s\n\n",
                supplierName, orderNo, paidAmount, paymentMethod, referenceNo,
                date, additionalNotes
        );


        alert.setHeaderText("Are you sure you want to insert this payment?");
        alert.setContentText(confirmationMessage);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveDetails(supplierId, orderId, paidAmount, currency, paymentMethod, referenceNo, date, additionalNotes);
            updateOrdersTable(paidAmount, balance, orderNo);
        }
    }


    private void saveDetails(int supplierId, int order_id, Double paidAmount, String currency, String paymentMethod, String referenceNo, Date date, String additionalNotes) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Details");

        String querry = "Insert into payments (supplier_id, order_id, paidAmount, currency, paymentMethod, referenceNo, date, additionalNotes) values (?,?,?,?,?,?,?,?)";

        DBConnection connection = new DBConnection();
        Connection conn = connection.getConnection();

        try{
            PreparedStatement ps = conn.prepareStatement(querry);
            ps.setInt(1, supplierId);
            ps.setInt(2, order_id);
            ps.setDouble(3, paidAmount);
            ps.setString(4, currency);
            ps.setString(5, paymentMethod);
            ps.setString(6, referenceNo);
            ps.setDate(7, date);
            ps.setString(8, additionalNotes);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Executing insertDetails for Order No: " + order_id);
                alert.setContentText("Payment details saved successfully");
                alert.showAndWait();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save details");
                alert.showAndWait();
            }

        } catch (SQLException e){
            e.printStackTrace();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Failed to save details");
            alert.showAndWait();
        }
    }

    private void updateOrdersTable(Double paidAmount, Double balance, String orderNo) {
        String querry = "Update orders set paidAmount = ?, balance = ? where orderNo = ?";

        DBConnection connection = new DBConnection();
        Connection conn = connection.getConnection();

        Double newPaidAmount = paidAmount + initialPaidAmount;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(querry);
            preparedStatement.setDouble(1, newPaidAmount);
            preparedStatement.setDouble(2, balance);
            preparedStatement.setString(3, orderNo);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order updated successfully");
                paidAmountTF.clear();
                referenceNoTF.clear();
                balanceTF.clear();
                supplierCombo.getSelectionModel().clearSelection();
                orderNoCombo.getSelectionModel().clearSelection();
            } else {
                System.out.println("Failed to save details");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void loadBalance(String newValue) {
        String querry = "Select balance, paidAmount, currency from orders where orderNo = '" + newValue + "'";

        DBConnection con = new DBConnection();
        try(Connection con1 = con.getConnection();
        Statement stmt = con1.createStatement();
        ResultSet rs = stmt.executeQuery(querry)) {
            while (rs.next()) {
                balance = Double.valueOf(rs.getString("balance"));
                currency = rs.getString("currency");
                initialPaidAmount = Double.valueOf(rs.getString("paidAmount"));
                balanceTF.setText(currency+ " " + String.valueOf(moneyFormat.format(balance)));
                paidAmountTF.requestFocus();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadOrderNumbers(String newValue) {
        ObservableList<String> orderNumbers = FXCollections.observableArrayList();

        String querry = "Select id, orderNo from orders where supplier = '" + newValue + "'";

        ///  TODO; update orders so we use supplierId instead of sullpier name

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(querry)) {

            Map<String, Integer> orderNumberMap = new HashMap<>();
            orderNoCombo.getItems().clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String orderNo = resultSet.getString("orderNo");
                orderNumberMap.put(orderNo, id);
                orderNoCombo.getItems().add(orderNo);
            }
            orderNoCombo.setUserData(orderNumberMap);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadSupplierComboBox() {
        String querry = "select id, name from suppliers";

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){

            Map<String, Integer> supplierMap = new HashMap<>();
            supplierCombo.getItems().clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                supplierMap.put(name, id);
                supplierCombo.getItems().add(name);
            }

            supplierCombo.setUserData(supplierMap);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getSelectedSupplierId() {
        String selectedSupplier = supplierCombo.getValue();
        if (selectedSupplier == null) return -1;

        Map<String, Integer> supplierMap = (Map<String, Integer>) supplierCombo.getUserData();
        return supplierMap.getOrDefault(selectedSupplier, -1);
    }

    private int getSelectedOrderId() {
        String selectedSupplier = orderNoCombo.getValue();
        if (selectedSupplier == null) return -1;

        Map<String, Integer> supplierMap = (Map<String, Integer>) orderNoCombo.getUserData();
        return supplierMap.getOrDefault(selectedSupplier, -1);
    }

    private void loadPaymentMethods() {
        String querry = "select name from paymentmethods";

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){
            while (resultSet.next()) {
                paymentMethods.add(resultSet.getString("name"));
            }
            paymentMethodCombo.setItems(paymentMethods);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateBalance() {
        try {
            // Check if the input field is empty and set default value to 0.0
            double paidAmount = paidAmountTF.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(paidAmountTF.getText());
            double newBalance = balance - paidAmount;
            balanceTF.setText(currency + " " + String.valueOf(moneyFormat.format(newBalance)));


        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid input");
            // Handle invalid inputs gracefully (e.g., non-numeric input)
            balanceTF.setText(String.valueOf(balance));
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
