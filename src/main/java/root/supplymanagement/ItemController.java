package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemController {
    @FXML
    private Label itemNameLB;
    public void setItem(String itemName){
        itemNameLB.setText(itemName);
    }
}
