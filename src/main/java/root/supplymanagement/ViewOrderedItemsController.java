package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ViewOrderedItemsController {
    @FXML
    private ImageView maximizeBtnImage, minimizeBtnImage, closeBtnImage;
    @FXML
    private Label orderNoLB, totalAmountLB, supplierNameLB, dueDateLB, paidAmountLB, balanceLB;
    @FXML
    private VBox vBox;

    public String orderNo, supplierName, duedate;
    public double paidAmount, balance, totalAmount;

    public void initialize() {
        loadImages();
    }

    public void loadOrderedItemsData(String orderNumber) {
        vBox.getChildren().clear(); // Clear existing items

        System.out.println("OrderNo is: " + orderNo + "\n Order Number is: " + orderNumber);


        String query = "SELECT productName, quantity FROM ordered_products WHERE orderNo = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the order number parameter in the query
            preparedStatement.setString(1, orderNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("view-ordered-product-item.fxml"));
                    Node node = loader.load();
                    System.out.println("Opening page");
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
