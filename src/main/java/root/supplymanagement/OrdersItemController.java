package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrdersItemController {
    @FXML
    private Label orderNo, supplierName, totalAmount, paidAmount, balAmount, dueDate;
    @FXML
    private HBox supplietItemView;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public void setOrderData(String orderno, String suppliername, Double totalamount, Double paidamount,
                             String currency, Double balamount, LocalDate duedate) {
        orderNo.setText(orderno);
        supplierName.setText(suppliername);
        totalAmount.setText(currency +" " + moneyFormat.format(totalamount));
        paidAmount.setText(currency +" " + moneyFormat.format(paidamount));
        balAmount.setText(currency +" " + moneyFormat.format(balamount));
        dueDate.setText(dateFormatter.format(duedate));

        // Attach click event to HBox
        supplietItemView.setOnMouseClicked(event -> handleItemClick());

        setTooltip(orderNo, orderNo.getText());
        setTooltip(supplierName, suppliername);
        setTooltip(totalAmount, totalamount.toString());
        setTooltip(paidAmount, paidamount.toString());
        setTooltip(balAmount, balamount.toString());
        setTooltip(dueDate, duedate.toString());
    }

    private void  setTooltip(Label label, String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(label, tooltip);
    }

    @FXML
    private void handleItemClick() {
        try {
            String orderNumber = orderNo.getText();
            String suppliername = supplierName.getText();
            String totalamount = totalAmount.getText();
            String paidamount = paidAmount.getText();
            String balamount = balAmount.getText();
            String duedate = dueDate.getText();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("view-ordered-items.fxml"));
            Parent root = loader.load();

            // Get controller of the new FXML file
            ViewOrderedItemsController controller = loader.getController();

            // Pass data to the new controller
            controller.setOrderDetails(orderNumber, suppliername, paidamount, balamount, totalamount, duedate);
            controller.loadOrderedItemsData(orderNumber);

            // Show new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 515));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
