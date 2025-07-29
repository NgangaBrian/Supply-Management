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
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

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
        String orderNo = orderNoLB.getText();
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

            // Define current date
            String reportDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

// Left column (labels in bold)
            ComponentBuilder<?, ?> leftColumn = Components.verticalList(
                    Components.horizontalList(
                            Components.text("Order No: ").setStyle(Styles.style().bold()),
                            Components.text(orderNo)
                    ),
                    Components.horizontalList(
                            Components.text("Supplier: ").setStyle(Styles.style().bold()),
                            Components.text(orderRs.getString("supplier"))
                    ),
                    Components.horizontalList(
                            Components.text("Date Placed: ").setStyle(Styles.style().bold()),
                            Components.text(String.valueOf(orderRs.getTimestamp("createdAt")))
                    ),Components.horizontalList(
                            Components.text("Report Generated On: ").setStyle(Styles.style().bold()),
                            Components.text(reportDate)
                    )

            );

// Right column (labels in bold)
            ComponentBuilder<?, ?> rightColumn = Components.verticalList(
                    Components.horizontalList(
                            Components.text("Total Amount: ").setStyle(Styles.style().bold()),
                            Components.text("KES " + orderRs.getBigDecimal("totalAmount"))
                    ),
                    Components.horizontalList(
                            Components.text("Paid Amount: ").setStyle(Styles.style().bold()),
                            Components.text("KES " + orderRs.getBigDecimal("paidAmount"))
                    ),
                    Components.horizontalList(
                            Components.text("Balance: ").setStyle(Styles.style().bold()),
                            Components.text("KES " + orderRs.getBigDecimal("balance"))
                    ),
                    Components.horizontalList(
                            Components.text("Due Date: ").setStyle(Styles.style().bold()),
                            Components.text(String.valueOf(orderRs.getDate("dueDate")))
                    )
            );

// Combine into two-column layout
            ComponentBuilder<?, ?> orderInfo = Components.verticalList(
                    Components.text("ORDER REPORT")
                            .setStyle(Styles.style().bold().setFontSize(18).setHorizontalAlignment(HorizontalAlignment.CENTER)),
                    Components.horizontalList()
                            .add(leftColumn, rightColumn)
                            .setGap(50) // adjust space between the two columns
            ).setStyle(Styles.style().setTopPadding(10).setFontSize(11));

            // Generate report
            JasperPrint jasperPrint = report()
                    .setPageFormat(PageType.A4)
                    .title(
                            header,
                            Components.verticalGap(10),
                            orderInfo,
                            Components.verticalGap(15)
                    )
                    .columns(
                            Columns.reportRowNumberColumn("No.")
                                    .setFixedColumns(2)
                                    .setTitleStyle(Styles.style().bold()) // <- Makes the title bold
                                    .setStyle(Styles.style()
                                            .setPadding(5)
                                            .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                            .setBorder(Styles.pen1Point())),

                            Columns.column("Product", "productName", DataTypes.stringType())
                                    .setTitleStyle(Styles.style().bold()) // <- Makes the title bold
                                    .setStyle(Styles.style()
                                            .setPadding(5)
                                            .setBorder(Styles.pen1Point())),

                            Columns.column("Quantity", "quantity", DataTypes.stringType())
                                    .setTitleStyle(Styles.style().bold()) // <- Makes the title bold
                                    .setStyle(Styles.style()
                                            .setPadding(5)
                                            .setBorder(Styles.pen1Point()))
                    )
                    .highlightDetailEvenRows()
                    .setDataSource(productRs)
                    .toJasperPrint();

            // Show with JasperViewer and prevent full app from closing
            JasperViewer viewer = new JasperViewer(jasperPrint, false); // 'false' prevents app from exiting
            viewer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            viewer.setVisible(true);
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
