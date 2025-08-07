package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class PaymentItemController {
    @FXML
    private Label orderNo, supplierName, paidAmount, paymentMethod, paymentCode, datePaid, additionalNotes;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");


    public void setPaymentData(String orderNumber, String suppliersName, double paidAmountt, String currency, String paymentsMethod,
                               String paymentsCode, Date datesPaid, String notes) {
        orderNo.setText(orderNumber);
        supplierName.setText(suppliersName);
        paidAmount.setText(currency + " " + String.valueOf(moneyFormat.format(paidAmountt)));
        paymentMethod.setText(paymentsMethod);
        paymentCode.setText(paymentsCode);
        datePaid.setText(String.valueOf(dateFormat.format(datesPaid)));
        additionalNotes.setText(notes);

    }
}
