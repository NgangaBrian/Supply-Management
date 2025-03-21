package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ViewOrderedProductItemController {
    @FXML
    private Label productNameLB, quantityLB;

    public void setOrderedProductdata(String productName, String quantity) {
        productNameLB.setText(productName);
        quantityLB.setText(quantity);
    }
}
