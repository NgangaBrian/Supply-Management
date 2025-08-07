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

    public void setInvoiceData(String invoiceNo, String supplierName, Double paidAmount, Double balance, Date dueDate) {
        invoiceNoLB.setText(invoiceNo != null ? invoiceNo : "-");
        supplierNameLB.setText(supplierName != null ? supplierName : "-");

        paidAmountLB.setText(paidAmount != null ? moneyFormat.format(paidAmount) : "0.00");
        balanceLB.setText(balance != null ? moneyFormat.format(balance) : "0.00");
        dueDateLB.setText(dueDate != null ? dateFormat.format(dueDate) : "N/A");
    }
}
