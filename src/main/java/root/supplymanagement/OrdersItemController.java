package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class OrdersItemController {
    @FXML
    private Label orderNo, supplierName, totalAmount, paidAmount, balAmount, dueDate;
    @FXML

    public void setOrderData(String orderno, String suppliername, String totalamount, String paidamount,
                                String balamount, String duedate) {
        orderNo.setText(orderno);
        supplierName.setText(suppliername);
        totalAmount.setText(totalamount);
        paidAmount.setText(paidamount);
        balAmount.setText(balamount);
        dueDate.setText(duedate);
    }
}
