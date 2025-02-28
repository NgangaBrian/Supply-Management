package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.time.LocalDate;

public class OrdersItemController {
    @FXML
    private Label orderNo, supplierName, totalAmount, paidAmount, balAmount, dueDate;
    @FXML

    public void setOrderData(String orderno, String suppliername, Double totalamount, Double paidamount,
                                Double balamount, LocalDate duedate) {
        orderNo.setText(orderno);
        supplierName.setText(suppliername);
        totalAmount.setText(totalamount.toString());
        paidAmount.setText(paidamount.toString());
        balAmount.setText(balamount.toString());
        dueDate.setText(duedate.toString());

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
}
