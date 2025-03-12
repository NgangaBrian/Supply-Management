package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Date;

public class InvoiceItemController {
    @FXML
    private Label invoiceNoLB, supplierNameLB, paidAmountLB, balanceLB, dueDateLB;

    public void setInvoiceData(String invoiceNo, String supplierName, Double paidAmount, Double balance, Date dueDate) {
        invoiceNoLB.setText(invoiceNo);
        supplierNameLB.setText(supplierName);
        paidAmountLB.setText(String.valueOf(paidAmount));
        balanceLB.setText(String.valueOf(balance));
        dueDateLB.setText(String.valueOf(dueDate));
    }
}
