package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class PaymentItemController {
    @FXML
    private Label orderNo, supplierName, paidAmount, paymentMethod, paymentCode, datePaid, additionalNotes;


    public void setPaymentData(String orderNumber, String suppliersName, String paidAmountt, String paymentsMethod,
    String paymentsCode, String datesPaid, String notes) {
        orderNo.setText(orderNumber);
        supplierName.setText(suppliersName);
        paidAmount.setText(paidAmountt);
        paymentMethod.setText(paymentsMethod);
        paymentCode.setText(paymentsCode);
        datePaid.setText(datesPaid);
        additionalNotes.setText(notes);

    }
}
