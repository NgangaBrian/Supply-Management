package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OrderedProductItemController {
    @FXML
    private Label productName, itemAmount;
    @FXML
    private Button plusBtn, minusBtn;
    @FXML
    private TextField productQuantity;
    @FXML
    private ImageView removeProduct;

    private Runnable onRemove; // Callback for removal
    private double unitPrice = 1.0;

    public void initialize() {
        loadImages();

        removeProduct.setOnMouseClicked(event -> {
            if (onRemove != null) {
                onRemove.run(); // Call the removal function
            }
        });

        //✅ Listener to update item amount when quantity is typed
        productQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                productQuantity.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }

            try {
                int qty = Integer.parseInt(newValue);
                if (qty <= 0) {
                    productQuantity.setText("1");
                    qty = 1;
                }
                itemAmount.setText(String.format("%.2f", qty * unitPrice));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                itemAmount.setText("0.00");
            }
        });
    }

    public void addQuantity(){
        try {
            int quantity = Integer.parseInt(productQuantity.getText());
            int updatedQuantity = quantity + 1;
            productQuantity.setText(String.valueOf(updatedQuantity));
            itemAmount.setText(String.format("%.2f", updatedQuantity * unitPrice));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            productQuantity.setText("1");
            itemAmount.setText(String.format("%.2f", unitPrice));
        }
    }
    public void minusQuantity(){
        try {
        int quantity = Integer.parseInt(productQuantity.getText());
        if (quantity > 1) {
            int updatedQuantity = quantity - 1;
            productQuantity.setText(String.valueOf(updatedQuantity));
            itemAmount.setText(String.format("%.2f", updatedQuantity * unitPrice));
        } else {
            // Call the removal function if it's set
            if (onRemove != null) {
                onRemove.run();
            }
        }} catch (NumberFormatException e){
            e.printStackTrace();
            productQuantity.setText("1");
            itemAmount.setText(String.format("%.2f", unitPrice));
        }
    }

    public String getProductName() {
        return productName.getText();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        try {
            return Integer.parseInt(productQuantity.getText());
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if input is invalid
        }
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    public void setProductName(String name, Double amount) {
        productName.setText(name);
        this.unitPrice = amount;
        itemAmount.setText(String.format("%.2f", amount));
        productQuantity.setText("1"); // start from quantity 1
    }

    private void loadImages() {
        Image deleteImage = new Image(getClass().getResource("/Images/deleteBtn.png").toExternalForm());
        removeProduct.setImage(deleteImage);
    }
}
