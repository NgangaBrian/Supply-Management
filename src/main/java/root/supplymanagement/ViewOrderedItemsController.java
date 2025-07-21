package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.Styles;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.PageType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;

public class ViewOrderedItemsController {
    @FXML
    private ImageView maximizeBtnImage, minimizeBtnImage, closeBtnImage;
    @FXML
    private Label orderNoLB, totalAmountLB, supplierNameLB, dueDateLB, paidAmountLB, balanceLB;
    @FXML
    private VBox vBox;
    @FXML
    private Button generateOrderReport;


    public String orderNo, supplierName, duedate;
    public double paidAmount, balance, totalAmount;

    public void initialize() {
        loadImages();
    }

    public void loadOrderedItemsData(String orderNumber) {
        vBox.getChildren().clear(); // Clear existing items

        String query = "SELECT productName, quantity FROM ordered_products WHERE orderNo = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the order number parameter in the query
            preparedStatement.setString(1, orderNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("view-ordered-product-item.fxml"));
                    Node node = loader.load();
                    ViewOrderedProductItemController controller = loader.getController();

                    controller.setOrderedProductdata(
                            resultSet.getString("productName"),
                            String.valueOf(resultSet.getInt("quantity")));

                    vBox.getChildren().add(node);
                }
            }
            preparedStatement.close();
            connection.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Ideally, log this error instead of printing
        }
    }

    @FXML
    public void generateOrderReport() {
        String orderNo = "KOS001";
        try(Connection conn = new DBConnection().getConnection()){
            String orderQuery = "SELECT * FROM orders WHERE orderNo = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, orderNo);
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.next()) {
                System.out.println("Order Not Found");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Not Found");
                alert.setContentText("Order Not Found");
                return;
            }

            String productQuery = "SELECT productName, quantity FROM ordered_products WHERE orderNo = ?";
            PreparedStatement productStmt = conn.prepareStatement(productQuery);
            productStmt.setString(1, orderNo);
            ResultSet productRs = productStmt.executeQuery();

            // Load logo image (make sure path is correct!)
            ImageBuilder logo = Components.image("src/main/resources/Images/kimsalogo.png")
                    .setFixedDimension(60, 60)
                    .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER);

            // Company details
            ComponentBuilder<?, ?> companyInfo = Components.verticalList(
                    Components.text("My Company Ltd").setStyle(Styles.style().bold().setFontSize(14).setHorizontalAlignment(HorizontalAlignment.CENTER)),
                    Components.text("Email: info@mycompany.com").setStyle(Styles.style().setFontSize(10).setHorizontalAlignment(HorizontalAlignment.CENTER)),
                    Components.text("1234 Some Street, Nairobi, Kenya").setStyle(Styles.style().setFontSize(10).setHorizontalAlignment(HorizontalAlignment.CENTER))
            );

            // Combine logo and company info in a horizontal layout
            ComponentBuilder<?, ?> header = Components.horizontalList(
                    logo,
                    Components.horizontalGap(10), // spacing between logo and text
                    companyInfo
            ).setStyle(Styles.style().setHorizontalAlignment(HorizontalAlignment.CENTER));

            // Order information section
            ComponentBuilder<?, ?> orderInfo = Components.verticalList(
                    Components.text("ORDER REPORT").setStyle(Styles.style().bold().setFontSize(18)),
                    Components.text("Order No: " + orderNo),
                    Components.text("Supplier: " + orderRs.getString("supplier")),
                    Components.text("Date Placed: " + orderRs.getTimestamp("createdAt")),
                    Components.text("Due Date: " + orderRs.getDate("dueDate")),
                    Components.text("Total Amount: KES " + orderRs.getBigDecimal("totalAmount")),
                    Components.text("Paid Amount: KES " + orderRs.getBigDecimal("paidAmount")),
                    Components.text("Balance: KES " + orderRs.getBigDecimal("balance"))
            ).setStyle(Styles.style().setTopPadding(10).setFontSize(11));

            // Generate report
            report()
                    .setPageFormat(PageType.A4)
                    .title(
                            header,
                            Components.verticalGap(10),
                            orderInfo,
                            Components.verticalGap(15)
                    )
                    .columns(
                            Columns.column("Product", "productName", DataTypes.stringType())
                                    .setStyle(Styles.style().setBorder(Styles.pen1Point())),
                            Columns.column("Quantity", "quantity", DataTypes.stringType())
                                    .setStyle(Styles.style().setBorder(Styles.pen1Point()))
                    )
                    .highlightDetailEvenRows()
                    .setDataSource(productRs)
                    .show();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleCloseBtnClick() {
        ((Stage) closeBtnImage.getScene().getWindow()).close();
    }

    @FXML
    private void handleMinimizeBtnClick() {
        Stage.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .map(window -> (Stage) window)
                .forEach(stage -> stage.setIconified(true));
    }

    @FXML
    private void handleMaximizeBtnClick() {
        Stage stage = (Stage) maximizeBtnImage.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    private void loadImages() {
        closeBtnImage.setImage(new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm()));
        minimizeBtnImage.setImage(new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm()));
        maximizeBtnImage.setImage(new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm()));
    }

    public void setOrderDetails(String orderNo, String supplierName, double paidAmount, double balance, double totalAmount,String duedate) {
        this.orderNo = orderNo;
        this.supplierName = supplierName;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.totalAmount = totalAmount;
        this.duedate = duedate;

        orderNoLB.setText(orderNo);
        totalAmountLB.setText(String.valueOf(totalAmount));
        supplierNameLB.setText(supplierName);
        dueDateLB.setText(duedate);
        paidAmountLB.setText(String.valueOf(paidAmount));
        balanceLB.setText(String.valueOf(balance));
    }
}
