package root.supplymanagement;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.Styles;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;

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
    @FXML
    private Button generateReportBtn;
    @FXML
    private AnchorPane rootAnchorPane;

    private StackPane rootOverlay;
    int supplierId = -1; // Default value if supplier is not found
    Map<Integer, String> orderMap = new HashMap<>();
    String suppliername = null;

    public void initialize() {
        loadImages();
        orderNoComboBox.setOnAction(event -> {handleOrderSelection();});
    }

    public void generateReport() {
        showLoadingOverlay();

        if (suppliername == null || suppliername.isEmpty()) {
            hideLoadingOverlay();
            showAlert(Alert.AlertType.WARNING, "Missing Supplier", "Supplier name not set. Cannot generate report.");
            return;
        }

        new Thread(() -> {
            try {
                generateSupplierPaymentReport(suppliername);
                Platform.runLater(() -> {
                    hideLoadingOverlay();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    hideLoadingOverlay();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void generateSupplierPaymentReport(String supplierName) {
        try (Connection conn = new DBConnection().getConnection()) {

            // Query all necessary payment/order/supplier info
            String query = """
            SELECT s.name as supplierName, s.email, s.phonenumber, s.address,
                   o.id as orderId, o.orderNo, o.invoiceNo, o.totalAmount, o.currency, o.dueDate,
                   p.paidAmount, p.paymentMethod, p.referenceNo, p.date, p.additionalNotes
            FROM payments p
            JOIN orders o ON p.order_id = o.id
            JOIN suppliers s ON p.supplier_id = s.id
            WHERE s.name = ?
            ORDER BY o.id, p.date
        """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, supplierName);
            ResultSet rs = stmt.executeQuery();

            Map<String, Object> supplierInfo = new HashMap<>();
            Map<String, List<Map<String, Object>>> paymentsByOrder = new LinkedHashMap<>();
            Map<String, Double> currencyTotals = new HashMap<>();
            Map<String, Double> currencyPaid = new HashMap<>();

            // ✅ FIX: Keep track of which orders we’ve already counted for totals
            Set<String> countedOrders = new HashSet<>();

            while (rs.next()) {
                if (supplierInfo.isEmpty()) {
                    supplierInfo.put("name", rs.getString("supplierName"));
                    supplierInfo.put("email", rs.getString("email"));
                    supplierInfo.put("phone", rs.getString("phonenumber"));
                    supplierInfo.put("address", rs.getString("address"));
                }

                String orderNo = rs.getString("orderNo");
                String invoiceNo = rs.getString("invoiceNo");
                String currency = rs.getString("currency");
                double totalAmount = rs.getDouble("totalAmount");
                double paidAmount = rs.getDouble("paidAmount");

                // Build row for this payment
                Map<String, Object> payment = new HashMap<>();
                payment.put("orderNo", orderNo);
                payment.put("invoiceNo", invoiceNo);
                payment.put("totalAmount", totalAmount);
                payment.put("currency", currency);
                payment.put("dueDate", rs.getDate("dueDate"));
                payment.put("paidAmount", paidAmount);
                payment.put("paymentMethod", rs.getString("paymentMethod"));
                payment.put("referenceNo", rs.getString("referenceNo"));
                payment.put("date", rs.getDate("date"));
                payment.put("notes", rs.getString("additionalNotes"));

                paymentsByOrder.computeIfAbsent(orderNo + "|" + invoiceNo + "|" + currency + "|" + totalAmount + "|" + rs.getDate("dueDate"),
                        k -> new ArrayList<>()).add(payment);

                // Track totals
                // ✅ FIX: Only count totalAmount once per order
                String orderKey = orderNo + "|" + currency;
                if (!countedOrders.contains(orderKey)) {
                    currencyTotals.merge(currency, totalAmount, Double::sum);
                    countedOrders.add(orderKey);
                }

                currencyPaid.merge(currency, paidAmount, Double::sum);
            }

            List<ComponentBuilder<?, ?>> reportContent = new ArrayList<>();

            // Supplier Info
            reportContent.add(Components.text("Supplier Payment Report")
                    .setStyle(Styles.style().bold().setFontSize(16).setHorizontalAlignment(HorizontalAlignment.CENTER)));

            reportContent.add(Components.verticalList(
                    Components.text("📌 Supplier: " + supplierInfo.get("name")).setStyle(Styles.style().bold()),
                    Components.text("Email: " + supplierInfo.get("email")),
                    Components.text("Phone: " + supplierInfo.get("phone")),
                    Components.text("Address: " + supplierInfo.get("address")),
                    Components.text("Generated On: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date())),
                    Components.line()
            ));

            // Payments grouped per order
            for (Map.Entry<String, List<Map<String, Object>>> entry : paymentsByOrder.entrySet()) {
                String[] orderInfo = entry.getKey().split("\\|");
                String orderNo = orderInfo[0];
                String invoiceNo = orderInfo[1];
                String currency = orderInfo[2];
                double totalAmount = Double.parseDouble(orderInfo[3]);
                Date dueDate = java.sql.Date.valueOf(orderInfo[4]);

                List<Map<String, Object>> payments = entry.getValue();

                double totalPaid = payments.stream().mapToDouble(p -> (double) p.get("paidAmount")).sum();
                double balance = totalAmount - totalPaid;

                // Order Header
                reportContent.add(Components.text("🧾 Order: " + orderNo + " (Invoice: " + invoiceNo + ")")
                        .setStyle(Styles.style().bold().setFontSize(13)));

                reportContent.add(Components.verticalList(
                        Components.text("Total Amount: " + currency + " " + String.format("%,.2f", totalAmount)),
                        Components.text("Due Date: " + dueDate)
                ));

                // Table
                reportContent.add(
                        Components.subreport(
                                report()
                                        .columns(
                                                Columns.column("Payment Date", "date", DataTypes.dateType()),
                                                Columns.column("Method", "paymentMethod", DataTypes.stringType()),
                                                Columns.column("Ref No", "referenceNo", DataTypes.stringType()),
                                                Columns.column("Paid Amount", "paidAmount", DataTypes.doubleType()),
                                                Columns.column("Notes", "notes", DataTypes.stringType())
                                        )
                                        .highlightDetailEvenRows()
                                        .setDataSource(payments)
                                        .summary(
                                                Components.horizontalList(
                                                        Components.text("Total Paid: ").setFixedWidth(350).setStyle(Styles.style().bold()),
                                                        Components.text(currency + " " + String.format("%,.2f", totalPaid)).setStyle(Styles.style().bold())
                                                ),
                                                Components.horizontalList(
                                                        Components.text("Balance Remaining: ").setFixedWidth(350).setStyle(Styles.style().bold()),
                                                        Components.text(currency + " " + String.format("%,.2f", balance)).setStyle(Styles.style().bold())
                                                )
                                        )
                        )
                );
                reportContent.add(Components.line());
            }

            // Grand totals
            reportContent.add(Components.text("🧾 Grand Totals").setStyle(Styles.style().bold().setFontSize(14)));

            List<ComponentBuilder<?, ?>> currencyRows = new ArrayList<>();
            currencyRows.add(Components.horizontalList(
                    Components.text("Currency").setFixedWidth(80).setStyle(Styles.style().bold()),
                    Components.text("Total Invoiced").setFixedWidth(100).setStyle(Styles.style().bold()),
                    Components.text("Total Paid").setFixedWidth(100).setStyle(Styles.style().bold()),
                    Components.text("Balance Remaining").setStyle(Styles.style().bold())
            ));

            // Load logo image (make sure path is correct!)
            ImageBuilder logo = Components.image(getClass().getResource("/Images/kimsalogo.png"))
                    .setFixedDimension(60, 60)
                    .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER);

            // Company details
            ComponentBuilder<?, ?> companyInfo = Components.verticalList(
                    Components.text("KIMSA Trading Company Ltd").setStyle(Styles.style().bold().setFontSize(14)),
                    Components.text("Email: kimsaelectricals89@gmail.com").setStyle(Styles.style().setFontSize(10)),
                    Components.text("P.O BOX 13236, Nakuru, Kenya").setStyle(Styles.style().setFontSize(10))
            );

            // Combine logo and company info in a horizontal layout
            ComponentBuilder<?, ?> header = Components.horizontalList(
                    logo,
                    Components.horizontalGap(10), // spacing between logo and text
                    companyInfo
            ).setStyle(Styles.style().setHorizontalAlignment(HorizontalAlignment.CENTER));

            for (String currency : currencyTotals.keySet()) {
                double total = currencyTotals.get(currency);
                double paid = currencyPaid.get(currency);
                double balance = total - paid;

                currencyRows.add(Components.horizontalList(
                        Components.text(currency).setFixedWidth(80),
                        Components.text(String.format("%,.2f", total)).setFixedWidth(100),
                        Components.text(String.format("%,.2f", paid)).setFixedWidth(100),
                        Components.text(String.format("%,.2f", balance))
                ));
            }

            reportContent.add(Components.verticalList(currencyRows.toArray(new ComponentBuilder[0])));

            JasperPrint jasperPrint = report()
                    .setPageFormat(PageType.A4)
                    .title(Components.verticalList(
                            header,
                            Components.verticalGap(10),
                            Components.verticalList(reportContent.toArray(new ComponentBuilder[0]))
                    ))
                    .toJasperPrint();

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoadingOverlay() {
        ProgressIndicator spinner = new ProgressIndicator();
        Label label = new Label("Generating Report... Please wait...");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        VBox overlayBox = new VBox(10, spinner, label);
        overlayBox.setAlignment(Pos.CENTER);
        overlayBox.setStyle("-fx-padding: 30px;");

        rootOverlay = new StackPane(overlayBox);
        rootOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        rootOverlay.setAlignment(Pos.CENTER);

        // Anchor to fill the parent AnchorPane
        AnchorPane.setTopAnchor(rootOverlay, 150.0);
        AnchorPane.setBottomAnchor(rootOverlay, 150.0);
        AnchorPane.setLeftAnchor(rootOverlay, 150.0);
        AnchorPane.setRightAnchor(rootOverlay, 150.0);

        Platform.runLater(() -> rootAnchorPane.getChildren().add(rootOverlay));
    }

    private void hideLoadingOverlay(){
        if (rootOverlay != null) {
            Platform.runLater(() -> rootAnchorPane.getChildren().remove(rootOverlay));
        }
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
                    String currency = resultSet.getString("currency");
                    String paymentsMethod = resultSet.getString("paymentMethod");
                    String referenceNo = resultSet.getString("referenceNo");
                    Date datePaid = resultSet.getDate("date");
                    String additionalNotes = resultSet.getString("additionalNotes");

                    setDetailsToView(orderId, supplierId, paidAmount, currency, paymentsMethod, referenceNo, datePaid, additionalNotes);

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



        try (Connection connection = new DBConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, orderId);
            // todo; set supplierId to prepared statements after the todo above

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    String orderNo = resultSet.getString("orderNo");
                    String supplierName = resultSet.getString("supplier");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("paymentItem.fxml"));

                    Node node = loader.load();
                    PaymentItemController controller = loader.getController();
                    controller.setPaymentData(orderNo, supplierName, paidAmount, currency, paymentsMethod,
                            referenceNo, datePaid, additionalNotes);
                    paymentsVbox.getChildren().add(node);
                }
            }
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
                            resultSet.getString("currency"),
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
