package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordOrderController implements Initializable {

    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;
    @FXML
    private Label orderNoLabel;
    @FXML
    private TextField totalAmountTF, paidAmountTF, balanceTF, invoiceNoTF, chequeNoTF;
    @FXML
    private ComboBox<String> supplierComboBox = new ComboBox<>();
    @FXML
    private ComboBox<String> paymentMethodsCombo = new ComboBox<>();
    @FXML
    private ComboBox<String> currencyComboBox = new ComboBox<>();
    @FXML
    private DatePicker dueDateTF;


    private ObservableList<String> supplierNames = FXCollections.observableArrayList();
    private ObservableList<String> paymentMethods = FXCollections.observableArrayList();
    private ObservableList<String> currencies = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        theOrderNo();
        loadSupplierNames();
        loadCurrency();
        loadPaymentMethods();

        paidAmountTF.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBalance();
        });
    }

    private void updateBalance() {
        try {
            // Check if the input field is empty and set default value to 0.0
            double paidAmount = paidAmountTF.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(paidAmountTF.getText());
            double totalAmount = totalAmountTF.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(totalAmountTF.getText());
            double balance = totalAmount - paidAmount;
            balanceTF.setText(String.valueOf(balance));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid input");
            // Handle invalid inputs gracefully (e.g., non-numeric input)
            balanceTF.setText("0.00");
        }
    }

    private void loadSupplierNames() {
        String querry = "select id, name from suppliers";
        Map<String, Integer> supplierMap = new HashMap<>();

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){

            supplierComboBox.getItems().clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                supplierMap.put(name, id);
                supplierComboBox.getItems().add(name);
            }

            supplierComboBox.setUserData(supplierMap);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getSelectedSupplierId() {
        String selectedSupplier = supplierComboBox.getValue();
        if (selectedSupplier == null) return -1;

        Map<String, Integer> supplierMap = (Map<String, Integer>) supplierComboBox.getUserData();
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
            paymentMethodsCombo.setItems(paymentMethods);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadCurrency() {
        String querry = "select code from currency";

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){
            while (resultSet.next()) {
                currencies.add(resultSet.getString("code"));
            }
            currencyComboBox.setItems(currencies);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static String getHighestOrderNo(Connection conn) throws SQLException {
        String sql = "SELECT orderNo FROM orders ORDER BY orderNo DESC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("orderNo");
            }
        }
        return null; // Return null if no orders exist
    }

    public  void theOrderNo(){
        DBConnection connect = new DBConnection();
        Connection conn = connect.getConnection();
        try {
            String highestOrderNo = getHighestOrderNo(conn);
            String newOrderNo = generateNextOrderNo(highestOrderNo);
            orderNoLabel.setText(newOrderNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Function to generate the next order number
    private static String generateNextOrderNo(String highestOrderNo) {
        // Default starting order number if none exists
        if (highestOrderNo == null) {
            return "KOS001";
        }

        // Extract the numeric part using regex
        Pattern pattern = Pattern.compile("KOS(\\d+)");
        Matcher matcher = pattern.matcher(highestOrderNo);

        if (matcher.matches()) {
            int currentNumber = Integer.parseInt(matcher.group(1));
            int nextNumber = currentNumber + 1;

            // Format to maintain leading zeros (e.g., KOS001 → KOS002)
            return String.format("KOS%03d", nextNumber);
        }

        // Return default if something goes wrong
        return "KOS001";
    }


//    public void storeOrder(){
//        String orderNo = orderNoLabel.getText();
//        String supplier = (supplierComboBox.getValue() != null) ? supplierComboBox.getValue() : "";
//        int supplierId = getSelectedSupplierId();
//        String totalAmount = totalAmountTF.getText();
//        String currency = (currencyComboBox.getValue() != null) ? currencyComboBox.getValue() : "";
//        String paidAmount = paidAmountTF.getText();
//        String balance = balanceTF.getText();
//        String invoiceNo = invoiceNoTF.getText();
//        String paymentMethod = (paymentMethodsCombo.getValue() != null) ? paymentMethodsCombo.getValue() : "";
//        String chequeNo = chequeNoTF.getText();
//        LocalDate dueDate = dueDateTF.getValue();
//        LocalDate currentDate = LocalDate.now();
//
//
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation");
//
//        if(orderNo.isEmpty() || supplier.isEmpty() || totalAmount.isEmpty() || currency.isEmpty() || paidAmount.isEmpty() || balance.isEmpty() ||
//        invoiceNo.isEmpty() || paymentMethod.isEmpty() || chequeNo.isEmpty() || dueDate == null){
//            alert.setAlertType(Alert.AlertType.WARNING);
//            alert.setTitle("Warning");
//            alert.setContentText("Please fill all the fields");
//            alert.showAndWait();
//            return;
//        } else {
//
//            String confirmationMessage = String.format(
//                    "Order No: %s\nSupplier: %s\nTotal Amount: %s %s\nPaid Amount: %s %s\n" +
//                            "Balance: %s %s\nInvoice No: %s\nPayment Method: %s\nCheque No: %s\nDue Date: %s\n\n" +
//                            "Are you sure you want to insert this order?",
//                    orderNo, supplier,
//                    currency, totalAmount,
//                    currency, paidAmount,
//                    currency, balance,
//                    invoiceNo, paymentMethod, chequeNo, dueDate
//            );
//
//
//            alert.setHeaderText("Confirmation Details");
//            alert.setContentText(confirmationMessage);
//
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.isPresent() && result.get() == ButtonType.OK) {
////                int orderId = insertOrderRecord(orderNo, supplier, totalAmount, paidAmount, currency,
////                        balance, invoiceNo, paymentMethod, chequeNo, dueDate);
//
//                updatePayments(supplierId, orderId, paidAmount, currency, paymentMethod, invoiceNo, currentDate);
//            }
//        }
//    }

    public void storeOrder() {
        String orderNo = orderNoLabel.getText();
        String supplier = supplierComboBox.getValue();
        String currency = currencyComboBox.getValue();
        String paymentMethod = paymentMethodsCombo.getValue();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");

        if (supplier == null || currency == null || paymentMethod == null ||
                totalAmountTF.getText().isEmpty() || paidAmountTF.getText().isEmpty() ||
                balanceTF.getText().isEmpty() || invoiceNoTF.getText().isEmpty() ||
                chequeNoTF.getText().isEmpty() || dueDateTF.getValue() == null) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
            return;
        }

        try {
            double totalAmount = Double.parseDouble(totalAmountTF.getText());
            double paidAmount = Double.parseDouble(paidAmountTF.getText());
            double balance = Double.parseDouble(balanceTF.getText());

            if (totalAmount < 0 || paidAmount < 0 || balance < 0) {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setContentText("Amounts cannot be negative.");
                alert.showAndWait();
                return;
            }

            String invoiceNo = invoiceNoTF.getText();
            String chequeNo = chequeNoTF.getText();
            LocalDate dueDate = dueDateTF.getValue();
            LocalDate currentDate = LocalDate.now();
            int supplierId = getSelectedSupplierId();

            String confirmationMessage = String.format(
                    "Order No: %s\nSupplier: %s\nTotal Amount: %s %s\nPaid Amount: %s %s\n" +
                            "Balance: %s %s\nInvoice No: %s\nPayment Method: %s\nCheque No: %s\nDue Date: %s\n\n" +
                            "Are you sure you want to insert this order?",
                    orderNo, supplier,
                    currency, totalAmount,
                    currency, paidAmount,
                    currency, balance,
                    invoiceNo, paymentMethod, chequeNo, dueDate
            );


            alert.setHeaderText("Confirmation Details");
            alert.setContentText(confirmationMessage);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                int orderId = insertOrderRecord(orderNo, supplier, totalAmount, paidAmount, currency,
                        balance, invoiceNo, paymentMethod, chequeNo, dueDate);                if (orderId > 0) {
                    updatePayments(supplierId, orderId, paidAmount, currency, paymentMethod, invoiceNo, currentDate);
                    prepareForNextEntry();
                    prepareForNextEntry();
                }
            }

        } catch (NumberFormatException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Warning");
            alert.setContentText("Please enter valid numeric values for amounts.");
            alert.showAndWait();
            return;
        }
    }


    public class DateFormatter {
        public static Date convertDateFormat(String inputDate) throws Exception {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = inputFormat.parse(inputDate);
            return new java.sql.Date(utilDate.getTime()); // Proper conversion
        }
    }

    private void updatePayments(int supplierId, int orderId, Double paidAmount, String currency, String paymentMethod, String invoiceNo, LocalDate currentDate) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Details");

        DBConnection connect = new DBConnection();

        String querry = "insert into payments(supplier_id, order_id, paidAmount, currency, paymentMethod, referenceNo, date) values(?,?,?,?,?,?,?)";
        try (Connection connection1 = connect.getConnection();
            PreparedStatement stmt = connection1.prepareStatement(querry)){

            stmt.setInt(1, supplierId);
            stmt.setInt(2, orderId);
            stmt.setDouble(3, paidAmount);
            stmt.setString(4, currency);
            stmt.setString(5, paymentMethod);
            stmt.setString(6, invoiceNo);
            stmt.setString(7, currentDate.toString());


            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Do nothing
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save payment details!");
                alert.showAndWait();
            }
        } catch (SQLException e){
            e.printStackTrace();
            e.getCause();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Failed. Try again later.");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public int insertOrderRecord(String orderNo, String supplier, Double totalAmount, Double paidAmount, String currency, Double balance, String invoiceNo, String paymentMethod, String chequeNo, LocalDate dueDate){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Details");

        DBConnection connect = new DBConnection();


        String insertSupplier = "insert into orders(orderNo, supplier, totalAmount, paidAmount, currency, balance, invoiceNo, paymentMethod, referenceNo, dueDate) values(?,?,?,?,?,?,?,?,?,?)";
        int generatedId = -1; // Variable to store the generated ID


        try (Connection connection1 = connect.getConnection();
            PreparedStatement preparedStatement = connection1.prepareStatement(insertSupplier, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, orderNo);
            preparedStatement.setString(2, supplier);
            preparedStatement.setDouble(3, totalAmount);
            preparedStatement.setDouble(4, paidAmount);
            preparedStatement.setString(5, currency);
            preparedStatement.setDouble(6, balance);
            preparedStatement.setString(7, invoiceNo);
            preparedStatement.setString(8, paymentMethod);
            preparedStatement.setString(9, chequeNo);
            preparedStatement.setString(10, String.valueOf(dueDate));


            int rowsaffected = preparedStatement.executeUpdate();

            if(rowsaffected>0){

                // Retrieve the generated key
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1); // Get the first column (ID)
                }
                generatedKeys.close();

                try {
                    // Load the new window
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ordered-items.fxml"));
                    Parent root = fxmlLoader.load();

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 550, 465));
                    stage.initStyle(StageStyle.UNDECORATED);

                    AddOrderedItems controller = fxmlLoader.getController();
                    controller.setOrderNo(orderNo);


                    // Ensure the new window is on top and focused
                    stage.setAlwaysOnTop(true);
                    stage.show();
                    stage.requestFocus(); // Forces focus on the new stage
                    stage.setAlwaysOnTop(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save details!");
                alert.showAndWait();
            }
            preparedStatement.close();
            connection1.close();
        } catch (SQLIntegrityConstraintViolationException e){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Order No repeated!!!");
            alert.showAndWait();
        }catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Failed. Try again later.");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
        return generatedId;
    }

    public void prepareForNextEntry() {
        // Clear all input fields
        totalAmountTF.clear();
        paidAmountTF.clear();
        balanceTF.clear();
        invoiceNoTF.clear();
        chequeNoTF.clear();

        // Reset combo boxes
        supplierComboBox.setEditable(true);
        paymentMethodsCombo.setEditable(true);
        currencyComboBox.setEditable(true);
        supplierComboBox.setValue(null);
        paymentMethodsCombo.setValue(null);
        currencyComboBox.setValue(null);
        supplierComboBox.setEditable(false);
        paymentMethodsCombo.setEditable(false);
        currencyComboBox.setEditable(false);

        // Reset date picker
        dueDateTF.setValue(null);

        // Generate a new order number
        theOrderNo();
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



    private void loadImages() {
        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
    }

}
