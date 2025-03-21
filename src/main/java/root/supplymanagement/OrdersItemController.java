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
import java.time.LocalDate;

public class OrdersItemController {
    @FXML
    private Label orderNo, supplierName, totalAmount, paidAmount, balAmount, dueDate;
    @FXML
    private HBox supplietItemView;

    public void setOrderData(String orderno, String suppliername, Double totalamount, Double paidamount,
                                Double balamount, LocalDate duedate) {
        orderNo.setText(orderno);
        supplierName.setText(suppliername);
        totalAmount.setText(totalamount.toString());
        paidAmount.setText(paidamount.toString());
        balAmount.setText(balamount.toString());
        dueDate.setText(duedate.toString());

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
        System.out.println("HBox clicked! Opening view-ordered-items.fxml...");
        try {
            String orderNumber = orderNo.getText();
            String suppliername = supplierName.getText();
            double totalamount = Double.parseDouble(totalAmount.getText());
            double paidamount = Double.parseDouble(paidAmount.getText());
            double balamount = Double.parseDouble(balAmount.getText());
            String duedate = dueDate.getText();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("view-ordered-items.fxml"));
            Parent root = loader.load();

            // Get controller of the new FXML file
            ViewOrderedItemsController controller = loader.getController();

            // Pass data to the new controller
            controller.setOrderDetails(orderNumber, suppliername, balamount, paidamount, totalamount, duedate);
            controller.loadOrderedItemsData(orderNumber);

            // Show new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 485));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
