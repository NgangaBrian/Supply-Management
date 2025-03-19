package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OrderedProductItemController {
    @FXML
    private Label productName;
    @FXML
    private Button plusBtn, minusBtn;
    @FXML
    private TextField productQuantity;
    @FXML
    private ImageView removeProduct;

    private Runnable onRemove; // Callback for removal

    public void initialize() {
        loadImages();
        removeProduct.setOnMouseClicked(event -> {
            if (onRemove != null) {
                onRemove.run(); // Call the removal function
            }
        });
    }

    public void addQuantity(){
        int quantity = Integer.parseInt(productQuantity.getText());
        productQuantity.setText(String.valueOf(quantity + 1));
    }
    public void minusQuantity(){
        int quantity = Integer.parseInt(productQuantity.getText());
        if (quantity > 1) {
            productQuantity.setText(String.valueOf(quantity - 1));
        } else {
            // Call the removal function if it's set
            if (onRemove != null) {
                onRemove.run();
            }
        }
    }

    public String getProductName() {
        return productName.getText();
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

    public void setProductName(String name) {
        productName.setText(name);
    }

    private void loadImages() {
        Image deleteImage = new Image(getClass().getResource("/Images/deleteBtn.png").toExternalForm());
        removeProduct.setImage(deleteImage);
    }
}
