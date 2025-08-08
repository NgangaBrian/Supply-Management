package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceItemController {

    @FXML
    private Label invoiceNoLB, supplierNameLB, paidAmountLB, balanceLB, dueDateLB;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    public void setInvoiceData(String invoiceNo, String supplierName, Double paidAmount, String currency, Double balance, Date dueDate) {
        invoiceNoLB.setText(invoiceNo != null ? invoiceNo : "-");
        supplierNameLB.setText(supplierName != null ? supplierName : "-");

        paidAmountLB.setText(currency + " " + String.valueOf(moneyFormat.format(paidAmount)));
        balanceLB.setText(currency + " " + String.valueOf(moneyFormat.format(balance)));
        dueDateLB.setText(dueDate != null ? dateFormat.format(dueDate) : "N/A");
    }
}
