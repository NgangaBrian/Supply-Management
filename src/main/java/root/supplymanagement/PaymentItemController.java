package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Date;

public class PaymentItemController {
    @FXML
    private Label orderNo, supplierName, paidAmount, paymentMethod, paymentCode, datePaid, additionalNotes;


    public void setPaymentData(String orderNumber, String suppliersName, double paidAmountt, String paymentsMethod,
                               String paymentsCode, Date datesPaid, String notes) {
        orderNo.setText(orderNumber);
        supplierName.setText(suppliersName);
        paidAmount.setText(String.valueOf(paidAmountt));
        paymentMethod.setText(paymentsMethod);
        paymentCode.setText(paymentsCode);
        datePaid.setText(String.valueOf(datesPaid));
        additionalNotes.setText(notes);

    }
}
