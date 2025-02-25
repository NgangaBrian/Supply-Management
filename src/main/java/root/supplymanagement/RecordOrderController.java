package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    private TextField itemsSuppliedTF, totalAmountTF, paidAmountTF, balanceTF, invoiceNoTF, chequeNoTF;
    @FXML
    private ComboBox<String> supplierComboBox = new ComboBox<>();
    @FXML
    private ComboBox<String> paymentMethodsCombo = new ComboBox<>();
    @FXML
    private DatePicker dueDateTF;


    private ObservableList<String> supplierNames = FXCollections.observableArrayList();
    private ObservableList<String> paymentMethods = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        theOrderNo();
        loadSupplierNames();
        loadPaymentMethods();
    }

    private void loadSupplierNames() {
        String querry = "select name from suppliers";

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){
            while (resultSet.next()) {
                supplierNames.add(resultSet.getString("name"));
            }
            supplierComboBox.setItems(supplierNames);

        } catch (SQLException e){
            e.printStackTrace();
        }
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


    public void storeOrder(){
        String orderNo = orderNoLabel.getText();
        String supplier = (supplierComboBox.getValue() != null) ? supplierComboBox.getValue() : "";
        String itemsSupplied = itemsSuppliedTF.getText();
        String totalAmount = totalAmountTF.getText();
        String paidAmount = paidAmountTF.getText();
        String balance = balanceTF.getText();
        String invoiceNo = invoiceNoTF.getText();
        String paymentMethod = (paymentMethodsCombo.getValue() != null) ? paymentMethodsCombo.getValue() : "";
        String chequeNo = chequeNoTF.getText();
        LocalDate dueDate = dueDateTF.getValue();


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");

        if(orderNo.isEmpty() || supplier.isEmpty() || itemsSupplied.isEmpty() || totalAmount.isEmpty() || paidAmount.isEmpty() || balance.isEmpty() ||
        invoiceNo.isEmpty() || paymentMethod.isEmpty() || chequeNo.isEmpty() || dueDate == null){
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        } else {

            String confirmationMessage = String.format(
                    "Order No: %s\nSupplier: %s\nItems Supplied: %s\nTotal Amount: %s\nPaid Amount: %s\n" +
                            "Balance: %s\nInvoice No: %s\nPayment Method: %s\nCheque No: %s\nDue Date: %s\n\n" +
                            "Are you sure you want to insert this order?",
                    orderNo, supplier, itemsSupplied, totalAmount, paidAmount,
                    balance, invoiceNo, paymentMethod, chequeNo, dueDate
            );


            alert.setHeaderText("Confirmation Details");
            alert.setContentText(confirmationMessage);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                insertOrderRecord(orderNo, supplier, itemsSupplied, totalAmount, paidAmount,
                        balance, invoiceNo, paymentMethod, chequeNo, dueDate);
            }
        }
    }

    public class DateFormatter {
        public static Date convertDateFormat(String inputDate) throws Exception {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = inputFormat.parse(inputDate);
            return new java.sql.Date(utilDate.getTime()); // Proper conversion
        }
    }

    public void insertOrderRecord(String orderNo, String supplier, String itemsSupplied, String totalAmount, String paidAmount, String balance, String invoiceNo, String paymentMethod, String chequeNo, LocalDate dueDate){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Details");

        DBConnection connect = new DBConnection();
        Connection connection1 = connect.getConnection();

        String insertSupplier = "insert into orders(orderNo, supplier, itemsSupplied, totalAmount, paidAmount, balance, invoiceNo, paymentMethod, chequeNo, dueDate) values(?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = connection1.prepareStatement(insertSupplier);
            preparedStatement.setString(1, orderNo);
            preparedStatement.setString(2, supplier);
            preparedStatement.setString(3, itemsSupplied);
            preparedStatement.setString(4, totalAmount);
            preparedStatement.setString(5, paidAmount);
            preparedStatement.setString(6, balance);
            preparedStatement.setString(7, invoiceNo);
            preparedStatement.setString(8, paymentMethod);
            preparedStatement.setString(9, chequeNo);
            preparedStatement.setString(10, String.valueOf(dueDate));


            int rowsaffected = preparedStatement.executeUpdate();

            if(rowsaffected>0){
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Details have been saved successfully!");
                alert.showAndWait();
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
